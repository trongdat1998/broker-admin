package io.bhex.broker.admin.service.impl;

import io.bhex.base.proto.Decimal;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.base.quote.GetLegalCoinRatesReply;
import io.bhex.base.quote.GetRatesRequest;
import io.bhex.base.quote.Rate;
import io.bhex.base.token.TokenCategory;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.grpc.client.TokenClient;
import io.bhex.broker.admin.service.GrpcQuoteService;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.admin.QueryTokenReply;
import io.bhex.broker.grpc.admin.TokenDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RateService {

    private static final String USDT_TOKEN_ID = "USDT";

    @Autowired
    private TokenClient tokenClient;
    @Resource
    private GrpcQuoteService grpcQuoteService;



    private BigDecimal getUSDTRate(String tokenId, Map<String, Rate> ratesMap) {
        if (Objects.isNull(ratesMap) || Objects.isNull(ratesMap.get(tokenId))) {
            throw new BizException(ErrorCode.EXCHANGE_RATE_ERROR);
        }
        Rate rate = ratesMap.get(tokenId);
        if (Objects.nonNull(rate)) {
            Decimal usdtRate = rate.getRatesMap().get(USDT_TOKEN_ID);
            if (Objects.nonNull(usdtRate)) {
                return DecimalUtil.toBigDecimal(usdtRate);
            }
        }
        return null;
    }

    public BigDecimal convertUSDTRate(Long brokerId, String tokenId) {
        Map<String, Rate> ratesMap = getRate(brokerId, Arrays.asList(tokenId));
        if (Objects.isNull(ratesMap) || Objects.isNull(ratesMap.get(tokenId))) {
            throw new BizException(ErrorCode.EXCHANGE_RATE_ERROR);
        }
        Rate rate = ratesMap.get(tokenId);
        if (Objects.nonNull(rate)) {
            Decimal usdtRate = rate.getRatesMap().get(USDT_TOKEN_ID);
            if (Objects.nonNull(usdtRate)) {
                return DecimalUtil.toBigDecimal(usdtRate);
            }
        }
        return null;
    }

    public Map<String, Rate> getRate(Long brokerId, List<String> tokenIdList) {
        try {
            List<TokenDetail> tokenDetails = new ArrayList<>();
            tokenIdList.forEach(t -> {
                tokenDetails.add(getBrokerTokenDetail(t, brokerId));
            });
            List<io.bhex.base.quote.Token> quoteToken = tokenDetails.stream().map(token -> io.bhex.base.quote.Token.newBuilder()
                    .setExchangeId(token.getExchangeId()).setToken(token.getTokenId()).build()).collect(Collectors.toList());
            GetRatesRequest request = GetRatesRequest.newBuilder()
                    .addAllTokens(quoteToken)
                    .build();
            GetLegalCoinRatesReply reply = grpcQuoteService.getRates(request);
            Map<String, Rate> ratesMap = reply.getRatesMapMap();
            log.info("getRate: rate map => {}.", JsonUtil.defaultGson().toJson(ratesMap));
            return ratesMap;
        } catch (Exception e) {
            log.error("Get token FXRate occurred a exception", e);
            return null;
        }
    }

    public TokenDetail getBrokerTokenDetail(String tokenId, Long brokerId) {
        QueryTokenReply queryTokenReply = tokenClient.queryToken(0, 1, TokenCategory.MAIN_CATEGORY_VALUE, tokenId, "", brokerId);
        List<TokenDetail> tokenDetailsList = queryTokenReply.getTokenDetailsList();
        log.info("broker:{} token:{} detail:{}", brokerId, tokenId, tokenDetailsList);
        if (!CollectionUtils.isEmpty(tokenDetailsList)) {
            return tokenDetailsList.get(0);
        }
        return null;
    }
}
