package io.bhex.broker.admin.grpc.client.impl;


import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.grpc.client.ContractCompetitionClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.grpc.activity.contract.competition.AddParticipantRequest;
import io.bhex.broker.grpc.activity.contract.competition.BoolResponse;
import io.bhex.broker.grpc.activity.contract.competition.CompetitionInfo;
import io.bhex.broker.grpc.activity.contract.competition.CompetitionShortUrlRequest;
import io.bhex.broker.grpc.activity.contract.competition.CompetitionShortUrlResponse;
import io.bhex.broker.grpc.activity.contract.competition.ContractCompetitionAbbr;
import io.bhex.broker.grpc.activity.contract.competition.DetailResponse;
import io.bhex.broker.grpc.activity.contract.competition.ListContractCompetitionResponse;
import io.bhex.broker.grpc.activity.contract.competition.ListParticipantResponse;
import io.bhex.broker.grpc.activity.contract.competition.ListRequest;
import io.bhex.broker.grpc.activity.contract.competition.Participant;
import io.bhex.broker.grpc.activity.contract.competition.RankType;
import io.bhex.broker.grpc.activity.contract.competition.RankingListRequest;
import io.bhex.broker.grpc.activity.contract.competition.RankingListResponse;
import io.bhex.broker.grpc.activity.contract.competition.SaveRequest;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static io.bhex.broker.grpc.activity.contract.competition.AdminContractCompetitionServiceGrpc.AdminContractCompetitionServiceBlockingStub;

@Slf4j
@Service
public class ContractCompetitionClientImpl implements ContractCompetitionClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminContractCompetitionServiceBlockingStub getConstractCompetitionStub() {
        return grpcConfig.adminContractCompetitionServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public List<ContractCompetitionAbbr> listContractCompetition(long orgId, int pageNo, int pageSize){
        AdminContractCompetitionServiceBlockingStub stub=getConstractCompetitionStub();
        ListRequest req=ListRequest.newBuilder()
                .setPageNo(pageNo)
                .setPageSize(pageSize)
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        ListContractCompetitionResponse resp=stub.list(req);
        if(resp.getErrorCode()==0){
            return resp.getPage().getListList();
        }

        log.warn("code={},msg={}",resp.getErrorCode(),resp.getErrorMessage());

        throw new BizException(ErrorCode.RPC_CALL_ERROR);
    }

    @Override
    public CompetitionInfo getDetail(long orgId,long id){
        AdminContractCompetitionServiceBlockingStub stub=getConstractCompetitionStub();
        ListRequest req=ListRequest.newBuilder()
                .setExt(id+"")
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        DetailResponse resp=stub.getDetail(req);
        if(resp.getErrorCode()==0){
            return resp.getCompetition();
        }

        log.warn("code={},msg={}",resp.getErrorCode(),resp.getErrorMessage());

        throw new BizException(ErrorCode.RPC_CALL_ERROR);
    }

    @Override
    public RankingListResponse listTop(long orgId,long id,String day,int type){

        log.info("listTop param,orgId={},id={},day={},type={}",orgId,id,day,type);
        AdminContractCompetitionServiceBlockingStub stub=getConstractCompetitionStub();
        RankingListRequest req=RankingListRequest.newBuilder()
                .setActivityId(id)
                .setDay(day)
                .setRankType(RankType.forNumber(type))
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        RankingListResponse resp=stub.rankingList(req);
        if(resp.getErrorCode()==0){
            return resp;
        }

        log.warn("code={},msg={}",resp.getErrorCode(),resp.getErrorMessage());

        throw new BizException(ErrorCode.RPC_CALL_ERROR);
    }


    @Override
    public ListParticipantResponse.Page listParticipant(long orgId,long id, int pageNo, int pageSize){
        AdminContractCompetitionServiceBlockingStub stub=getConstractCompetitionStub();
        ListRequest req=ListRequest.newBuilder()
                .setPageNo(pageNo)
                .setPageSize(pageSize)
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setExt(id+"")
                .build();
        ListParticipantResponse resp=stub.listParticipant(req);
        if(resp.getErrorCode()==0){
            return resp.getPage();
        }

        log.warn("code={},msg={}",resp.getErrorCode(),resp.getErrorMessage());
        throw new BizException(ErrorCode.RPC_CALL_ERROR);
    }



    @Override
    public boolean save(SaveRequest request){
        AdminContractCompetitionServiceBlockingStub stub=getConstractCompetitionStub();
        BoolResponse resp=stub.save(request);
        if(resp.getErrorCode()==0){
            return resp.getSuccess();
        }

        //todo 增加唯一性错误码
        if(resp.getErrorCode()==BrokerErrorCode.DB_RECORD_NOT_UNIQUE.code()){
            throw new BizException("contract.competition.same.event");
        }

        log.warn("code={},msg={}",resp.getErrorCode(),resp.getErrorMessage());
        throw new BizException(ErrorCode.RPC_CALL_ERROR);
    }


    @Override
    public boolean saveParticipant(long orgId,long id,int mode,List<Participant> participants){

        AddParticipantRequest req=AddParticipantRequest.newBuilder()
                .setActivityId(id)
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setMode(AddParticipantRequest.MODE.forNumber(mode))
                .addAllParticipants(participants)
                .build();

        AdminContractCompetitionServiceBlockingStub stub=getConstractCompetitionStub();
        BoolResponse resp=stub.addParitcipant(req);
        if(resp.getErrorCode()==0){
            return resp.getSuccess();
        }

        log.warn("code={},msg={}",resp.getErrorCode(),resp.getErrorMessage());
        throw new BizException(ErrorCode.RPC_CALL_ERROR);
    }


    @Override
    public CompetitionShortUrlResponse getShortUrl(long orgId,long id,String domain){

        CompetitionShortUrlRequest req=CompetitionShortUrlRequest.newBuilder()
                .setId(id)
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setDomain(domain)
                .build();

        AdminContractCompetitionServiceBlockingStub stub=getConstractCompetitionStub();
        CompetitionShortUrlResponse resp=stub.getShortUrl(req);
        if(resp.getErrorCode()==0){
            return resp;
        }

        throw new BizException(ErrorCode.RPC_CALL_ERROR);
    }
}
