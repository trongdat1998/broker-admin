package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.NewsClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.news.CreateNewsRequest;
import io.bhex.broker.grpc.news.CreateNewsResponse;
import io.bhex.broker.grpc.news.GetTemplateRequest;
import io.bhex.broker.grpc.news.GetTemplateResponse;
import io.bhex.broker.grpc.news.ModifyNewsRequest;
import io.bhex.broker.grpc.news.ModifyNewsResponse;
import io.bhex.broker.grpc.news.NewsServiceGrpc;
import io.bhex.broker.grpc.news.QueryNewsRequest;
import io.bhex.broker.grpc.news.QueryNewsResponse;
import io.bhex.broker.grpc.news.SetTemplateRequest;
import io.bhex.broker.grpc.news.SetTemplateResponse;
import io.bhex.broker.grpc.news.UpdateStatusRequest;
import io.bhex.broker.grpc.news.UpdateStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class NewsClientImpl implements NewsClient {

    @Resource
    GrpcClientConfig grpcConfig;

    NewsServiceGrpc.NewsServiceBlockingStub getStub() {
        return grpcConfig.newsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public QueryNewsResponse queryNews(QueryNewsRequest request) {
        return getStub().queryNews(request);
    }

    @Override
    public CreateNewsResponse createNews(CreateNewsRequest request) {
        return getStub().createNews(request);
    }

    @Override
    public ModifyNewsResponse modifyNews(ModifyNewsRequest request) {
        return getStub().modifyNews(request);
    }

    @Override
    public UpdateStatusResponse updateStatus(UpdateStatusRequest request) {
        return getStub().updateStatus(request);
    }

    @Override
    public GetTemplateResponse getTemplate(GetTemplateRequest request) {
        return getStub().getTemplate(request);
    }

    @Override
    public SetTemplateResponse setTemplate(SetTemplateRequest request) {
        return getStub().setTemplate(request);
    }
}
