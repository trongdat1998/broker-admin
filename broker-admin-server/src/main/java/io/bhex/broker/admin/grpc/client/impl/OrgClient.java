package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.account.ExchangeReply;
import io.bhex.base.account.GetExchangesByBrokerReply;
import io.bhex.base.account.GetExchangesByBrokerRequest;
import io.bhex.base.account.OrgServiceGrpc;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class OrgClient {

    @Resource
    GrpcClientConfig grpcConfig;

    public ExchangeReply findExchangeByBrokerId(Long brokerId){
        List<ExchangeReply> exchanges =  findAllExchangeByBrokerId(brokerId);
        if (exchanges.size() == 0) {
            return null;
        }

        if (exchanges.size() == 1) {
            return exchanges.get(0);
        }

        Optional<ExchangeReply> optional = exchanges.stream().filter(e -> e.getIsTrust()).findFirst();
        return optional.orElseGet(() -> exchanges.get(0));
    }

    public ExchangeReply findTrustExchangeByBrokerId(Long brokerId){
        List<ExchangeReply> exchanges =  findAllExchangeByBrokerId(brokerId);
        if (exchanges.size() == 0) {
            return null;
        }

        Optional<ExchangeReply> optional = exchanges.stream().filter(e -> e.getIsTrust()).findFirst();
        return optional.orElse(null);
    }

    public List<ExchangeReply> findAllExchangeByBrokerId(Long brokerId){
        GetExchangesByBrokerRequest req = GetExchangesByBrokerRequest.newBuilder()
                .setBrokerId(brokerId)
                .build();
        OrgServiceGrpc.OrgServiceBlockingStub stub= grpcConfig.orgServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
        GetExchangesByBrokerReply reply = stub.getExchangesByBroker(req);
        return reply.getExchangesList();
    }
}
