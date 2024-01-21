package io.bhex.broker.admin.service.impl;

import com.alibaba.fastjson.JSON;
import io.bhex.bhop.common.config.LocaleMessageService;
import io.bhex.bhop.common.config.OrgInstanceConfig;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.grpc.client.MessagePushClient;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.broker.admin.aspect.KycVerifyAnnotation;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.dto.UserVerifyDTO;
import io.bhex.broker.admin.controller.dto.UserVerifyHistoryDTO;
import io.bhex.broker.admin.controller.dto.UserVerifyReasonDTO;
import io.bhex.broker.admin.controller.param.ListUpdateUserByDatePO;
import io.bhex.broker.admin.controller.param.QueryUserVerifyPO;
import io.bhex.broker.admin.controller.param.VerifyUserPO;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.grpc.client.UserVerifyClient;
import io.bhex.broker.admin.service.UserVerifyService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.admin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 24/08/2018 5:58 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class UserVerifyServiceImpl implements UserVerifyService {

    @Autowired
    private UserVerifyClient userVerifyClient;
    @Autowired
    private OsAccessAuthService osAccessAuthService;
    @Autowired
    private MessagePushClient messagePushClient;
    @Autowired
    private BrokerUserClient brokerUserClient;

    @KycVerifyAnnotation
    @Override
    public PaginationVO<UserVerifyDTO> queryUnverifyiedUser(QueryUserVerifyPO po, String locale, long brokerId) {
        po.setVerifyStatus(VerifyUserPO.UserVerifyStatus.UNDER_REVIEW.value());
        return queryUserVerifyList(po, locale, brokerId);
    }

    @Override
    public PaginationVO<UserVerifyDTO> queryUserVerifyList(QueryUserVerifyPO po, String locale, long brokerId) {
        QueryUserVerifyListRequest.Builder builder = QueryUserVerifyListRequest.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(po, builder);
        builder.setBrokerId(brokerId);
        builder.setLocale(locale);

        QueryUserVerifyListReply reply = userVerifyClient.queryUserVerifyList(builder.build());
        PaginationVO<UserVerifyDTO> vo = new PaginationVO();
        BeanUtils.copyProperties(reply, vo);

        List<UserVerifyDTO> dtos = new ArrayList<>();
        List<UserVerifyDetail> details = reply.getUserVerifyDetailsList();
        for (UserVerifyDetail detail : details) {
            UserVerifyDTO dto = new UserVerifyDTO();
            BeanUtils.copyProperties(detail, dto);
            String cardNo = detail.getCardNo().replaceAll("(.{5})(.*)(.{3})", "$1******$3");
            dto.setNationality(detail.getNationalityStr());
            dto.setCardNo(cardNo);
            dtos.add(dto);
        }
        vo.setList(dtos);
        return vo;
    }

    @Override
    public UserVerifyDTO getVerifyUserById(Long userVerifyId, Long brokerId, String locale, boolean decryptUrl) {
        UserVerifyDetail detail = userVerifyClient.getVerifyUserById(userVerifyId, brokerId, locale, decryptUrl);

        UserVerifyDTO dto = new UserVerifyDTO();
        if (null != detail) {
            BeanUtils.copyProperties(detail, dto);
            dto.setCardHandUrl(osAccessAuthService.createAccessUrl(dto.getCardHandUrl()));
            dto.setCardFrontUrl(osAccessAuthService.createAccessUrl(dto.getCardFrontUrl()));
            dto.setCardBackUrl(osAccessAuthService.createAccessUrl(dto.getCardBackUrl()));
            dto.setFacePhotoUrl(osAccessAuthService.createAccessUrl(dto.getFacePhotoUrl()));
            dto.setFaceVideoUrl(osAccessAuthService.createAccessUrl(dto.getFaceVideoUrl()));
            dto.setVideoUrl(osAccessAuthService.createAccessUrl(dto.getVideoUrl()));
        }
        return dto;
    }


    @Override
    public Boolean updateVerifyUser(Long brokerId, Long adminUserId, Long userVerifyId, Integer verifyStatus, Long reasonId, String remark) {
        UserVerifyDTO verifyUserById = getVerifyUserById(userVerifyId, brokerId, LocaleUtil.getLanguage(), false);
        if (null == verifyUserById) {
            return false;
        }
        UpdateVerifyUserReply reply = userVerifyClient.updateVerifyUser(brokerId, adminUserId, userVerifyId, verifyStatus, reasonId, remark);

//        if (reply.getResult()) {
//            BrokerUserDTO dto = brokerUserClient.getBorkerUser(brokerId, verifyUserById.getUserId());
//            if (dto == null) {
//                return false;
//            }
//
//            if (verifyStatus != VerifyUserPO.UserVerifyStatus.PASSED.value()
//                    && verifyStatus != VerifyUserPO.UserVerifyStatus.REFUSED.value()) {
//                return true;
//            }
//
//            //kyc的短信语言选择用户最后一次的设置
//            Locale locale = Locale.US;
//            QueryLoginLogsResponse lastLoginResponse = brokerUserClient.queryLoginLogs(brokerId,
//                    dto.getUserId(), 1, 1);
//            List<UserLoginLog> loginLogs = lastLoginResponse.getLoginLogsList();
//            if (!CollectionUtils.isEmpty(loginLogs)) {
//                String lang = loginLogs.get(0).getLanguage();
//                if (!StringUtils.isEmpty(lang) && lang.contains("_")) {
//                    locale = new Locale(lang.split("_")[0], lang.split("_")[1]);
//                }
//            }
//
//            String businessType = verifyStatus == VerifyUserPO.UserVerifyStatus.PASSED.value()
//                    ? "ADMIN_KYC_VERIFY_SUC" : "ADMIN_KYC_VERIFY_FAIL";
//
//            if (!StringUtils.isEmpty(dto.getEmail())) {
//                messagePushClient.sendMail(brokerId, dto.getUserId(), dto.getRealEmail(), businessType, locale.toString(), null);
//                log.info("send email(user verify notify)");
//
//            }
//
//            if (!StringUtils.isEmpty(dto.getNationalCode()) && !StringUtils.isEmpty(dto.getMobile())) {
//                messagePushClient.sendSms(brokerId, dto.getUserId(), dto.getNationalCode(), dto.getRealMobile(), businessType,
//                        locale.toString(), null);
//                log.info("send sms(user verify notify)");
//            }
//        }

        return reply.getResult();
    }

    @Override
    public List<UserVerifyReasonDTO> listVerifyReason(String locale) {
        ListVerifyReasonReply reply = userVerifyClient.listVerifyReason(locale);

        List<UserVerifyReasonDTO> dtos = new ArrayList<>();
        List<VerifyReasonDetail> details = reply.getVerifyReasonDetailsList();
        for (VerifyReasonDetail detail : details) {
            UserVerifyReasonDTO dto = new UserVerifyReasonDTO();
            BeanUtils.copyProperties(detail, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<UserVerifyHistoryDTO> listVerifyHistory(Long brokerId, String locale, Long userVerifyId) {
        ListVerifyHistoryReply reply = userVerifyClient.listVerifyHistory(brokerId, locale, userVerifyId);

        List<UserVerifyHistoryDTO> dtos = new ArrayList<>();
        List<VerifyHistoryDetail> details = reply.getVerifyHistoryDetailsList();
        for (VerifyHistoryDetail detail : details) {
            UserVerifyHistoryDTO dto = new UserVerifyHistoryDTO();
            BeanUtils.copyProperties(detail, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<BrokerUserDTO> listUpdateUserByDate(ListUpdateUserByDatePO param) {
        log.info("info {}", JSON.toJSONString(param));
        ListUpdateUserByDateRequest request = ListUpdateUserByDateRequest.newBuilder()
                .setOrgId(param.getBrokerId())
                .setFromId(Objects.isNull(param.getFromId()) ? 0 : param.getFromId())
                .setEndId(Objects.isNull(param.getEndId()) ? 0 : param.getEndId())
                .setStartTime(Objects.isNull(param.getStartTime()) ? 0 : param.getStartTime())
                .setEndTime(Objects.isNull(param.getEndTime()) ? 0 : param.getEndTime())
                .setLimit(Objects.isNull(param.getLimit()) ? 0 : param.getLimit())
                .build();

        ListUpdateUserByDateReply reply = userVerifyClient.listUpdateUserByDate(request);
        List<UserInfo> userInfoList = reply.getUserInfoList();
        List<BrokerUserDTO> dtos = new ArrayList<>();
        userInfoList.forEach(userInfo -> {
            BrokerUserDTO dto = new BrokerUserDTO();
            BeanUtils.copyProperties(userInfo, dto);
            dto.setMobile(userInfo.getMobile());
            dto.setEmail(userInfo.getEmail());
            dto.setRealEmail(userInfo.getEmail());
            dto.setRealMobile(userInfo.getMobile());
            dto.setId(userInfo.getId());
            dtos.add(dto);
        });
        return dtos;
    }

    @Override
    public AddBrokerKycConfigReply addBrokerKycConfig(AddBrokerKycConfigRequest request) {
        return userVerifyClient.addBrokerKycConfig(request);
    }

    @Override
    public DegradeBrokerKycLevelReply degradeBrokerKycLevel(long brokerId, long userId) {
        return userVerifyClient.degradeBrokerKycLevel(brokerId, userId);
    }

    @Override
    public OpenThirdKycAuthReply openThirdKycAuth(long brokerId, long userId) {
        return userVerifyClient.openThirdKycAuth(brokerId, userId);
    }

    @Override
    public List<BrokerKycConfig> getBrokerKycConfigs(long brokerId) {
        return userVerifyClient.getBrokerKycConfigs(brokerId).getConfigsList();
    }
}
