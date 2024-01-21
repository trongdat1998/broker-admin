package io.bhex.broker.admin.service.impl;

import io.bhex.base.account.AccountType;
import io.bhex.base.idgen.api.ISequenceGenerator;
import io.bhex.bhop.common.config.LocaleMessageService;
import io.bhex.bhop.common.config.OrgInstanceConfig;
import io.bhex.bhop.common.dto.param.BrokerInstanceRes;
import io.bhex.bhop.common.grpc.client.BhAccountClient;
import io.bhex.bhop.common.grpc.client.MessagePushClient;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BindFundAccountCheckDTO;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.dto.FundAccountDTO;
import io.bhex.broker.admin.controller.param.BindFundAccountPO;
import io.bhex.broker.admin.controller.param.GetBrokerUserPO;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.grpc.client.impl.BrokerUserClientImpl;
import io.bhex.broker.admin.service.PlatformAccountBindService;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.admin.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Date: 2018/10/10 下午5:11
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@Service
public class PlatformAccountBindServiceImpl implements PlatformAccountBindService {

    @Autowired
    private BhAccountClient bhAccountClient;
    @Autowired
    private OrgInstanceConfig orgInstanceConfig;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private LocaleMessageService localeMessageService;
    @Autowired
    private MessagePushClient messagePushClient;
    @Autowired
    private BrokerUserClient brokerUserClient;
    @Autowired
    private BrokerClient brokerClient;

    @Autowired
    private BrokerUserClientImpl brokerUserClientImpl;

    @Autowired
    private ISequenceGenerator sequenceGenerator;

    @Value("${verify-captcha:true}")
    private Boolean verifyCaptcha;

    @Override
    public InnerResult checkBindInput(Long myBrokerId, String brokerName, String username, int accountType) {
        InnerResult result = new InnerResult();
        Optional<BrokerInstanceRes> optional = orgInstanceConfig.listBrokerInstances().stream()
                .filter(b -> b.getBrokerName().equals(brokerName))
                .findFirst();
        if (!optional.isPresent()) {
            result.setErrorMsg("platform_account.broker.name.not.found");
            return result;
        }

        result.setBrokerId(optional.get().getBrokerId());
        result.setUsername(username);

        Long bindingAccountId = bhAccountClient.bindRelation(myBrokerId, AccountType.forNumber(accountType));
        if (bindingAccountId != null && bindingAccountId > 0) {
            result.setErrorMsg("platform_account.binding");
            return result;
        }

        GetBrokerUserPO userPO = new GetBrokerUserPO();
        if (username.indexOf("@") != -1) {
            userPO.setEmail(username);
            result.setUsernameIsEmail(true);
        } else {
            userPO.setPhone(username);
            result.setUsernameIsEmail(false);
        }


        BrokerUserDTO dto = brokerUserClient.getBrokerUser(optional.get().getBrokerId(),
                userPO.getUserId(), userPO.getNationalCode(), userPO.getPhone(), userPO.getEmail());
        if (dto == null) {
            result.setErrorMsg("platform_account.username.not.found");
            return result;
        }

        Long accountId = brokerUserClient.getAccountId(optional.get().getBrokerId(), dto.getUserId());
        result.setAccountId(accountId);
        return result;
    }

    public InnerResult checkVerifyCode(Long myBrokerId, String inputValidateCode, int accountType, Long accountId) {
        InnerResult result = new InnerResult();
        String cacheKey = getCacheKey(myBrokerId, accountType, accountId);
        if (!verifyCaptcha) {
            redisTemplate.delete(cacheKey);
            if (!"123456".equals(inputValidateCode)) {
                result.setErrorMsg("verify.code.error");
            }
            return result;
        }
        String code = redisTemplate.opsForValue().get(cacheKey);
        log.info("verify code:{} {}", cacheKey, code);
        if (code == null) {
            result.setErrorMsg("verify.code.error");
            return result;
        }

        if (!inputValidateCode.equals(code)) {
            result.setErrorMsg("verify.code.error");
            return result;
        }
        redisTemplate.delete(cacheKey);
        return result;
    }

    public String getCacheKey(Long targetOrgId, Integer accountType, Long accountId) {
        String key = "Bind.Account." + targetOrgId + "." + accountType + "." + accountId;
        return key;
    }

    public boolean sendVerifyCode(Long myBrokerId, String username, int accountType, InnerResult result) {
        String cacheKey = getCacheKey(myBrokerId, accountType, result.getAccountId());
        String duplicateCacheKey = cacheKey + "_retry";
//        Object duplicateCache = redisClient.getObject(duplicateCacheKey);
        Object duplicateCache = redisTemplate.opsForValue().get(duplicateCacheKey);
        if (duplicateCache != null) {
            return false;
        }
        String code = RandomStringUtils.random(6, false, true);
        String message = localeMessageService.getMessage("platform.account.bind.sms.verify.code", new Object[]{code});

        BrokerInstanceRes brokerInstance = orgInstanceConfig.getBrokerInstance(myBrokerId);
        if (result.isUsernameIsEmail()) {
            String senderName = brokerInstance.getBrokerName();
            messagePushClient.sendMailDirectly(myBrokerId, username, senderName, senderName, message, LocaleUtil.getLanguage());
        } else {
            BrokerUserDTO dto = brokerUserClient.getBrokerUser(result.getBrokerId(),
                    null, null, username, null);
            BrokerDetail detail = brokerClient.getByBrokerId(result.getBrokerId());
            messagePushClient.sendVerificationCodeSmsDirectly(myBrokerId, dto.getNationalCode(), dto.getRealMobile(), code, LocaleUtil.getLanguage());
        }

        redisTemplate.opsForValue().set(cacheKey, code, 600, TimeUnit.SECONDS);
        log.info("{} send code:{} {}", username, cacheKey, code);

        redisTemplate.opsForValue().set(duplicateCacheKey, Boolean.TRUE.toString(), 60, TimeUnit.SECONDS);
        return true;
    }

    public ResultModel<BindFundAccountCheckDTO> checkBindFundAccount(Long orgId, Long accountId) {

        String cacheKey = getBindFundAccountCacheKey(orgId, accountId);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtils.isNotEmpty(cacheValue)) {

            long time = Long.parseLong(cacheValue.split("\\|")[2]);
            if (time + 60000 > System.currentTimeMillis()) {
                //60s内获取缓存。不然重新检查
                BindFundAccountCheckDTO dto = JsonUtil.defaultGson().fromJson(cacheValue.split("\\|")[1], BindFundAccountCheckDTO.class);
                return ResultModel.ok(dto);
            }
            redisTemplate.delete(cacheKey);
        }

        CheckBindFundAccountRequest request = CheckBindFundAccountRequest.newBuilder()
                .setOrgId(orgId)
                .setAccountId(accountId)
                .build();
        CheckBindFundAccountResponse response = brokerUserClientImpl.checkBindFundAccount(request);

        if (response.getCode() == 0) {
            BindFundAccountCheckDTO dto = new BindFundAccountCheckDTO();
            dto.setAccountId(accountId);
            dto.setUserId(response.getResponse().getUserId());
            dto.setRequestId(0L);

            if (response.getResponse().getBindGa()) {
                dto.setAuthType(3);
                return ResultModel.ok(dto);
            }

            String code = RandomStringUtils.random(6, false, true);

            BrokerDetail detail = brokerClient.getByBrokerId(orgId);
            messagePushClient.sendVerificationCodeSmsDirectly(orgId, response.getResponse().getNationalCode(), response.getResponse().getMobile(), code, LocaleUtil.getLanguage());
            Long requestId = sequenceGenerator.getLong();

            dto.setAuthType(1);
            dto.setRequestId(requestId);

            redisTemplate.opsForValue().set(cacheKey, code + "|" + JsonUtil.defaultGson().toJson(dto) + "|" + System.currentTimeMillis(), 600, TimeUnit.SECONDS);
            log.info("{} send code:{} {}", response.getResponse().getMobile(), cacheKey, code + "|" + JsonUtil.defaultGson().toJson(dto));

            return ResultModel.ok(dto);
        }

        //0=正常，1=账户不存在 2=已是资金账户 3=未绑定手机或GA 4、非币币交易主账户 5、ga验证码错误
        return getBindFundAccountErrorResult(response.getCode());
    }

    public ResultModel bindFundAccount(Long orgId, BindFundAccountPO po) {

        if (po.getAuthType() != 1 && po.getAuthType() != 3) {
            return ResultModel.error("request.parameter.error");
        }

        if (po.getAuthType() == 1) {
            //手机号,校验验证码是否正确
            String cacheKey = getBindFundAccountCacheKey(orgId, po.getAccountId());
            String cacheValue = redisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.isEmpty(cacheValue)) {
                return ResultModel.error("verify.code.error");
            }

            String code = cacheValue.split("\\|")[0];
            BindFundAccountCheckDTO dto = JsonUtil.defaultGson().fromJson(cacheValue.split("\\|")[1], BindFundAccountCheckDTO.class);

            if (!po.getVerifyCode().equals(code) || !dto.getRequestId().equals(po.getRequestId())) {
                return ResultModel.error("verify.code.error");
            }
            redisTemplate.delete(cacheKey);
        }

        BindFundAccountRequest request = BindFundAccountRequest.newBuilder()
                .setAccountId(po.getAccountId())
                .setAuthType(po.getAuthType())
                .setOrgId(orgId)
                .setVerifyCode(po.getVerifyCode())
                .setUserId(po.getUserId())
                .setRemark(po.getRemark() == null ? "" : po.getRemark())
                .setTag(po.getTag())
                .build();
        BindFundAccountResponse response = brokerUserClientImpl.bindFundAccount(request);

        if (response.getCode() == 0) {
            return ResultModel.ok();
        } else {
            return getBindFundAccountErrorResult(response.getCode());
        }

    }

    public List<FundAccountDTO> queryFundAccount(Long orgId) {

        QueryFundAccountRequest request = QueryFundAccountRequest.newBuilder().setOrgId(orgId).build();

        QueryFundAccountResponse response = brokerUserClientImpl.queryFundAccount(request);

        List<FundAccountDTO> fundAccountDTOList = new ArrayList<>();

        for (QueryFundAccountResponse.FundAccount account : response.getFundAccountListList()) {
            FundAccountDTO dto = new FundAccountDTO();
            dto.setId(account.getId());
            dto.setOrgId(account.getOrgId());
            dto.setUserId(account.getUserId());
            dto.setTag(account.getTag());
            dto.setRemark(account.getRemark());
            dto.setIsShow(account.getIsShow());
            dto.setAccountId(account.getAccountId());
            dto.setCreatedAt(account.getCreatedAt());
            dto.setUpdatedAt(account.getUpdatedAt());
            fundAccountDTOList.add(dto);
        }

        return fundAccountDTOList;

    }

    public void setFundAccountVisible(Long orgId, Long accountId) {
        brokerUserClientImpl.setFundAccountShow(SetFundAccountShowRequest.newBuilder().setOrgId(orgId).setAccountId(accountId).setIsShow(1).build());
    }


    public void setFundAccountUnVisible(Long orgId, Long accountId) {
        brokerUserClientImpl.setFundAccountShow(SetFundAccountShowRequest.newBuilder().setOrgId(orgId).setAccountId(accountId).setIsShow(0).build());
    }


    private ResultModel getBindFundAccountErrorResult(int errorCode) {
        //0=正常，1=账户不存在 2=已是资金账户 3=未绑定手机或GA 4=非币币主账户 5=ga验证错误

        if (errorCode == 1) {
            return ResultModel.error("platform_account.accountid.error");
        } else if (errorCode == 2) {
            return ResultModel.error("platform_account.binding");
        } else if (errorCode == 3) {
            return ResultModel.error("unbind.ga.mobile.error");
        } else if (errorCode == 4) {
            return ResultModel.error("platform_account.mainaccount.error");
        } else if (errorCode == 5) {
            return ResultModel.error("verify.google.ga.error");
        } else {
            return ResultModel.error("request.parameter.error");
        }
    }

    private String getBindFundAccountCacheKey(Long orgId, Long accountId) {
        String key = "Bind.FundAccount." + orgId + "." + accountId;
        return key;
    }

}
