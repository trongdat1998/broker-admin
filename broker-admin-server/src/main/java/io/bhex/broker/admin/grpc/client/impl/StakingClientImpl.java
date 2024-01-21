package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.StakingClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.BrokerUserServiceGrpc;
import io.bhex.broker.grpc.admin.QueryFundAccountShowRequest;
import io.bhex.broker.grpc.admin.QueryFundAccountShowResponse;
import io.bhex.broker.grpc.proto.AdminCommonResponse;
import io.bhex.broker.grpc.staking.AdminGetBrokerProductPermissionReply;
import io.bhex.broker.grpc.staking.AdminGetBrokerProductPermissionRequest;
import io.bhex.broker.grpc.staking.AdminGetProductDetailReply;
import io.bhex.broker.grpc.staking.AdminGetProductDetailRequest;
import io.bhex.broker.grpc.staking.AdminGetProductListReply;
import io.bhex.broker.grpc.staking.AdminGetProductListRequest;
import io.bhex.broker.grpc.staking.AdminOnlineProductRequest;
import io.bhex.broker.grpc.staking.AdminQueryBrokerProductHistoryRebateReply;
import io.bhex.broker.grpc.staking.AdminQueryBrokerProductHistoryRebateRequest;
import io.bhex.broker.grpc.staking.AdminQueryBrokerProductUndoRebateReply;
import io.bhex.broker.grpc.staking.AdminQueryBrokerProductUndoRebateRequest;
import io.bhex.broker.grpc.staking.AdminQueryCurrentProductAssetReply;
import io.bhex.broker.grpc.staking.AdminQueryCurrentProductAssetRequest;
import io.bhex.broker.grpc.staking.AdminSaveProductReply;
import io.bhex.broker.grpc.staking.AdminSaveProductRequest;
import io.bhex.broker.grpc.staking.AdminStakingProductServiceGrpc;
import io.bhex.broker.grpc.staking.GetCurrentProductRebateListReply;
import io.bhex.broker.grpc.staking.GetCurrentProductRebateListRequest;
import io.bhex.broker.grpc.staking.GetCurrentProductRepaymentScheduleReply;
import io.bhex.broker.grpc.staking.GetCurrentProductRepaymentScheduleRequest;
import io.bhex.broker.grpc.staking.GetProductRepaymentScheduleReply;
import io.bhex.broker.grpc.staking.GetProductRepaymentScheduleRequest;
import io.bhex.broker.grpc.staking.QueryBrokerProductOrderReply;
import io.bhex.broker.grpc.staking.QueryBrokerProductOrderRequest;
import io.bhex.broker.grpc.staking.StakingCalcInterestReply;
import io.bhex.broker.grpc.staking.StakingCalcInterestRequest;
import io.bhex.broker.grpc.staking.StakingProductCancelDividendRequest;
import io.bhex.broker.grpc.staking.StakingProductCancelDividendResponse;
import io.bhex.broker.grpc.staking.StakingProductDividendTransferRequest;
import io.bhex.broker.grpc.staking.StakingProductDividendTransferResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StakingClientImpl implements StakingClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminStakingProductServiceGrpc.AdminStakingProductServiceBlockingStub getStub() {
        return grpcConfig.adminStakingProductServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private BrokerUserServiceGrpc.BrokerUserServiceBlockingStub getBrokerUserStub() {
        return grpcConfig.brokerUserServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public AdminSaveProductReply saveProduct(AdminSaveProductRequest request) {
        return getStub().saveProduct(request);
    }

    @Override
    public AdminGetProductDetailReply getProductDetail(AdminGetProductDetailRequest request) {
        return getStub().getProductDetail(request);
    }

    @Override
    public AdminGetProductListReply getProductList(AdminGetProductListRequest request) {
        return getStub().getProductList(request);
    }

    @Override
    public AdminCommonResponse onlineProduct(AdminOnlineProductRequest request) {
        return getStub().onlineProduct(request);
    }

    @Override
    public AdminGetBrokerProductPermissionReply getBrokerProductPermission(AdminGetBrokerProductPermissionRequest request) {
        return getStub().getBrokerProductPermission(request);
    }

    @Override
    public AdminQueryBrokerProductUndoRebateReply queryBrokerProductUndoRebate(AdminQueryBrokerProductUndoRebateRequest request) {
        return getStub().queryBrokerProductUndoRebate(request);
    }

    @Override
    public AdminQueryBrokerProductHistoryRebateReply queryBrokerProductHistoryRebate(AdminQueryBrokerProductHistoryRebateRequest request) {
        return getStub().queryBrokerProductHistoryRebate(request);
    }

    @Override
    public StakingProductDividendTransferResponse dividendTransfer(StakingProductDividendTransferRequest request) {
        return getStub().dividendTransfer(request);
    }

    @Override
    public StakingProductCancelDividendResponse cancelDividend(StakingProductCancelDividendRequest request) {
        return getStub().cancelDividend(request);
    }

    public QueryFundAccountShowResponse queryFundAccountShow(QueryFundAccountShowRequest request) {
        return getBrokerUserStub().queryFundAccountShow(request);
    }

    public QueryBrokerProductOrderReply queryBrokerProductOrder(QueryBrokerProductOrderRequest request) {
        return getStub().queryBrokerProductOrder(request);

    }

    public GetProductRepaymentScheduleReply getProductRepaymentSchedule(GetProductRepaymentScheduleRequest request) {
        return getStub().getProductRepaymentSchedule(request);
    }

    public StakingCalcInterestReply calcInterest(StakingCalcInterestRequest request) {
        return getStub().calcInterest(request);
    }

    public AdminQueryCurrentProductAssetReply queryCurrentProductAsset(AdminQueryCurrentProductAssetRequest request) {
        return getStub().queryCurrentProductAsset(request);

    }

    public GetCurrentProductRebateListReply getCurrentProductRebateList(GetCurrentProductRebateListRequest request) {
        return getStub().getCurrentProductRebateList(request);
    }

    public GetCurrentProductRepaymentScheduleReply getCurrentProductRepaymentSchedule(GetCurrentProductRepaymentScheduleRequest request) {
        return getStub().getCurrentProductRepaymentSchedule(request);
    }

}
