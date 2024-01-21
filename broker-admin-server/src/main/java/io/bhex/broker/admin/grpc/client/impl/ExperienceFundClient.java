package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.activity.experiencefund.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: 体验金活动
 * @Date: 2020/3/3 下午3:17
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Slf4j
@Service
public class ExperienceFundClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private ExperienceFundServiceGrpc.ExperienceFundServiceBlockingStub getStub() {
        return grpcConfig.experienceFundServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    public void saveExperienceFundInfo(SaveExperienceFundInfoRequest request) {
        getStub().saveExperienceFundInfo(request);
    }

    public List<ExperienceFundInfo> queryExperienceFunds(QueryExperienceFundsRequest request) {
        return getStub().queryExperienceFunds(request).getExperienceFundInfoList();
    }

    public List<ExperienceFundTransferRecord> queryTransferList(QueryTransferRecordsRequest request) {
        return getStub().queryTransferRecords(request).getRecordsList();
    }

    public ExperienceFundInfo queryExperienceFundDetail(QueryExperienceFundDetailRequest request) {
        return getStub().queryExperienceFundDetail(request);
    }

    public Map<Long, Boolean> checkAccountJoinedActivity(CheckAccountJoinedActivityRequest request) {
        return getStub().checkAccountJoinedActivity(request).getItemMap();
    }
}
