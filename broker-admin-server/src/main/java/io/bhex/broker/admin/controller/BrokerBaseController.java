package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.param.GetBrokerUserPO;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.grpc.admin.ListUserAccountResponse;
import io.bhex.broker.grpc.admin.UserAccountMap;
import io.bhex.broker.grpc.common.AccountTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date: 2018/9/19 下午6:36
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
public class BrokerBaseController extends BaseController {

    @Autowired
    private BrokerUserClient brokerUserClient;

    /**
     * 检查是否在查询用户
     * @param po
     * @param <T>
     * @return
     */
    protected <T> boolean hasUserQueryCondition(T po) {
        GetBrokerUserPO userPO = new GetBrokerUserPO();
        BeanUtils.copyProperties(po, userPO);
        if ((userPO.getUserId() == null || userPO.getUserId() == 0)
                && StringUtils.isEmpty(userPO.getPhone()) && StringUtils.isEmpty(userPO.getEmail())) {
            return false;
        }
        return true;
    }

    /**
     * @param po  可以转换成GetBrokerUserPO的Bean
     * @param <T>
     * @return v1-userid v2-accountid
     */
    protected <T> Combo2<Long, Long> getUserIdAndAccountId(T po, Long brokerId) {

        GetBrokerUserPO userPO = new GetBrokerUserPO();
        BeanUtils.copyProperties(po, userPO);
        if ((userPO.getUserId() == null || userPO.getUserId() == 0)
                && StringUtils.isEmpty(userPO.getPhone()) && StringUtils.isEmpty(userPO.getEmail())) {
            return null;
        }
        BrokerUserDTO dto = brokerUserClient.getBrokerUser(brokerId, userPO.getUserId(), userPO.getUserId(),
                userPO.getNationalCode(), userPO.getPhone(), userPO.getEmail());
        if (dto == null) {
            return null;
        }
        Long accountId = brokerUserClient.getAccountId(brokerId, dto.getUserId());
        log.info("userId:{} accountId{}", dto.getUserId(), accountId);
        return new Combo2<>(dto.getUserId(), accountId);
    }

//    protected Long getAdminUserId(HttpServletRequest request) {
//        return (Long) request.getAttribute(OpenApiAuthenticationInterceptor.USER_ID_ATTR_KEY);
//    }
    
    protected List<Long> getErrorUserIds(long brokerId, List<Long> userIds) {
        return getErrorUserIds(brokerId, userIds, AccountTypeEnum.COIN);
    }

    protected List<Long> getErrorUserIds(long brokerId, AccountTypeEnum accountTypeEnum, List<String> userIds) {
        List<Long> userIdList = userIds.stream()
                .filter(u -> !getUserIdStr(u.trim()).equals(""))
                .map(i -> Long.parseLong(getUserIdStr(i.trim())))
                .collect(Collectors.toList());
        return getErrorUserIds(brokerId, userIdList, accountTypeEnum);
    }

    protected List<Long> getErrorUserIds(long brokerId, List<Long> userIds, AccountTypeEnum accountTypeEnum) {
        List<UserAccountMap> list = listUserAccount(brokerId, userIds, accountTypeEnum);
        List<Long> errorList = new ArrayList<>();
        for (Long userId : userIds) {
            boolean existed = false;
            for (UserAccountMap accountMap : list) {
                if (accountMap.getUserId() == userId) {
                    existed = true;
                    break;
                }
            }
            if (!existed) {
                errorList.add(userId);
            }
        }
        return errorList;
    }

    protected String getUserIdStr(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }


    protected List<UserAccountMap> listUserAccount(long brokerId, List<Long> userIds, AccountTypeEnum accountTypeEnum) {
        ListUserAccountResponse resp = brokerUserClient.listUserAccount(brokerId, userIds, accountTypeEnum);
        List<UserAccountMap> list = resp.getAccountInfoList()
                .stream().filter(u -> u.getAccountIndex() == 0) //主账户
                .collect(Collectors.toList());
        return list;
    }
    /**
     * @param po  可以转换成GetBrokerUserPO的Bean
     * @param <T>
     * @return v1-userid v2-accountid
     */
    protected <T> Combo2<Long, Long> getUserIdAndMarginAccountId(T po, Long brokerId) {

        GetBrokerUserPO userPO = new GetBrokerUserPO();
        BeanUtils.copyProperties(po, userPO);
        if ((userPO.getUserId() == null || userPO.getUserId() == 0)
                && StringUtils.isEmpty(userPO.getPhone()) && StringUtils.isEmpty(userPO.getEmail())) {
            return null;
        }
        BrokerUserDTO dto = brokerUserClient.getBrokerUser(brokerId,
                userPO.getUserId(), userPO.getUserId(), userPO.getNationalCode(), userPO.getPhone(), userPO.getEmail());
        if (dto == null) {
            return null;
        }
        Long accountId = brokerUserClient.getMarginAccountId(brokerId, dto.getUserId());
        log.info("userId:{} accountId{}", dto.getUserId(), accountId);
        return new Combo2<>(dto.getUserId(), accountId);
    }

    protected <T> Long getUserId(T po, Long brokerId) {
        GetBrokerUserPO userPO = new GetBrokerUserPO();
        BeanUtils.copyProperties(po, userPO);
        if ((userPO.getUserId() == null || userPO.getUserId() == 0)
                && StringUtils.isEmpty(userPO.getPhone()) && StringUtils.isEmpty(userPO.getEmail())) {
            return null;
        }
        BrokerUserDTO dto = brokerUserClient.getBrokerUser(brokerId,
                userPO.getUserId(), userPO.getNationalCode(), userPO.getPhone(), userPO.getEmail());
        if (dto == null) {
            return null;
        }
        return dto.getUserId();
    }

}
