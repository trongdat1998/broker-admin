package io.bhex.broker.admin.service.impl;


import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.controller.dto.CompetitionParticipantListDTO;
import io.bhex.broker.admin.controller.dto.CompetitionRankingListDTO;
import io.bhex.broker.admin.controller.dto.CompetitionShortUrlDTO;
import io.bhex.broker.admin.controller.dto.ContractCompetitionAbbrDTO;
import io.bhex.broker.admin.controller.dto.ContractCompetitionDTO;
import io.bhex.broker.admin.grpc.client.ContractCompetitionClient;
import io.bhex.broker.admin.service.ContractCompetitionService;
import io.bhex.broker.grpc.activity.contract.competition.AddParticipantRequest;
import io.bhex.broker.grpc.activity.contract.competition.CompetitionInfo;
import io.bhex.broker.grpc.activity.contract.competition.CompetitionShortUrlResponse;
import io.bhex.broker.grpc.activity.contract.competition.ContractCompetitionAbbr;
import io.bhex.broker.grpc.activity.contract.competition.ListParticipantResponse;
import io.bhex.broker.grpc.activity.contract.competition.Participant;
import io.bhex.broker.grpc.activity.contract.competition.RankType;
import io.bhex.broker.grpc.activity.contract.competition.RankingListResponse;
import io.bhex.broker.grpc.activity.contract.competition.SaveRequest;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractCompetitionServiceImpl implements ContractCompetitionService {


    @Resource
    private ContractCompetitionClient contractCompetitionClient;

    @Override
    public List<ContractCompetitionAbbrDTO> listCompetition(long brokerId, int pageNo, int pageSize) {

        List<ContractCompetitionAbbr> list = contractCompetitionClient.listContractCompetition(brokerId, pageNo, pageSize);
        return list.stream().map(i -> {

            List<String> tmp = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(i.getDuration());
            String start = "";
            String end = "";
            if (CollectionUtils.isNotEmpty(tmp)) {
                start = Strings.nullToEmpty(tmp.get(0));
                end = Strings.nullToEmpty(tmp.get(1));
            }
            return ContractCompetitionAbbrDTO.builder()
                    .contractId(i.getContractId()).id(i.getId())
                    .contractName(i.getContractName())
                    .duration(i.getDuration())
                    .tokenId(i.getTokenId())
                    .status(i.getStatus().getNumber())
                    .start(start)
                    .end(end)
                    .code(i.getCode())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public ContractCompetitionDTO getDetail(long brokerId, long id) {

        CompetitionInfo detail = contractCompetitionClient.getDetail(brokerId, id);
        List<Integer> rankTypes = detail.getRankTypesList().stream().map(i -> i.getNumber()).collect(Collectors.toList());
        List<ContractCompetitionDTO.Extend> extendList = detail.getExtendsList().stream().map(i -> {
            return ContractCompetitionDTO.Extend.builder()
                    .appBanner(i.getAppBanner())
                    .language(convertLanguage(i.getLanguage()))
                    .pcBanner(i.getPcBanner())
                    .description(i.getDescription())
                    .build();
        }).collect(Collectors.toList());
        return ContractCompetitionDTO.builder()
                .begin(detail.getBegin())
                .end(detail.getEnd())
                .contractId(detail.getContractId())
                .id(detail.getActivityId())
                .rankNumber(detail.getRankingNumber())
                .qualify(ContractCompetitionDTO.Qualify.builder().
                        position(detail.getQualify().getPosition())
                        .build())
                .rankTypes(rankTypes)
                .extendList(extendList)
                .status(detail.getStatusValue())
                .isReverse(detail.getIsReverse())
                .build();

    }

    private String convertLanguage(String language) {

        if (StringUtils.isBlank(language)) {
            return "";
        }

        List<String> list = Splitter.on("_").trimResults().omitEmptyStrings().splitToList(language);
        return list.get(0) + "-" + list.get(1).toLowerCase();
    }

    @Override
    public CompetitionRankingListDTO listTop(long brokerId, long id, int type, String day) {

        RankingListResponse resp = contractCompetitionClient.listTop(brokerId, id, day, type);

        List<Integer> rankTypes = resp.getRankTypesList().stream().map(i -> i.getNumber()).collect(Collectors.toList());

        List<CompetitionRankingListDTO.Ranking> list = resp.getListList().stream().map(i -> {
                    return CompetitionRankingListDTO.Ranking.builder()
                            .uid(i.getUid())
                            .nickname(i.getNickname())
                            .returnAmount(i.getAmountReturn())
                            .returnRate(i.getRateReturn())
                            .build();
                }
        ).collect(Collectors.toList());

        return CompetitionRankingListDTO.builder()
                .currentType(resp.getCurrentRankTypeValue())
                .days(resp.getDaysList())
                .tokenId(resp.getTokenId())
                .rankTypes(rankTypes)
                .rankingList(list)
                .currentDay(resp.getCurrentDay())
                .build();
    }

    @Override
    public CompetitionParticipantListDTO listParticipant(long brokerId, long id, int pageNo, int pageSize) {
        ListParticipantResponse.Page page = contractCompetitionClient.listParticipant(brokerId, id, pageNo, pageSize);
        List<Participant> list = page.getListList();
        log.info("listParticipant,list size={}", list.size());
        List<CompetitionParticipantListDTO.CompetitionParticipantDTO> tmp = list.stream().map(i -> CompetitionParticipantListDTO.CompetitionParticipantDTO.builder()
                .userId(i.getUid())
                .nickname(i.getNickname())
                .isWhiteList(i.getIsWhiteList())
                .wechat(i.getWechat())
                .build())
                .collect(Collectors.toList());
        log.info("listParticipant after transfer,list size={}", tmp.size());
        return CompetitionParticipantListDTO.builder()
                .total(page.getTotal())
                .list(tmp)
                .build();
    }

    @Override
    public boolean save(long brokerId, ContractCompetitionDTO param, String domain) {

        try {
            Preconditions.checkArgument(Objects.nonNull(param));
            Preconditions.checkArgument(brokerId > 0);

            Preconditions.checkArgument(StringUtils.isNotBlank(param.getBegin()));
            Preconditions.checkArgument(StringUtils.isNotBlank(param.getEnd()));

            Preconditions.checkArgument(StringUtils.isNotBlank(param.getContractId()));
            Preconditions.checkArgument(Objects.nonNull(param.getQualify()));

            Preconditions.checkArgument(Objects.nonNull(param.getRankTypes()));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }

        List<RankType> rankTypes = param.getRankTypes().stream().map(i -> RankType.forNumber(i)).collect(Collectors.toList());
        List<CompetitionInfo.Extend> extendList = param.getExtendList().stream().map(i ->
                CompetitionInfo.Extend.newBuilder()
                        .setLanguage(i.convertLanguage())
                        .setPcBanner(i.getPcBanner())
                        .setAppBanner(i.getAppBanner())
                        .setDescription(i.getDescription())
                        .build()
        ).collect(Collectors.toList());

        CompetitionInfo info = CompetitionInfo.newBuilder()
                .setActivityId(param.getIdSafe())
                .setContractId(param.getContractId())
                .setBegin(param.getBegin())
                .setEnd(param.getEnd())
                .setRankingNumber(param.getRankNumber())
                .addAllRankTypes(rankTypes)
                .setQualify(CompetitionInfo.Qualify.newBuilder()
                        .setPosition(param.getQualify().getPositionSafe())
                        .build())
                .addAllExtends(extendList)
                .setIsReverse(param.getIsReverse() != null ? param.getIsReverse() : 0)
                .build();

        SaveRequest req = SaveRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(brokerId).build())
                .setCompetition(info)
                .setDomain(domain)
                .build();
        return contractCompetitionClient.save(req);
    }

    @Override
    public boolean saveParticipant(long brokerId, long id, List<CompetitionParticipantListDTO.CompetitionParticipantDTO> importList) {

        List<Participant> list = importList.stream().map(i -> Participant.newBuilder()
                .setIsWhiteList(i.isWhiteList())
                .setUid(i.getUserId())
                .setNickname(i.getNickname())
                .build()
        ).collect(Collectors.toList());

        return contractCompetitionClient.saveParticipant(brokerId, id, AddParticipantRequest.MODE.APPEND.getNumber(), list);
    }

    @Override
    public CompetitionShortUrlDTO getShortUrl(long brokerId, Long id, String domain) {
        CompetitionShortUrlResponse resp = contractCompetitionClient.getShortUrl(brokerId, id, domain);

        return CompetitionShortUrlDTO.builder()
                .id(id)
                .shortUrlH5(resp.getH5ShortUrl())
                .shortUrlPc(resp.getPcShortUrl())
                .build()
                ;
    }

/*    public boolean save(long brokerId, long id,int mode) {

        return contractCompetitionClient.saveParticipant(brokerId,id,mode,participants);
    }*/


}
