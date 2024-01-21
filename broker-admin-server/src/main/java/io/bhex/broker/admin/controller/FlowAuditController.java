package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.service.AdminLoginUserService;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.AirdropPO;
import io.bhex.broker.admin.model.AdminUser;
import io.bhex.broker.admin.service.AdminUserService;
import io.bhex.broker.admin.service.AirdropService;
import io.bhex.broker.admin.service.FlowAuditService;
import io.bhex.broker.grpc.admin.AirdropInfo;
import io.bhex.broker.grpc.admin.TmplRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * flow audit controller
 *
 * @author songxd
 * @date 2021-01-15
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/flow")
public class FlowAuditController extends BrokerBaseController {

    private final FlowAuditService flowAuditService;
    private final AdminLoginUserService adminLoginUserService;
    private final AdminUserService adminUserService;
    private final AirdropService airdropService;

    public FlowAuditController(FlowAuditService flowAuditService, AdminLoginUserService adminLoginUserService
            , AdminUserService adminUserService, AirdropService airdropService) {
        this.flowAuditService = flowAuditService;
        this.adminLoginUserService = adminLoginUserService;
        this.adminUserService = adminUserService;
        this.airdropService = airdropService;
    }

    /**
     * insert or update flow config
     *
     * @param dto flow and flow node info
     * @return 0=成功 1=param invalid 2=flow of biz type is exists 3=not allow modify 4=not allow forbidden
     */
    @RequestMapping(value = "/flow_config", method = RequestMethod.POST)
    public ResultModel setTokenConfig(@RequestBody FlowConfigDTO dto) {
        // 二次校验
        adminLoginUserService.verifyAdvance(dto.getAuthType(), dto.getVerifyCode(), getRequestUserId(), getOrgId(),  getAdminPlatform());
        String language = LocaleUtil.getLanguage();
        return flowAuditService.saveFlowConfig(dto, getRequestUser(), language);
    }

    /**
     * set flow forbidden
     *
     * @param dto dto
     * @return 0=成功 1=param invalid 2=not allow forbidden
     */
    @RequestMapping(value = "/flow_config/set_forbidden", method = RequestMethod.POST)
    public ResultModel setFlowForbidden(@RequestBody FlowSetForbiddenDTO dto) {
        // 二次校验
        adminLoginUserService.verifyAdvance(dto.getAuthType(), dto.getVerifyCode(), getRequestUserId(), getOrgId(), getAdminPlatform());
        return flowAuditService.setFlowForbidden(dto, getRequestUser());
    }

    /**
     * get flow config list
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/flow_config/list", method = RequestMethod.POST)
    public ResultModel getFlowConfigList(@RequestBody FlowConfigGetListDTO dto) {
        if (dto != null) {
            dto.setOrgId(getOrgId());
        }
        String language = LocaleUtil.getLanguage();
        return flowAuditService.getFlowConfigList(dto, language);
    }

    /**
     * get flow biz type list
     *
     * @return
     */
    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/flow_config/get_biz_types", method = RequestMethod.GET)
    public ResultModel getFlowBizTypeList(){
        String language = LocaleUtil.getLanguage();
        return flowAuditService.getFlowBizTypeList(getOrgId(), language);
    }

    /**
     * get flow config detail
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/flow_config/get_detail", method = RequestMethod.POST)
    public ResultModel getFlowConfigDetail(@RequestBody FlowGetDetailDTO dto) {
        if (dto != null) {
            dto.setOrgId(getOrgId());
        }
        return flowAuditService.getFlowConfigDetail(dto);
    }

    /**
     * get audit records
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/records/audit_list", method = RequestMethod.POST)
    public ResultModel getAuditRecordList(@RequestBody FlowGetRecordsDTO dto) {
        return flowAuditService.getAuditRecordList(dto, getRequestUser());
    }

    /**
     * get approved records
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/records/approved_list", method = RequestMethod.POST)
    public ResultModel getApprovedRecordList(@RequestBody FlowGetRecordsDTO dto) {
        return flowAuditService.getApprovedRecordList(dto, getRequestUser());
    }

    /**
     * get audit logs
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/records/audit/logs", method = RequestMethod.POST)
    public ResultModel getAuditLogList(@RequestBody FlowGetAuditLogDTO dto) {
        if (dto != null) {
            dto.setOrgId(getOrgId());
        }
        return flowAuditService.getAuditLogList(dto);
    }

    /**
     * flow audit request
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/records/audit/request", method = RequestMethod.POST)
    public ResultModel auditProcess(@RequestBody FlowAuditDTO dto) {
        // 二次校验
        adminLoginUserService.verifyAdvance(dto.getAuthType(), dto.getVerifyCode(), getRequestUserId(), getOrgId(), getAdminPlatform());
        return flowAuditService.auditProcess(dto, getRequestUser());
    }

    @RequestMapping(value = "/records/audit/get_detail", method = RequestMethod.POST)
    public ResultModel getAuditDetail(@RequestBody FlowAuditDetailDTO dto){
        if (dto != null) {
            dto.setOrgId(getOrgId());
        }
        return flowAuditService.getAuditDetail(dto);
    }

    @RequestMapping(value = "/flow_config/get_admin_users", method = RequestMethod.GET)
    public ResultModel getAdminUsers(){
        List<AdminUser> list = adminUserService.listAdminUser(getOrgId());
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/records/down_attachment", produces = {"text/plain"})
    public void downloadTransferData(
            @RequestParam(value = "bizId") long bizId,
            @RequestParam(value = "bizType") Integer bizType,
            HttpServletResponse response)  throws Exception {
        response.setCharacterEncoding("UTF-8");

        if(bizType == 1 || bizType == 2){
            airDropDownload(response, bizId);
        }
    }

    /**
     * air drop download
     *
     * @param response
     * @param bizId
     */
    private void airDropDownload(HttpServletResponse response, Long bizId) throws Exception{
        AirdropInfo airdropInfo = airdropService.getAirdropInfo(bizId, getOrgId());

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet();

        List<TmplRecord> records = airdropService.listTmplRecords(getOrgId(), bizId, -1, "", 10000);

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
