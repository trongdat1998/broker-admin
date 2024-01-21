package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.*;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerAccountTradeFeeGroupDTO;
import io.bhex.broker.admin.controller.param.BrokerAccountTradeFeeGroupPO;
import io.bhex.broker.admin.grpc.client.BrokerAccountClient;
import io.bhex.broker.admin.grpc.client.BrokerAccountTradeFeeSettingClient;
import io.bhex.broker.grpc.account.SimpleAccount;
import io.bhex.broker.grpc.account.VerifyBrokerAccountRequest;
import io.bhex.broker.grpc.account.VerifyBrokerAccountResponse;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date: 2018/11/21 下午5:30
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface BrokerAccountTradeFeeSettingService {


    BrokerAccountTradeFeeGroupDTO getBrokerAccountTradeFeeGroup(Long brokerId, Long groupId);

    List<BrokerAccountTradeFeeGroupDTO> getBrokerAccountTradeFeeGroups(Long brokerId);

    /**
     * 获取合作交易所中最小的MakerBonusRate
     * @param brokerId
     * @param exchangeIds
     * @return
     */
    BigDecimal getMinMakerBonusRate(Long brokerId, List<Long> exchangeIds);

    ResultModel editBrokerAccountTradeFeeGroup(Long brokerId, BrokerAccountTradeFeeGroupPO po);

    void enableBrokerAccountTradeFeeGroup(Long brokerId, Long groupId);

    void disableBrokerAccountTradeFeeGroup(Long brokerId, Long groupId);
}
