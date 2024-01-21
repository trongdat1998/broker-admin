package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import io.bhex.base.account.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.grpc.client.AdminUserClient;
import io.bhex.bhop.common.service.AdminLoginUserService;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.config.AwsPublicStorageConfig;
import io.bhex.broker.admin.constants.StorageConstants;
import io.bhex.broker.admin.controller.dto.AirdropDTO;
import io.bhex.broker.admin.controller.dto.AirdropTmplDTO;
import io.bhex.broker.admin.controller.dto.TokenDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.service.AirdropService;
import io.bhex.broker.admin.service.OrgAccountService;
import io.bhex.broker.admin.service.TokenService;
import io.bhex.broker.admin.util.NumberUtil;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.common.objectstorage.CannedAccessControlList;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import io.bhex.broker.common.util.FileUtil;
import io.bhex.broker.grpc.admin.AirdropInfo;
import io.bhex.broker.grpc.admin.TmplRecord;
import io.bhex.broker.grpc.admin.TokenDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.controller
 * @Author: ming.xu
 * @CreateDate: 07/11/2018 5:56 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/airdrop")
public class AirdropController extends BrokerBaseController {

    @Autowired
    private AirdropService airdropService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdminLoginUserService adminLoginUserService;

    @Autowired
    private OrgAccountService orgAccountService;

    @Resource(name = "awsPublicStorageConfig")
    private AwsPublicStorageConfig awsPublicStorageConfig;
    @Resource(name = "objecPublictStorage")
    private ObjectStorage awsPublicObjectStorage;
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AdminUserClient adminUserClient;

    public static final String SEQUENCE_KEY = "airdrop.seq.";


    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResultModel queryAirdrop(@RequestBody @Valid QueryAirdropPO param, AdminUserReply adminUser) {
        Long brokerId = adminUser.getOrgId();
        param.setBrokerId(brokerId);
        List<AirdropDTO> airdropDTOs = airdropService.queryAirdropInfo(param);
        return ResultModel.ok(airdropDTOs);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResultModel queryAirdrop(@RequestBody @Valid IdPO param) {
        AirdropInfo airdropInfo = airdropService.getAirdropInfo(param.getId(), getOrgId());
        AirdropDTO dto = new AirdropDTO();
        BeanUtils.copyProperties(airdropInfo, dto);
//        AdminUserReply adminUser = adminUserClient.getAdminUserById(airdropInfo.getAdminId());
//        if (null != adminUser) {
//            dto.setAdminId(adminUser.getEmail());
//        }
        dto.setAirdropTokenNum(new BigDecimal(airdropInfo.getAirdropTokenNum()));
        dto.setHaveTokenNum(new BigDecimal(airdropInfo.getHaveTokenNum()));
        dto.setUserAccountIds(airdropInfo.getUserIds());
        dto.setTmplModel(StringUtils.isEmpty(airdropInfo.getTmplUrl()) ? 0 : 1);
        return ResultModel.ok(dto);
    }

    @BussinessLogAnnotation(opContent = "Create Airdrop title:{#param.title} type:{#param.type == 1 ? 'Free Airdrop' : 'Airdrop with token holding'}"
            + "{#param.tmplModel == 1 ? 'TemplateAirdrop' : ''}")
    @RequestMapping(method = RequestMethod.POST)
    public ResultModel createAirdrop(@RequestBody @Valid AirdropPO param, AdminUserReply adminUser) {
        Long brokerId = adminUser.getOrgId();

        // 二次校验
        adminLoginUserService.verifyAdvance(param.getAuthType(), param.getVerifyCode(), adminUser.getId(), brokerId, getAdminPlatform());

        param.setBrokerId(brokerId);
        param.setLockModel(param.getLockModel()); //锁仓功能还没开放 不能用
        if (param.getTmplModel() != null  && param.getTmplModel() == 1) {
            param.setUserType(AirdropPO.USER_TYPE_SPECIAL);
            param.setTmplUrl(redisTemplate.opsForValue().get(SEQUENCE_KEY + param.getSequenceId()));
        } else {
            param.setTmplModel(0);

            if (StringUtils.isEmpty(param.getAirdropTokenId())) {
                return ResultModel.errorParameter("airdropTokenId", ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
            }

            if (param.getType() == AirdropPO.AIRDROP_TYPE_FORK && StringUtils.isEmpty(param.getHaveTokenId())) {
                return ResultModel.errorParameter("airdropTokenId", ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
            }
        }
        // 目前只用 运营账户 向用户转钱
        Long accountId = orgAccountService.getOrgAccountIdByType(brokerId, AccountType.OPERATION_ACCOUNT);

        // 目前兑换比例，用户持有token数固定为1
        //param.setHaveTokenNum(new BigDecimal(1));

        // 空投时间如果小于当前时间，则变更为当前系统时间
        param.setAirdropTime(System.currentTimeMillis() >= param.getAirdropTime() ? System.currentTimeMillis() : param.getAirdropTime());
        if (null != accountId && accountId != 0L) {
            param.setAccountId(accountId);
        } else {
            return ResultModel.error("airdrop.org.account.not.exist");
        }

        Combo2<Boolean, List<Long>> combo2 = airdropService.createAirdrop(param, adminUser);
        if (combo2.getV1()) {
            return ResultModel.ok(true);
        } else {
            List<Long> errorUserIds = combo2.getV2();
            if (!CollectionUtils.isEmpty(errorUserIds)) {
                Map<String, Object> result = new HashMap<>();
                result.put("errorUserIds", errorUserIds);
                return ResultModel.error(ErrorCode.AIRDROP_WRONG_USERID_ERROR.getCode(),
                        ErrorCode.AIRDROP_WRONG_USERID_ERROR.getDesc(), result);
            } else {
                return ResultModel.error("internal.error");
            }

        }
    }

    @BussinessLogAnnotation(opContent = "Retry Airdrop")
    @RequestMapping(value = "/retry", method = RequestMethod.POST)
    public ResultModel retry(@RequestBody @Valid AirdropRetryPO param) {
        Long brokerId = getOrgId();
        Boolean isOk = airdropService.retryAirdrop(param.getAirdropId(), brokerId);
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }


    @RequestMapping(value = "/init_info", method = RequestMethod.POST)
    public ResultModel getInitInfo() {
        Map<String, Long> result = new HashMap<>();
        result.put("systemTime", System.currentTimeMillis());
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/token_list", method = RequestMethod.POST)
    public ResultModel getTokenList() {
        Long brokerId = getOrgId();
        //
        List<TokenDTO> result = tokenService.queryTokenByBrokerId(1, 500, brokerId);
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public ResultModel closeAirdrop(@RequestBody UpdateAirdropPO param) {
        Boolean isOk = airdropService.closeAirdrop(param.getAirdropId(), getOrgId());
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }


    @RequestMapping(value = "/file/text", method = RequestMethod.POST)
    public ResultModel uploadText(@RequestParam(name = "uploadFile") MultipartFile uploadImageFile,
                                  @RequestParam(value = "echoStr", required = false, defaultValue = "") String echoStr,
                                  @RequestParam(value = "type", required = true, defaultValue = "1") int type) throws Exception {

        long orgId = getOrgId();
        String fileType = FileUtil.getFileSuffix(uploadImageFile.getOriginalFilename(), "");
        if (Strings.isNullOrEmpty(fileType) || !StorageConstants.TEXT_FILE_TYPES.contains(fileType)) {
            throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(uploadImageFile.getInputStream());
        } catch (NotOfficeXmlFileException e) {
            log.warn("invalid OOXML file", e);
            throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
        }


        Map<String, Object> result = new HashMap<>();
        result.put("tmplOk", true);

        String suffix = fileType.toLowerCase();
        String fileKey = "bhop/airdrop/" + System.nanoTime() + ".xlsx";

        List<AirdropTmplDTO> list = airdropService.convertTmpl(workbook);
        if (CollectionUtils.isEmpty(list)) {
            result.put("success", false);
            return ResultModel.ok(result);
        }

        List<String> errorUserIds = new ArrayList<>();
        for (AirdropTmplDTO dto : list) {
            if (!NumberUtil.isLong(dto.getUserId())) {
                errorUserIds.add(dto.getUserId());
            }
        }
        List<Long> userIds = list.stream()
                .filter(d -> NumberUtil.isLong(d.getUserId()))
                .map(d -> Long.parseLong(d.getUserId()))
                .collect(Collectors.toList());
        errorUserIds.addAll(
                getErrorUserIds(orgId, userIds)
                        .stream().map(s -> s + "")
                        .collect(Collectors.toList())
        );
        log.info("error userids : {}", errorUserIds);

        boolean forkAirdrop = !StringUtils.isEmpty(list.get(0).getHaveTokenId());

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
        XSSFDataFormat format = xssfWorkbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("@"));

        XSSFSheet sheet = xssfWorkbook.createSheet();
        List<String> headers = new ArrayList<>();
        headers.add(AirdropTmplDTO.LINE_ID_COLUMN);
        headers.add(AirdropTmplDTO.UID_COLUMN);
        headers.add(AirdropTmplDTO.TOKEN_COLUMN);
        headers.add(AirdropTmplDTO.QUANTITY_COLUMN);
        if (forkAirdrop) {
            headers.add(AirdropTmplDTO.HAVE_TOKEN_COLUMN);
            headers.add(AirdropTmplDTO.HAVE_QUANTITY_COLUMN);
        }
        headers.add("数据是否有效");
        XSSFRow titlerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            titlerRow.createCell(i).setCellValue(headers.get(i));
        }

        Map<String, Boolean> tokenExistMap = new HashMap<>();
        boolean hasErrorData = false;
        for (int i = 0; i <  list.size(); i++) {
            AirdropTmplDTO dto = list.get(i);
            log.info("i:{} data:{}", i, dto);
            List<String> columns = new ArrayList<>();
            columns.add((i + 1) + "");
            columns.add(dto.getUserId() + "");
            columns.add(dto.getAirdropTokenId().toUpperCase());
            columns.add(dto.getAirdropTokenNum());
            if (forkAirdrop) {
                columns.add(dto.getHaveTokenId().toUpperCase());
                columns.add(dto.getAirdropTokenNum());
            }

            String error = "";

            if (!NumberUtil.isNumber(dto.getAirdropTokenNum()) || new BigDecimal(dto.getAirdropTokenNum()).compareTo(BigDecimal.ZERO) <= 0) {
                error += "NumError";
                hasErrorData = true;
            }

            if (errorUserIds.contains(dto.getUserId())) {
                error += " UIDError";
                hasErrorData = true;
            }

            String tokenId = dto.getAirdropTokenId().toUpperCase();
            if (!tokenExistMap.containsKey(tokenId)) {
                TokenDetail tokenDetail = airdropService.getBrokerTokenDetail(tokenId, orgId);
                tokenExistMap.put(tokenId, tokenDetail != null && tokenDetail.getExchangeId() > 0);
            }
            if (!tokenExistMap.get(tokenId)) {
                error += " TokenError";
                hasErrorData = true;
            }


            if (!StringUtils.isEmpty(error)) {
                columns.add(error);
            } else {
                columns.add("OK");
            }




            int lastRowNum = sheet.getLastRowNum();
            XSSFRow dataRow = sheet.createRow(lastRowNum + 1);
            for (int j = 0; j < columns.size(); j++) {
                XSSFCell cell = dataRow.createCell(j, CellType.STRING);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(columns.get(j));
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            xssfWorkbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);

        awsPublicObjectStorage.uploadObject(fileKey,  MediaType.MICROSOFT_EXCEL, is, CannedAccessControlList.PublicRead);
        String url = awsPublicStorageConfig.getStaticUrl() + fileKey;
        log.info("url:{}", url);

        long sequenceId = System.currentTimeMillis();
        redisTemplate.opsForValue().set(SEQUENCE_KEY + sequenceId, fileKey, 12, TimeUnit.HOURS);

        if (hasErrorData) {
            result.put("success", false);
            result.put("errorUrl", url);
        } else {
            result.put("success", true);
            result.put("sequenceId", sequenceId + "");
        }
        result.put("echoStr", echoStr);
        return ResultModel.ok(result);
    }


    @RequestMapping(value = "/download_transfer_record", produces = {"text/plain"})
    public void downloadTransferData(
            //@RequestBody @Valid IdPO po,
                             @RequestParam(value = "id") long id,
                             HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        long airdropId = id;
        AirdropInfo airdropInfo = airdropService.getAirdropInfo(airdropId, getOrgId());

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet();

        List<TmplRecord> records = airdropService.listTmplRecords(getOrgId(), airdropId, -1, "", 1000000);

        List<String> headers = new ArrayList<>();
        headers.add(AirdropTmplDTO.LINE_ID_COLUMN);
        headers.add(AirdropTmplDTO.UID_COLUMN);
        headers.add(AirdropTmplDTO.TOKEN_COLUMN);
        headers.add(AirdropTmplDTO.QUANTITY_COLUMN);

        if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_FORK) {
            headers.add(AirdropTmplDTO.HAVE_TOKEN_COLUMN);
            headers.add(AirdropTmplDTO.HAVE_QUANTITY_COLUMN);
        }
        headers.add("发放状态");

        XSSFRow titlerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            titlerRow.createCell(i).setCellValue(headers.get(i));
        }

        for (TmplRecord record : records) {
            List<String> columns = new ArrayList<>();
            columns.add(record.getTmplLineId() + "");
            columns.add(record.getUserId() + "");
            columns.add(record.getTokenId());
            columns.add(record.getTokenAmount());
            if (airdropInfo.getType() == AirdropPO.AIRDROP_TYPE_FORK) {
                columns.add(record.getHaveTokenId());
                columns.add(record.getHaveTokenAmount());
            }
            columns.add(record.getStatus() == 1 ? "已发放" : "未发放");

            int lastRowNum = sheet.getLastRowNum();
            XSSFRow dataRow = sheet.createRow(lastRowNum + 1);
            for (int i = 0; i < columns.size(); i++) {
                dataRow.createCell(i).setCellValue(columns.get(i));
            }
        }

        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(getOrgId() + "_" + System.currentTimeMillis() + ".xlsx", "UTF-8"));
        response.setContentType(com.google.common.net.MediaType.MICROSOFT_EXCEL.toString());

        xssfWorkbook.write(response.getOutputStream());
        xssfWorkbook.close();

    }

}
