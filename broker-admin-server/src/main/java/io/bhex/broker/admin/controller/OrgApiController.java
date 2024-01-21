package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.OrgApiKeyDTO;
import io.bhex.broker.admin.controller.dto.SubBusinessSubjectDTO;
import io.bhex.broker.admin.controller.param.SubBusinessSubjectPO;
import io.bhex.broker.admin.service.OrgApiKeyService;
import io.bhex.broker.admin.service.SubBusinessSubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/org_api")
public class OrgApiController extends BrokerBaseController {

    private static final String EXECUTABLE_API_KEY_TYPE_STR = "11";
    private static final int EXECUTABLE_API_KEY_TYPE = 11;

    @Resource
    private SubBusinessSubjectService subBusinessSubjectService;

    @Resource
    private OrgApiKeyService orgApiKeyService;

    @RequestMapping(value = "/query_sub_business_subject")
    public ResultModel querySubBusinessSubject(@RequestParam(required = false, defaultValue = "0") Integer parentSubject) {
        List<SubBusinessSubjectDTO> dtoList = subBusinessSubjectService.querySubBusinessSubjectList(getOrgId(), parentSubject);
        return ResultModel.ok(dtoList);
    }

    @RequestMapping(value = "/save_sub_business_subject")
    public ResultModel saveSubBusinessSubject(@RequestBody SubBusinessSubjectPO po) {
        po.setOrgId(getOrgId());
        subBusinessSubjectService.saveSubBusinessSubject(po);
        return ResultModel.ok();
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/api_key/create", method = RequestMethod.POST)
    public ResultModel createApiKey(@RequestBody Map<String, String> paramsMap) {
//        return ResultModel.error("create_org_api_key.close");
        String tag = Strings.nullToEmpty(paramsMap.get("tag"));
//        Integer type = Integer.parseInt(paramsMap.getOrDefault("type", "11"));
        Integer type = Integer.parseInt(paramsMap.getOrDefault("type", EXECUTABLE_API_KEY_TYPE_STR));
        OrgApiKeyDTO orgApiKeyDTO = orgApiKeyService.createApiKey(getOrgId(), tag, type);
        return ResultModel.ok(orgApiKeyDTO);
    }

    @BussinessLogAnnotation(opContent = "UpdateIpWhiteList id:{#paramsMap['id']} ips:{#paramsMap['ips']}")
    @RequestMapping(value = "/api_key/update_ips", method = RequestMethod.POST)
    public ResultModel updateIpWhiteList(@RequestBody Map<String, String> paramsMap) {
        Long id = Long.parseLong(paramsMap.get("id"));
        String ips = Strings.nullToEmpty(paramsMap.get("ips"));
        orgApiKeyService.updateWhiteIps(getOrgId(), id, ips);
        return ResultModel.ok();
    }

    @BussinessLogAnnotation(opContent = "UpdateApiKey id:{#paramsMap['id']}")
    @RequestMapping(value = "/api_key/change_status", method = RequestMethod.POST)
    public ResultModel updateApiKey(@RequestBody Map<String, String> paramsMap) {
        Long id = Long.parseLong(paramsMap.get("id"));
        Integer status = Integer.parseInt(paramsMap.get("status"));
        orgApiKeyService.updateStatus(getOrgId(), id, status);
        return ResultModel.ok();
    }

    @BussinessLogAnnotation(opContent = "DeleteApiKey id:{#paramsMap['id']}")
    @PostMapping(value = "/api_key/delete")
    public ResultModel deleteApiKey(@RequestBody Map<String, String> paramsMap) {
        Long id = Long.parseLong(paramsMap.get("id"));
        orgApiKeyService.delete(getOrgId(), id);
        return ResultModel.ok();
    }

    @RequestMapping(value = "/api_keys")
    public ResultModel queryUserApiKeys() {
        List<OrgApiKeyDTO> apiKeyDTOList = orgApiKeyService.queryApiKeys(getOrgId());
        return ResultModel.ok(apiKeyDTOList);
    }

}
