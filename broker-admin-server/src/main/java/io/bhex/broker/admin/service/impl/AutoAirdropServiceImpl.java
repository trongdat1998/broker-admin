package io.bhex.broker.admin.service.impl;

import io.bhex.broker.admin.controller.dto.AutoAirdropDTO;
import io.bhex.broker.admin.controller.param.AutoAirdropPO;
import io.bhex.broker.admin.grpc.client.AutoAirdropClient;
import io.bhex.broker.admin.service.AutoAirdropService;
import io.bhex.broker.grpc.airdrop.AutoAirdropInfo;
import io.bhex.broker.grpc.airdrop.GetAutoAirdropInfoRequest;
import io.bhex.broker.grpc.airdrop.SaveAutoAirdropInfoRequest;
import io.bhex.broker.grpc.airdrop.SaveAutoAirdropInfoResponse;
import io.bhex.broker.grpc.common.Header;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 30/11/2018 3:56 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class AutoAirdropServiceImpl implements AutoAirdropService {

    @Autowired
    private AutoAirdropClient  autoAirdropClient;

    @Override
    public Boolean saveAutoAirdrop(AutoAirdropPO param) {
        Header header = Header.newBuilder()
                .setOrgId(param.getBrokerId())
                .build();

        AutoAirdropInfo.Builder builder = AutoAirdropInfo.newBuilder();
        builder.setBrokerId(param.getBrokerId());
        builder.setTokenId(param.getTokenId());
        builder.setAirdropTokenNum(param.getAirdropTokenNum().toPlainString());
        builder.setAirdropType(param.getAirdropType());
        builder.setAccountType(param.getAccountType());
        builder.setStatus(param.getStatus()? 1: 0);

        SaveAutoAirdropInfoRequest request = SaveAutoAirdropInfoRequest.newBuilder()
                .setAutoAirdrop(builder.build())
                .setHeader(header)
                .build();
        SaveAutoAirdropInfoResponse response = autoAirdropClient.saveAutoAirdrop(request);
        return response.getRet();
    }

    @Override
    public AutoAirdropDTO getAutoAirdrop(Long brokerId) {
        Header header = Header.newBuilder()
                .setOrgId(brokerId)
                .build();

        GetAutoAirdropInfoRequest request = GetAutoAirdropInfoRequest.newBuilder()
                .setHeader(header)
                .build();

        AutoAirdropInfo autoAirdrop = autoAirdropClient.getAutoAirdrop(request);

        AutoAirdropDTO dto = new AutoAirdropDTO();
        BeanUtils.copyProperties(autoAirdrop, dto);
        dto.setStatus(autoAirdrop.getStatus() == 1? true: false);
        return dto;
    }
}
