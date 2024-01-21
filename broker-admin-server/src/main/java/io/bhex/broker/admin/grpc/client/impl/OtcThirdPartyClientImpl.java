package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.OtcThirdPartyClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.otc.third.party.GetOtcThirdPartyRequest;
import io.bhex.broker.grpc.otc.third.party.GetOtcThirdPartyResponse;
import io.bhex.broker.grpc.otc.third.party.QueryOtcThirdPartyDisclaimerRequest;
import io.bhex.broker.grpc.otc.third.party.QueryOtcThirdPartyDisclaimerResponse;
import io.bhex.broker.grpc.otc.third.party.QueryOtcThirdPartyOrdersByAdminRequest;
import io.bhex.broker.grpc.otc.third.party.QueryOtcThirdPartyOrdersResponse;
import io.bhex.broker.grpc.otc.third.party.UpdateOtcThirdPartyDisclaimerRequest;
import io.bhex.broker.grpc.otc.third.party.UpdateOtcThirdPartyDisclaimerResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OtcThirdPartyClientImpl implements OtcThirdPartyClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public GetOtcThirdPartyResponse getOtcThirdParty(GetOtcThirdPartyRequest request) {
        return grpcConfig.otcThirdPartyServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).getOtcThirdParty(request);
    }

    @Override
    public QueryOtcThirdPartyDisclaimerResponse queryOtcThirdPartyDisclaimer(QueryOtcThirdPartyDisclaimerRequest request) {
        return grpcConfig.otcThirdPartyServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOtcThirdPartyDisclaimer(request);
    }

    @Override
    public UpdateOtcThirdPartyDisclaimerResponse updateOtcThirdPartyDisclaimer(UpdateOtcThirdPartyDisclaimerRequest request) {
        return grpcConfig.otcThirdPartyServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).updateOtcThirdPartyDisclaimer(request);
    }

    @Override
    public QueryOtcThirdPartyOrdersResponse queryOtcThirdPartyOrdersByAdmin(QueryOtcThirdPartyOrdersByAdminRequest request) {
        return grpcConfig.otcThirdPartyServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOtcThirdPartyOrdersByAdmin(request);
    }

}
