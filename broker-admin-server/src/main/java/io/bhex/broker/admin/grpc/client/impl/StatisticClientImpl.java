package io.bhex.broker.admin.grpc.client.impl;


import io.bhex.broker.admin.grpc.client.StatisticClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.statistics.AdminQueryTokenHoldTopInfoResponse;
import io.bhex.broker.grpc.statistics.QueryTokenHoldTopInfoRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Date: 2018/12/14 下午3:06
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Service
public class StatisticClientImpl implements StatisticClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public QueryAggregateRegStatisticReply queryAggregateRegStatistic(QueryAggregateRegStatisticRequest request) {
        return grpcConfig.adminStatisticServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryAggregateRegStatistic(request);
    }

    @Override
    public QueryDailyRegStatisticReply queryDailyRegStatistic(QueryDailyRegStatisticRequest request) {
        return grpcConfig.adminStatisticServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryDailyRegStatistic(request);
    }

    @Override
    public QueryAggregateKycStatisticReply queryAggregateKycStatistic(QueryAggregateKycStatisticRequest request) {
        return grpcConfig.adminStatisticServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryAggregateKycStatistic(request);
    }

    @Override
    public QueryDailyKycStatisticReply queryDailyKycStatistic(QueryDailyKycStatisticRequest request) {
        return grpcConfig.adminStatisticServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryDailyKycStatistic(request);
    }

    @Override
    public AdminQueryTokenHoldTopInfoResponse adminQueryTokenHoldTopInfo(QueryTokenHoldTopInfoRequest request) {
        return grpcConfig.statisticsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQueryTokenHoldTopInfo(request);
    }
}
