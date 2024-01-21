package io.bhex.broker.admin.grpc.client.impl;


import io.bhex.broker.admin.grpc.client.NoticeTemplateClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.notice.NoticeTemplateServiceGrpc;
import io.bhex.broker.grpc.notice.NoticesBusinessType;
import io.bhex.broker.grpc.notice.QueryNoticeTemplateRequest;
import io.bhex.broker.grpc.notice.QueryNoticeTemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class NoticeTemplateClientImpl implements NoticeTemplateClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public Long getTemplateId(Long brokerId, int noticeType, NoticesBusinessType.TypeEnum businessType, String language) {
        NoticeTemplateServiceGrpc.NoticeTemplateServiceBlockingStub stub =
                grpcConfig.noticeTemplateServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);

        QueryNoticeTemplateRequest request = QueryNoticeTemplateRequest.newBuilder()
                .setOrgId(brokerId)
                .setNoticeType(noticeType)
                .setBusinessType(businessType)
                .setLanguage(language)
                .build();

        QueryNoticeTemplateResponse response = stub.queryNoticeTemplate(request);
        return response.getTemplateId();
    }
}
