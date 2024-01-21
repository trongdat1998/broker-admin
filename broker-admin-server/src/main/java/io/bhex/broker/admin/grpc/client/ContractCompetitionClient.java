package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.activity.contract.competition.*;

import java.util.List;

public interface ContractCompetitionClient {

    List<ContractCompetitionAbbr> listContractCompetition(long orgId, int pageNo, int pageSize);

    CompetitionInfo getDetail(long orgId, long id);

    RankingListResponse listTop(long orgId, long id, String day, int type);

    ListParticipantResponse.Page listParticipant(long orgId, long id, int pageNo, int pageSize);

    boolean save(SaveRequest request);

    boolean saveParticipant(long orgId,long id,int mode,List<Participant> participants);

    CompetitionShortUrlResponse getShortUrl(long orgId,long id,String domain);
}
