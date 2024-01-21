package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.airdrop.AutoAirdropInfo;
import io.bhex.broker.grpc.airdrop.GetAutoAirdropInfoRequest;
import io.bhex.broker.grpc.airdrop.SaveAutoAirdropInfoRequest;
import io.bhex.broker.grpc.airdrop.SaveAutoAirdropInfoResponse;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 30/11/2018 3:58 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface AutoAirdropClient {

    SaveAutoAirdropInfoResponse saveAutoAirdrop(SaveAutoAirdropInfoRequest param);

    AutoAirdropInfo getAutoAirdrop(GetAutoAirdropInfoRequest request);
}
