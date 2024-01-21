package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.statistics.AdminQueryTokenHoldTopInfoResponse;
import io.bhex.broker.grpc.statistics.QueryTokenHoldTopInfoRequest;

/**
 * @Description:
 * @Date: 2018/12/14 下午3:05
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface StatisticClient {

    QueryAggregateRegStatisticReply queryAggregateRegStatistic(QueryAggregateRegStatisticRequest request);

    QueryDailyRegStatisticReply queryDailyRegStatistic(QueryDailyRegStatisticRequest request);

    QueryAggregateKycStatisticReply queryAggregateKycStatistic(QueryAggregateKycStatisticRequest request);

    QueryDailyKycStatisticReply queryDailyKycStatistic(QueryDailyKycStatisticRequest request);

    AdminQueryTokenHoldTopInfoResponse adminQueryTokenHoldTopInfo(QueryTokenHoldTopInfoRequest request);
}
