package io.bhex.broker.admin.service;

import com.google.common.base.Strings;
import io.bhex.broker.admin.controller.dto.KycStatisticDTO;
import io.bhex.broker.admin.controller.dto.RegStatisticDTO;
import io.bhex.broker.admin.controller.dto.TokenHoldInfoDTO;
import io.bhex.broker.admin.grpc.client.StatisticClient;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.statistics.AdminQueryTokenHoldTopInfoResponse;
import io.bhex.broker.grpc.statistics.QueryTokenHoldTopInfoRequest;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticService {
    @Autowired
    private StatisticClient statisticClient;

    public RegStatisticDTO queryAggregateRegStatistic(Long brokerId) {
        QueryAggregateRegStatisticRequest request = QueryAggregateRegStatisticRequest.newBuilder()
                .setBrokerId(brokerId).build();
        QueryAggregateRegStatisticReply reply = statisticClient.queryAggregateRegStatistic(request);
        RegStatistic regStatistic = reply.getRegStatistic();
        RegStatisticDTO dto = new RegStatisticDTO();
        BeanUtils.copyProperties(regStatistic, dto);
        return dto;

    }

    public List<RegStatisticDTO> queryDailyRegStatistic(Long brokerId, Long startDate, Long endDate) {
        QueryDailyRegStatisticRequest request = QueryDailyRegStatisticRequest.newBuilder()
                .setBrokerId(brokerId)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .build();
        QueryDailyRegStatisticReply reply = statisticClient.queryDailyRegStatistic(request);
        List<RegStatistic> statistics = reply.getRegStatisticList();
        if (CollectionUtils.isEmpty(statistics)) {
            return new ArrayList<>();
        }
        List<RegStatisticDTO> resultList = statistics.stream().map(regStatistic -> {
            RegStatisticDTO dto = new RegStatisticDTO();
            BeanUtils.copyProperties(regStatistic, dto);
            return dto;
        }).collect(Collectors.toList());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; ; i++) {
            Date date = new DateTime(startDate).plusDays(i).toDate();
            if (date.getTime() > endDate) {
                break;
            }
            String statisticDate = dateFormat.format(date);
            boolean existed = statistics.stream().anyMatch(s -> s.getStatisticDate().equals(statisticDate));
            if (!existed) {
                RegStatisticDTO regStatisticDTO = new RegStatisticDTO();
                regStatisticDTO.setStatisticDate(statisticDate);
                resultList.add(regStatisticDTO);
            }
        }
        resultList = resultList.stream()
                .sorted(Comparator.comparing(RegStatisticDTO::getStatisticDate).reversed())
                .collect(Collectors.toList());

        return resultList;
    }

    public KycStatisticDTO queryAggregateKycStatistic(Long brokerId) {
        QueryAggregateKycStatisticRequest request = QueryAggregateKycStatisticRequest.newBuilder()
                .setBrokerId(brokerId).build();
        QueryAggregateKycStatisticReply reply = statisticClient.queryAggregateKycStatistic(request);
        KycStatistic statistic = reply.getKycStatistic();
        KycStatisticDTO dto = new KycStatisticDTO();
        BeanUtils.copyProperties(statistic, dto);
        return dto;

    }

    public List<KycStatisticDTO> queryDailyKycStatistic(Long brokerId, Long startDate, Long endDate) {
        QueryDailyKycStatisticRequest request = QueryDailyKycStatisticRequest.newBuilder()
                .setBrokerId(brokerId)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .build();
        QueryDailyKycStatisticReply reply = statisticClient.queryDailyKycStatistic(request);
        List<KycStatistic> statistics = reply.getKycStatisticList();
        if (CollectionUtils.isEmpty(statistics)) {
            return new ArrayList<>();
        }
        List<KycStatisticDTO> resultList = statistics.stream().map(regStatistic -> {
            KycStatisticDTO dto = new KycStatisticDTO();
            BeanUtils.copyProperties(regStatistic, dto);
            return dto;
        }).collect(Collectors.toList());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0;   ; i++) {
            Date date = new DateTime(startDate).plusDays(i).toDate();
            if (date.getTime() > endDate) {
                break;
            }
            String statisticDate = dateFormat.format(date);
            boolean existed = statistics.stream().anyMatch(s -> s.getStatisticDate().equals(statisticDate));
            if (!existed) {
                KycStatisticDTO kycStatisticDTO = new KycStatisticDTO();
                kycStatisticDTO.setStatisticDate(statisticDate);
                resultList.add(kycStatisticDTO);
            }
        }
        resultList = resultList.stream()
                .sorted(Comparator.comparing(KycStatisticDTO::getStatisticDate).reversed())
                .collect(Collectors.toList());

        return resultList;
    }

    public List<TokenHoldInfoDTO> queryOrgBalanceTop(long orgId, String tokenId, long userId, int top) {
        QueryTokenHoldTopInfoRequest request = QueryTokenHoldTopInfoRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setTokenId(Strings.nullToEmpty(tokenId))
                .setTop(top)
                .build();
        List<AdminQueryTokenHoldTopInfoResponse.TokenHoldInfo> list = statisticClient
                .adminQueryTokenHoldTopInfo(request).getTokenHoldInfoList();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(l -> {
            TokenHoldInfoDTO dto = new TokenHoldInfoDTO();
            dto.setBalanceId(l.getBalanceId());
            dto.setTokenId(l.getTokenId());
            dto.setUserId(l.getUserId());
            dto.setTotal(new BigDecimal(l.getTotal()));
            return dto;
        }).collect(Collectors.toList());

    }
}
