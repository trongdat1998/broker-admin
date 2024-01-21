package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.red_packet.ChangeThemeCustomOrderRequest;
import io.bhex.broker.grpc.red_packet.ChangeThemeCustomOrderResponse;
import io.bhex.broker.grpc.red_packet.ChangeTokenConfigCustomOrderRequest;
import io.bhex.broker.grpc.red_packet.ChangeTokenConfigCustomOrderResponse;
import io.bhex.broker.grpc.red_packet.QueryRedPacketListRequest;
import io.bhex.broker.grpc.red_packet.QueryRedPacketListResponse;
import io.bhex.broker.grpc.red_packet.QueryRedPacketReceiveDetailListRequest;
import io.bhex.broker.grpc.red_packet.QueryRedPacketReceiveDetailListResponse;
import io.bhex.broker.grpc.red_packet.QueryRedPacketThemeRequest;
import io.bhex.broker.grpc.red_packet.QueryRedPacketThemeResponse;
import io.bhex.broker.grpc.red_packet.QueryRedPacketTokenConfigRequest;
import io.bhex.broker.grpc.red_packet.QueryRedPacketTokenConfigResponse;
import io.bhex.broker.grpc.red_packet.RedPacketAdminServiceGrpc;
import io.bhex.broker.grpc.red_packet.SaveOrUpdateRedPacketThemeRequest;
import io.bhex.broker.grpc.red_packet.SaveOrUpdateRedPacketThemeResponse;
import io.bhex.broker.grpc.red_packet.SaveOrUpdateRedPacketThemesRequest;
import io.bhex.broker.grpc.red_packet.SaveOrUpdateRedPacketTokenConfigRequest;
import io.bhex.broker.grpc.red_packet.SaveOrUpdateRedPacketTokenConfigResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedPacketClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private RedPacketAdminServiceGrpc.RedPacketAdminServiceBlockingStub getStub() {
        return grpcConfig.redPacketAdminServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    public QueryRedPacketThemeResponse queryRedPacketTheme(QueryRedPacketThemeRequest request) {
        return getStub().queryRedPacketTheme(request);
    }

    public QueryRedPacketTokenConfigResponse queryRedPacketTokenConfig(QueryRedPacketTokenConfigRequest request) {
        return getStub().queryRedPacketTokenConfig(request);
    }

    public SaveOrUpdateRedPacketThemeResponse saveRedPacketTheme(SaveOrUpdateRedPacketThemeRequest request) {
        return getStub().saveOrUpdateRedPacketTheme(request);
    }

    public SaveOrUpdateRedPacketThemeResponse saveRedPacketThemes(SaveOrUpdateRedPacketThemesRequest request) {
        return getStub().saveOrUpdateRedPacketThemes(request);
    }

    public SaveOrUpdateRedPacketTokenConfigResponse saveRedPacketTokenConfig(SaveOrUpdateRedPacketTokenConfigRequest request) {
        return getStub().saveOrUpdateRedPacketTokenConfig(request);
    }

    public ChangeThemeCustomOrderResponse changeThemeCustomOrder(ChangeThemeCustomOrderRequest request) {
        return getStub().changeThemeCustomOrder(request);
    }

    public ChangeTokenConfigCustomOrderResponse changeTokenConfigCustomOrder(ChangeTokenConfigCustomOrderRequest request) {
        return getStub().changeTokenConfigCustomOrder(request);
    }

    public QueryRedPacketListResponse queryRedPacketList(QueryRedPacketListRequest request) {
        return getStub().queryRedPacketList(request);
    }

    public QueryRedPacketReceiveDetailListResponse queryRedPacketReceiveDetailList(QueryRedPacketReceiveDetailListRequest request) {
        return getStub().queryRedPacketReceiveDetailList(request);
    }

}
