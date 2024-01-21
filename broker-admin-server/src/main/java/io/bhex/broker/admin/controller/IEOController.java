package io.bhex.broker.admin.controller;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import com.google.gson.Gson;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.config.AwsPublicStorageConfig;
import io.bhex.broker.admin.constants.StorageConstants;
import io.bhex.broker.admin.controller.dto.ActivityInfoDTO;
import io.bhex.broker.admin.controller.dto.ActivityProfileDTO;
import io.bhex.broker.admin.controller.dto.ActivityPurchaseInfoDTO;
import io.bhex.broker.admin.controller.dto.IEOUploadDTO;
import io.bhex.broker.admin.controller.dto.IEOWhiteListDTO;
import io.bhex.broker.admin.controller.dto.LockInterestOrderInfoDto;
import io.bhex.broker.admin.controller.dto.PageResultDTO;
import io.bhex.broker.admin.controller.dto.TokenDTO;
import io.bhex.broker.admin.controller.param.ActivityInfoPO;
import io.bhex.broker.admin.controller.param.CalculateActivityResultPO;
import io.bhex.broker.admin.controller.param.IEOOnlineStatusPO;
import io.bhex.broker.admin.controller.param.IEOProjectListPO;
import io.bhex.broker.admin.controller.param.IEOProjectPO;
import io.bhex.broker.admin.controller.param.IEOWhiteListPO;
import io.bhex.broker.admin.controller.param.IdPO;
import io.bhex.broker.admin.controller.param.QueryActivityOrderPO;
import io.bhex.broker.admin.service.ActivityService;
import io.bhex.broker.admin.service.TokenService;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.common.objectstorage.CannedAccessControlList;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import io.bhex.broker.common.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/activity/ieo")
public class IEOController extends BaseController {

    @Resource
    private TokenService tokenService;

    @Resource
    private ActivityService activityService;
    @Resource(name = "awsPublicStorageConfig")
    private AwsPublicStorageConfig awsPublicStorageConfig;
    @Resource(name = "objecPublictStorage")
    private ObjectStorage awsPublicObjectStorage;

    @RequestMapping(value = "/project/create", method = RequestMethod.POST)
    public ResultModel<Long> createProject(@RequestBody @Valid IEOProjectPO project) {
        try {
            project.validProject();
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
            return ResultModel.error(e.getMessage());
        }
        Long orgId = getOrgId();
        project.setPurchaseTokenName("");
        project.setOfferingsTokenName("");
        return activityService.createIEOProject(orgId, project);
    }


    @RequestMapping(value = "/project/get")
    public ResultModel<IEOProjectPO> findProject(@RequestBody IdPO idObject) {

        Long id = idObject.getId();
        IEOProjectPO project = activityService.findActivity(id);
        return ResultModel.ok(project);
    }


    @RequestMapping(value = "/project/list")
    public ResultModel<PageResultDTO<List<ActivityProfileDTO>>> listIEOProject(@RequestBody IEOProjectListPO param) {

        Long brokerId = getOrgId();
        Integer pageNo = param.getPageNo();
        Integer size = param.getSize();
        String language = LocaleUtil.getLanguage();
        List<Integer> types = Lists.newArrayList(2, 3);
        Pair<List<ActivityProfileDTO>, Integer> pair = activityService.listActivity(brokerId, pageNo, size, types, language);
        List<ActivityProfileDTO> list = pair.getLeft();
        BigDecimal[] remainder = new BigDecimal(pair.getRight()).divideAndRemainder(new BigDecimal(size));

        int totalPageNumber = remainder[0].intValue();
        if (remainder[1].compareTo(BigDecimal.ZERO) > 0) {
            totalPageNumber += 1;
        }

        PageResultDTO<List<ActivityProfileDTO>> result = PageResultDTO.<List<ActivityProfileDTO>>builder()
                .list(list)
                .nextPage(totalPageNumber > pageNo)
                .build();
        return ResultModel.ok(result);

    }

    public ResultModel<Boolean> publishProject(Long projectId) {
        return null;
    }

    @RequestMapping(value = "/tokens")
    public ResultModel<List<TokenDTO>> listToken() {
        Long orgId = getOrgId();
        List<TokenDTO> list = tokenService.listTokenByOrgId(orgId, 1);
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/project/result/calculate")
    public ResultModel<ActivityPurchaseInfoDTO> calculateActivityResult(@RequestBody @Valid CalculateActivityResultPO resultPO) {

        Long projectId = resultPO.getProjectIdNumber();
        Long brokerId = getOrgId();
        String language = LocaleUtil.getLanguage();
        String actualOfferingsVolume = resultPO.getActualOfferingsVolume();
        ActivityPurchaseInfoDTO result = activityService.calculateActivityResult(projectId, brokerId, language, actualOfferingsVolume);
        return ResultModel.ok(result);

    }

    @RequestMapping(value = "/project/result/get")
    public ResultModel<ActivityPurchaseInfoDTO> getActivityResult(@RequestBody IdPO idPO) {

        Long projectId = idPO.getId();
        Long brokerId = getOrgId();
        String language = LocaleUtil.getLanguage();
        ActivityPurchaseInfoDTO dto = activityService.findActivityResult(projectId, brokerId, language);
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/project/result/confirm")
    public ResultModel<String> confirm(@RequestBody IdPO idPO) {
        Long projectId = idPO.getId();
        Long brokerId = getOrgId();
        String language = LocaleUtil.getLanguage();
        return activityService.confirmResult(language, projectId, brokerId);
    }

    @RequestMapping(value = "/project/online")
    public ResultModel<Boolean> queryOnlineStatus(@RequestBody IEOOnlineStatusPO po) {

        Long projectId = po.getProjectId();
        Long brokerId = getOrgId();
        if (po.getProjectId() == null || po.getIsShow() == null || po.getIsShow() > 1) {
            return ResultModel.error("Fail");
        }
        boolean success = activityService.onlineStatus(projectId, brokerId, po.getIsShow());
        if (success) {
            return ResultModel.ok();
        } else {
            return ResultModel.error("Project status not allow update");
        }
    }

    @RequestMapping(value = "/order/list")
    public ResultModel<List<LockInterestOrderInfoDto>> queryAdminQueryAllActivityOrderInfo(@RequestBody QueryActivityOrderPO po) {
        Long brokerId = getOrgId();
        String language = LocaleUtil.getLanguage();
        po.setOrgId(brokerId);
        po.setLanguage(language);
        return ResultModel.ok(activityService.adminQueryAllActivityOrderInfo(po));
    }

    @RequestMapping(value = "/query/whitelist")
    public ResultModel<IEOWhiteListDTO> queryIeoWhiteList(@RequestBody IEOWhiteListPO po) {
        Long brokerId = getOrgId();
        po.setBrokerId(brokerId);
        return activityService.queryIeoWhiteList(po);
    }

    @RequestMapping(value = "/save/whitelist")
    public ResultModel saveIeoWhiteList(@RequestBody IEOWhiteListPO po) {
        Long brokerId = getOrgId();
        po.setBrokerId(brokerId);
        return activityService.saveIeoWhiteList(po);
    }

    @GetMapping("/download/order")
    public void download(@RequestParam("projectId") String projectId,
                         @RequestParam("type") Integer type,
                         HttpServletResponse response) throws IOException {
        log.info("ieo download order projectId {} type {}", projectId, type);
        Long brokerId = getOrgId();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("IEO订单");
        setTitle(workbook, sheet, type);
        List<LockInterestOrderInfoDto> list = new ArrayList<>();
        Long fromId = 0L;
        while (true) {
            QueryActivityOrderPO activityOrderPO = new QueryActivityOrderPO();
            activityOrderPO.setProjectId(Long.parseLong(projectId));
            activityOrderPO.setLimit(2000);
            activityOrderPO.setFromId(fromId);
            activityOrderPO.setOrgId(brokerId);
            List<LockInterestOrderInfoDto> lockInterestOrderInfoDtoList
                    = activityService.adminQueryAllActivityOrderInfo(activityOrderPO);
            if (CollectionUtils.isNotEmpty(lockInterestOrderInfoDtoList)) {
                list.addAll(lockInterestOrderInfoDtoList);
                fromId = Long.parseLong(lockInterestOrderInfoDtoList.get(lockInterestOrderInfoDtoList.size() - 1).getOrderId());
            } else {
                break;
            }
        }

        int rowNum = 1;
        for (LockInterestOrderInfoDto infoDto : list) {
            HSSFRow row = sheet.createRow(rowNum);
            if (type.equals(2)) {
                row.createCell(0).setCellValue(infoDto.getMappingId());
            } else {
                row.createCell(0).setCellValue(infoDto.getPurchaseTime());
            }
            row.createCell(1).setCellValue(infoDto.getUserId());
            row.createCell(2).setCellValue(infoDto.getOrderId());
            row.createCell(3).setCellValue(infoDto.getProjectName());
            row.createCell(4).setCellValue(infoDto.getAmount() + "/" + infoDto.getPurchaseTokenName());
            row.createCell(5).setCellValue(infoDto.getReceiveTokenQuantity() + "/" + infoDto.getReceiveTokenName());
            if (type.equals(2)) {
                row.createCell(6).setCellValue(infoDto.getUseAmount() + "/" + infoDto.getPurchaseTokenName());
                row.createCell(7).setCellValue(infoDto.getBackAmount() + "/" + infoDto.getPurchaseTokenName());
            }
            rowNum++;
        }
        String fileName = "orders.xlsx";
        response.reset();
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        response.setContentType(com.google.common.net.MediaType.MICROSOFT_EXCEL.toString());
        workbook.write(os);
        os.flush();
        os.close();
    }

    /***
     * 设置表头
     * @param workbook
     * @param sheet
     */
    private void setTitle(HSSFWorkbook workbook, HSSFSheet sheet, Integer type) {
        HSSFRow row = sheet.createRow(0);
        sheet.setColumnWidth(0, 10 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 20 * 256);
        sheet.setColumnWidth(4, 20 * 256);
        sheet.setColumnWidth(5, 20 * 256);
        if (type.equals(2)) {
            sheet.setColumnWidth(6, 20 * 256);
            sheet.setColumnWidth(7, 20 * 256);
        }
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        HSSFCell cell;
        if (type.equals(2)) {
            cell = row.createCell(0);
            cell.setCellValue("ID");
            cell.setCellStyle(style);
        } else {
            cell = row.createCell(0);
            cell.setCellValue("时间");
            cell.setCellStyle(style);
        }

        cell = row.createCell(1);
        cell.setCellValue("UID");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("订单号");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("项目");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("申购金额");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("获取数量");
        cell.setCellStyle(style);

        if (type.equals(2)) {
            cell = row.createCell(6);
            cell.setCellValue("消耗金额");
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue("返还金额");
            cell.setCellStyle(style);
        }
    }

    @RequestMapping(value = "/activity/upload", method = RequestMethod.POST)
    public ResultModel modifyActivityOrderInfo(@RequestParam(name = "uploadFile") MultipartFile uploadFile,
                                               @RequestParam(value = "id", required = true, defaultValue = "0") Long id,
                                               @RequestParam(value = "requestId", required = true, defaultValue = "0") Long requestId) throws Exception {
        long orgId = getOrgId();
        String fileType = FileUtil.getFileSuffix(uploadFile.getOriginalFilename(), "");
        if (Strings.isNullOrEmpty(fileType) || !StorageConstants.TEXT_FILE_TYPES.contains(fileType)) {
            throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
        }
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(uploadFile.getInputStream());
        } catch (NotOfficeXmlFileException e) {
            try {
                workbook = new HSSFWorkbook(uploadFile.getInputStream());
            } catch (Exception exception) {
                log.warn("invalid OOXML file", e);
                throw new BrokerException(BrokerErrorCode.UNSUPPORTED_FILE_TYPE);
            }
        }
        List<IEOUploadDTO> list = new ArrayList<>();
        try {
            list = this.buildModel(workbook);
        } catch (Exception ex) {
            log.info("modifyActivityOrderInfo buildModel fail {}", ex);
            return ResultModel.error("ieo.activity.failed.upload.data");
        }
        if (org.springframework.util.CollectionUtils.isEmpty(list)) {
            return ResultModel.error("ieo.activity.upload.data.empty");
        }
        String fileKey = "bhop/ieo/" + System.nanoTime() + "_" + orgId + "_" + id + ".xlsx";
        awsPublicObjectStorage.uploadObject(fileKey, MediaType.MICROSOFT_EXCEL, uploadFile.getInputStream(), CannedAccessControlList.PublicRead);
        String url = awsPublicStorageConfig.getStaticUrl() + fileKey;
        try {
            return this.activityService.modifyActivityOrderInfo(orgId, id, url, list);
        } catch (Exception ex) {
            log.info("Modify activity order info fail {}", ex);
            return ResultModel.error("ieo.activity.failed.upload.data");
        }
    }

    public List<IEOUploadDTO> buildModel(Workbook workbook) {
        List<String> titles = new ArrayList<>();
        Sheet sheetAt = workbook.getSheetAt(0);
        List<IEOUploadDTO> ieoUploadDTOList = new ArrayList<>();
        for (Row row : sheetAt) {
            if (row.getRowNum() == 0) {
                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    String column = row.getCell(i).toString().trim();
                    titles.add(column);
                }
            } else {
                Cell idCell = row.getCell(titles.indexOf(IEOUploadDTO.ID));
                String id = idCell != null ? idCell.toString().trim().replaceAll("\t", "") : "";
                if (StringUtils.isEmpty(id)) {
                    continue;
                }
                Cell userIdCell = row.getCell(titles.indexOf(IEOUploadDTO.USER_ID));
                String userId = userIdCell != null ? userIdCell.toString().trim().replaceAll("\t", "") : "";
                Cell orderIdCell = row.getCell(titles.indexOf(IEOUploadDTO.ORDER_ID));
                String orderId = orderIdCell != null ? orderIdCell.toString().trim().replaceAll("\t", "") : "";
                Cell amountCell = row.getCell(titles.indexOf(IEOUploadDTO.AMOUNT));
                String amount = amountCell != null ? amountCell.toString().trim().replaceAll("\t", "") : "";
                Cell useAmountCell = row.getCell(titles.indexOf(IEOUploadDTO.USE_AMOUNT));
                String useAmount = useAmountCell != null ? useAmountCell.toString().trim().replaceAll("\t", "") : "";
                Cell luckyAmountCell = row.getCell(titles.indexOf(IEOUploadDTO.LUCKY_AMOUNT));
                String luckyAmount = luckyAmountCell != null ? luckyAmountCell.toString().trim().replaceAll("\t", "") : "";
                Cell backAmountCell = row.getCell(titles.indexOf(IEOUploadDTO.BACK_AMOUNT));
                String backAmount = backAmountCell != null ? backAmountCell.toString().trim().replaceAll("\t", "") : "";
                String newAmount = "0";
                if (StringUtils.isNoneBlank(amount)) {
                    if (amount.contains("/")) {
                        String[] strings = amount.split("/");
                        if (strings.length == 2) {
                            newAmount = strings[0];
                        }
                    } else {
                        newAmount = new BigDecimal(amount).setScale(8, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                    }
                }

                String newUseAmount = "0";
                if (StringUtils.isNoneBlank(useAmount)) {
                    if (useAmount.contains("/")) {
                        String[] strings = useAmount.split("/");
                        if (strings.length == 2) {
                            newUseAmount = strings[0];
                        }
                    } else {
                        newUseAmount = new BigDecimal(useAmount).setScale(8, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                    }
                }

                String newLuckyAmount = "0";
                if (StringUtils.isNoneBlank(luckyAmount)) {
                    if (luckyAmount.contains("/")) {
                        String[] strings = luckyAmount.split("/");
                        if (strings.length == 2) {
                            newLuckyAmount = strings[0];
                        }
                    } else {
                        newLuckyAmount = new BigDecimal(luckyAmount).setScale(8, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                    }
                }

                String newBackAmount = "0";
                if (StringUtils.isNoneBlank(backAmount)) {
                    if (backAmount.contains("/")) {
                        String[] strings = backAmount.split("/");
                        if (strings.length == 2) {
                            newBackAmount = strings[0];
                        }
                    } else {
                        newBackAmount = new BigDecimal(backAmount).setScale(8, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                    }
                }

                IEOUploadDTO ieoUploadDTO = new IEOUploadDTO();
                ieoUploadDTO.setId(new BigDecimal(id).longValue());
                ieoUploadDTO.setUserId(new BigDecimal(userId).longValue());
                ieoUploadDTO.setOrderId(new BigDecimal(orderId).longValue());
                ieoUploadDTO.setAmount(newAmount);
                ieoUploadDTO.setUseAmount(newUseAmount);
                ieoUploadDTO.setLuckyAmount(newLuckyAmount);
                ieoUploadDTO.setBackAmount(newBackAmount);
                ieoUploadDTOList.add(ieoUploadDTO);
            }
        }
        log.info("ieoUploadDTOList {}", new Gson().toJson(ieoUploadDTOList));
        return ieoUploadDTOList;
    }

    public static void main(String[] args) {
        String str = "111/USDT";
        String[] strings = str.split("/");
        System.out.println(strings.length);
        System.out.println(strings[0]);
        System.out.println(strings[1]);
    }

    @RequestMapping(value = "/query/activity/info")
    public ResultModel<ActivityInfoDTO> findProject(@RequestBody ActivityInfoPO activityInfoPO) {
        long orgId = getOrgId();
        ActivityInfoDTO activityInfoDTO = activityService.queryActivityProjectInfo(orgId, activityInfoPO.getProjectId());
        return ResultModel.ok(activityInfoDTO);
    }
}
