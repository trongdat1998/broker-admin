package io.bhex.broker.admin.service;


import io.bhex.broker.admin.controller.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContractCompetitionService {

    List<ContractCompetitionAbbrDTO> listCompetition(long brokerId, int pageNo, int pageSize);

    ContractCompetitionDTO getDetail(long brokerId, long id);

    CompetitionRankingListDTO listTop(long brokerId, long id,int type, String day);

    CompetitionParticipantListDTO listParticipant(long brokerId, long id, int pageNo, int pageSize);

    boolean save(long brokerId, ContractCompetitionDTO param,String domain);

    boolean saveParticipant(long brokerId, long id,List<CompetitionParticipantListDTO.CompetitionParticipantDTO> importList);

    CompetitionShortUrlDTO getShortUrl(long brokerId, Long id, String domain);
}
