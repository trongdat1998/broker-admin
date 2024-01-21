package io.bhex.broker.admin.grpc.client.impl;

import com.google.gson.Gson;
import io.bhex.base.account.GetBrokerExchangeContractReply;
import io.bhex.base.account.GetBrokerExchangeContractRequest;
import io.bhex.base.account.OrgServiceGrpc;
import io.bhex.broker.admin.controller.dto.DiscountFeeConfigDTO;
import io.bhex.broker.admin.controller.dto.DiscountFeeUserDTO;
import io.bhex.broker.admin.controller.dto.SymbolFeeConfigDTO;
import io.bhex.broker.admin.controller.dto.SymbolMarketAccountDTO;
import io.bhex.broker.admin.controller.param.DiscountFeeConfigPO;
import io.bhex.broker.admin.controller.param.SymbolMarketAccountDetailPO;
import io.bhex.broker.admin.grpc.client.BrokerFeeClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.fee.AddDiscountFeeConfigRequest;
import io.bhex.broker.grpc.fee.AddDiscountFeeConfigResponse;
import io.bhex.broker.grpc.fee.AddSymbolFeeConfigRequest;
import io.bhex.broker.grpc.fee.AddSymbolFeeConfigResponse;
import io.bhex.broker.grpc.fee.CancelUserDiscountConfigRequest;
import io.bhex.broker.grpc.fee.CancelUserDiscountConfigResponse;
import io.bhex.broker.grpc.fee.DeleteAccountTradeFeeConfigRequest;
import io.bhex.broker.grpc.fee.DeleteAccountTradeFeeConfigResponse;
import io.bhex.broker.grpc.fee.FeeServiceGrpc;
import io.bhex.broker.grpc.fee.QueryAccountTradeFeeConfigRequest;
import io.bhex.broker.grpc.fee.QueryAccountTradeFeeConfigResponse;
import io.bhex.broker.grpc.fee.QueryDiscountFeeConfigRequest;
import io.bhex.broker.grpc.fee.QueryDiscountFeeConfigResponse;
import io.bhex.broker.grpc.fee.QueryOneDiscountFeeConfigRequest;
import io.bhex.broker.grpc.fee.QueryOneDiscountFeeConfigResponse;
import io.bhex.broker.grpc.fee.QuerySymbolFeeConfigRequest;
import io.bhex.broker.grpc.fee.QuerySymbolFeeConfigResponse;
import io.bhex.broker.grpc.fee.QueryUserDiscountConfigRequest;
import io.bhex.broker.grpc.fee.QueryUserDiscountConfigResponse;
import io.bhex.broker.grpc.fee.SaveSymbolMarketAccountRequest;
import io.bhex.broker.grpc.fee.SaveSymbolMarketAccountResponse;
import io.bhex.broker.grpc.fee.SaveUserDiscountConfigRequest;
import io.bhex.broker.grpc.fee.SaveUserDiscountConfigResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class BrokerFeeClientImpl implements BrokerFeeClient {


    @Resource
    GrpcClientConfig grpcConfig;

    private OrgServiceGrpc.OrgServiceBlockingStub getOrgStub() {
        return grpcConfig.orgServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
    }

    private FeeServiceGrpc.FeeServiceBlockingStub feeServiceBlockingStub() {
        return grpcConfig.feeServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }


    @Override
    public AddDiscountFeeConfigResponse addDiscountFeeConfig(DiscountFeeConfigPO request) {
        AddDiscountFeeConfigResponse discountFeeConfigResponse = feeServiceBlockingStub().addDiscountFeeConfig(AddDiscountFeeConfigRequest
                .newBuilder()
                .setOrgId(request.getOrgId())
                .setId(request.getId() > 0 ? request.getId() : 0)
                .setExchangeId(request.getExchangeId() != null ? request.getExchangeId() : 0L)
                .setSymbolId(request.getSymbolId() != null ? request.getSymbolId() : "")
                .setName(request.getName())
                .setMark(request.getMark())
                .setStatus(request.getStatus() != null ? request.getStatus() : 1)
                .setCoinMakerBuyFeeDiscount(request.getCoinMakerBuyFeeDiscount() != null ? request.getCoinMakerBuyFeeDiscount().stripTrailingZeros().toPlainString() : "")
                .setCoinMakerSellFeeDiscount(request.getCoinMakerSellFeeDiscount() != null ? request.getCoinMakerSellFeeDiscount().stripTrailingZeros().toPlainString() : "")
                .setCoinTakerBuyFeeDiscount(request.getCoinTakerBuyFeeDiscount() != null ? request.getCoinTakerBuyFeeDiscount().stripTrailingZeros().toPlainString() : "")
                .setCoinTakerSellFeeDiscount(request.getCoinTakerSellFeeDiscount() != null ? request.getCoinTakerSellFeeDiscount().stripTrailingZeros().toPlainString() : "")
//                .setOptionMakerBuyFeeDiscount(request.getOptionMakerBuyFeeDiscount() != null ? request.getOptionMakerBuyFeeDiscount().stripTrailingZeros().toPlainString() : "")
//                .setOptionMakerSellFeeDiscount(request.getOptionMakerSellFeeDiscount() != null ? request.getOptionMakerSellFeeDiscount().stripTrailingZeros().toPlainString() : "")
//                .setOptionTakerBuyFeeDiscount(request.getOptionTakerBuyFeeDiscount() != null ? request.getOptionTakerBuyFeeDiscount().stripTrailingZeros().toPlainString() : "")
//                .setOptionTakerSellFeeDiscount(request.getOptionTakerSellFeeDiscount() != null ? request.getOptionTakerSellFeeDiscount().stripTrailingZeros().toPlainString() : "")
                .setContractMakerBuyFeeDiscount(request.getContractMakerBuyFeeDiscount() != null ? request.getContractMakerBuyFeeDiscount().stripTrailingZeros().toPlainString() : "")
                .setContractMakerSellFeeDiscount(request.getContractMakerSellFeeDiscount() != null ? request.getContractMakerSellFeeDiscount().stripTrailingZeros().toPlainString() : "")
                .setContractTakerBuyFeeDiscount(request.getContractTakerBuyFeeDiscount() != null ? request.getContractTakerBuyFeeDiscount().stripTrailingZeros().toPlainString() : "")
                .setContractTakerSellFeeDiscount(request.getContractTakerSellFeeDiscount() != null ? request.getContractTakerSellFeeDiscount().stripTrailingZeros().toPlainString() : "")
                .setUserList(StringUtils.isNotEmpty(request.getUserList()) ? request.getUserList() : "")
                .build());
        return discountFeeConfigResponse;
    }

    @Override
    public List<DiscountFeeConfigDTO> queryDiscountFeeConfigList(QueryDiscountFeeConfigRequest request) {
        QueryDiscountFeeConfigResponse queryDiscountFeeConfigResponse = feeServiceBlockingStub().queryDiscountFeeConfigList(request);

        List<DiscountFeeConfigDTO> discountFeeConfigDTOList = new ArrayList<>();

        queryDiscountFeeConfigResponse.getDiscountConfigList().forEach(discountConfig -> {
            discountFeeConfigDTOList.add(DiscountFeeConfigDTO
                    .builder()
                    .orgId(request.getOrgId())
                    .exchangeId(discountConfig.getExchangeId())
                    .symbolId(discountConfig.getSymbolId())
                    .type(discountConfig.getType())
                    .id(discountConfig.getId() > 0 ? discountConfig.getId() : 0)
                    .name(discountConfig.getName())
                    .mark(discountConfig.getMark())
                    .status(discountConfig.getStatus())
                    .coinMakerBuyFeeDiscount(StringUtils.isNotEmpty(discountConfig.getCoinMakerBuyFeeDiscount()) ? new BigDecimal(discountConfig.getCoinMakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .coinMakerSellFeeDiscount(StringUtils.isNotEmpty(discountConfig.getCoinMakerSellFeeDiscount()) ? new BigDecimal(discountConfig.getCoinMakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .coinTakerBuyFeeDiscount(StringUtils.isNotEmpty(discountConfig.getCoinTakerBuyFeeDiscount()) ? new BigDecimal(discountConfig.getCoinTakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .coinTakerSellFeeDiscount(StringUtils.isNotEmpty(discountConfig.getCoinTakerSellFeeDiscount()) ? new BigDecimal(discountConfig.getCoinTakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .optionMakerBuyFeeDiscount(StringUtils.isNotEmpty(discountConfig.getOptionMakerBuyFeeDiscount()) ? new BigDecimal(discountConfig.getOptionMakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .optionMakerSellFeeDiscount(StringUtils.isNotEmpty(discountConfig.getOptionMakerSellFeeDiscount()) ? new BigDecimal(discountConfig.getOptionMakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .optionTakerBuyFeeDiscount(StringUtils.isNotEmpty(discountConfig.getOptionTakerBuyFeeDiscount()) ? new BigDecimal(discountConfig.getOptionTakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .optionTakerSellFeeDiscount(StringUtils.isNotEmpty(discountConfig.getOptionTakerSellFeeDiscount()) ? new BigDecimal(discountConfig.getOptionTakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .contractMakerBuyFeeDiscount(StringUtils.isNotEmpty(discountConfig.getContractMakerBuyFeeDiscount()) ? new BigDecimal(discountConfig.getContractMakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .contractMakerSellFeeDiscount(StringUtils.isNotEmpty(discountConfig.getContractMakerSellFeeDiscount()) ? new BigDecimal(discountConfig.getContractMakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .contractTakerBuyFeeDiscount(StringUtils.isNotEmpty(discountConfig.getContractTakerBuyFeeDiscount()) ? new BigDecimal(discountConfig.getContractTakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .contractTakerSellFeeDiscount(StringUtils.isNotEmpty(discountConfig.getContractTakerSellFeeDiscount()) ? new BigDecimal(discountConfig.getContractTakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .userList(StringUtils.isNotEmpty(discountConfig.getUserList()) ? discountConfig.getUserList() : "")
                    .build());
        });

        return discountFeeConfigDTOList;
    }

    @Override
    public DiscountFeeConfigDTO queryOneDiscountFeeConfig(QueryOneDiscountFeeConfigRequest request) {
        QueryOneDiscountFeeConfigResponse response = feeServiceBlockingStub().queryOneDiscountFeeConfig(request);
        if (response != null) {
            DiscountFeeConfigDTO discountFeeConfig = DiscountFeeConfigDTO
                    .builder()
                    .orgId(request.getOrgId())
                    .id(response.getId() > 0 ? response.getId() : 0)
                    .exchangeId(response.getExchangeId())
                    .symbolId(response.getSymbolId())
                    .type(response.getType())
                    .name(response.getName())
                    .mark(response.getMark())
                    .coinMakerBuyFeeDiscount(StringUtils.isNotEmpty(response.getCoinMakerBuyFeeDiscount()) ? new BigDecimal(response.getCoinMakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .coinMakerSellFeeDiscount(StringUtils.isNotEmpty(response.getCoinMakerSellFeeDiscount()) ? new BigDecimal(response.getCoinMakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .coinTakerBuyFeeDiscount(StringUtils.isNotEmpty(response.getCoinTakerBuyFeeDiscount()) ? new BigDecimal(response.getCoinTakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .coinTakerSellFeeDiscount(StringUtils.isNotEmpty(response.getCoinTakerSellFeeDiscount()) ? new BigDecimal(response.getCoinTakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .optionMakerBuyFeeDiscount(StringUtils.isNotEmpty(response.getOptionMakerBuyFeeDiscount()) ? new BigDecimal(response.getOptionMakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .optionMakerSellFeeDiscount(StringUtils.isNotEmpty(response.getOptionMakerSellFeeDiscount()) ? new BigDecimal(response.getOptionMakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .optionTakerBuyFeeDiscount(StringUtils.isNotEmpty(response.getOptionTakerBuyFeeDiscount()) ? new BigDecimal(response.getOptionTakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .optionTakerSellFeeDiscount(StringUtils.isNotEmpty(response.getOptionTakerSellFeeDiscount()) ? new BigDecimal(response.getOptionTakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .contractMakerBuyFeeDiscount(StringUtils.isNotEmpty(response.getContractMakerBuyFeeDiscount()) ? new BigDecimal(response.getContractMakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .contractMakerSellFeeDiscount(StringUtils.isNotEmpty(response.getContractMakerSellFeeDiscount()) ? new BigDecimal(response.getContractMakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .contractTakerBuyFeeDiscount(StringUtils.isNotEmpty(response.getContractTakerBuyFeeDiscount()) ? new BigDecimal(response.getContractTakerBuyFeeDiscount()) : BigDecimal.ZERO)
                    .contractTakerSellFeeDiscount(StringUtils.isNotEmpty(response.getContractTakerSellFeeDiscount()) ? new BigDecimal(response.getContractTakerSellFeeDiscount()) : BigDecimal.ZERO)
                    .build();
            return discountFeeConfig;
        }
        return DiscountFeeConfigDTO.builder().build();
    }

    @Override
    public SaveUserDiscountConfigResponse saveUserDiscountConfig(SaveUserDiscountConfigRequest request) {
        return feeServiceBlockingStub().saveUserDiscountConfig(request);
    }

    @Override
    public CancelUserDiscountConfigResponse cancelUserDiscountConfig(CancelUserDiscountConfigRequest request) {
        return feeServiceBlockingStub().cancelUserDiscountConfig(request);
    }

    @Override
    public DiscountFeeUserDTO queryUserDiscountConfig(QueryUserDiscountConfigRequest request) {
        QueryUserDiscountConfigResponse response = feeServiceBlockingStub().queryUserDiscountConfig(request);
        if (response != null) {
            return DiscountFeeUserDTO.builder()
                    .useId(response.getUserId())
                    .baseGroupId(response.getBaseGroupId())
                    .temporaryGroupId(response.getTemporaryGroupId())
                    .baseGroupName(response.getBaseGroupName())
                    .temporaryGroupName(response.getTemporaryGroupName())
                    .status(response.getStatus())
                    .build();
        }
        return null;
    }

    @Override
    public AddSymbolFeeConfigResponse addSymbolFeeConfig(AddSymbolFeeConfigRequest request) {
        return feeServiceBlockingStub().addSymbolFeeConfig(request);
    }

    @Override
    public List<SymbolFeeConfigDTO> querySymbolFeeConfigList(QuerySymbolFeeConfigRequest request) {
        QuerySymbolFeeConfigResponse response = feeServiceBlockingStub().querySymbolFeeConfigList(request);

        List<SymbolFeeConfigDTO> symbolFeeConfigDTOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(response.getSymbolFeeList())) {
            return new ArrayList<>();
        }

        response.getSymbolFeeList().forEach(fee -> {
            symbolFeeConfigDTOList.add(SymbolFeeConfigDTO.builder()
                    .id(fee.getId())
                    .symbolId(fee.getSymbolId())
                    .orgId(fee.getOrgId())
                    .makerBuyFee(StringUtils.isNotEmpty(fee.getMakerBuyFee()) ? new BigDecimal(fee.getMakerBuyFee()) : BigDecimal.ZERO)
                    .makerSellFee(StringUtils.isNotEmpty(fee.getMakerSellFee()) ? new BigDecimal(fee.getMakerSellFee()) : BigDecimal.ZERO)
                    .takerBuyFee(StringUtils.isNotEmpty(fee.getTakerBuyFee()) ? new BigDecimal(fee.getTakerBuyFee()) : BigDecimal.ZERO)
                    .takerSellFee(StringUtils.isNotEmpty(fee.getTakerSellFee()) ? new BigDecimal(fee.getTakerSellFee()) : BigDecimal.ZERO)
                    .status(fee.getStatus())
                    .created(fee.getCreated())
                    .updated(fee.getUpdated())
                    .baseTokenId(fee.getBaseTokenId())
                    .quoteTokenId(fee.getQuoteTokenId())
                    .build());
        });
        return symbolFeeConfigDTOList;
    }


    @Override
    public SaveSymbolMarketAccountResponse saveSymbolMarketAccount(Long orgId, List<SymbolMarketAccountDetailPO> marketAccountList) {
        if (CollectionUtils.isEmpty(marketAccountList)) {
            return SaveSymbolMarketAccountResponse.getDefaultInstance();
        }
        log.info("saveSymbolMarketAccount info {}", new Gson().toJson(marketAccountList));
        List<SaveSymbolMarketAccountRequest.MarketAccount> marketAccount = new ArrayList<>();
        marketAccountList.forEach(account -> {
            marketAccount.add(SaveSymbolMarketAccountRequest.MarketAccount.newBuilder()
                    .setId(account.getId() != null ? account.getId() : 0L)
                    .setOrgId(orgId)
                    .setSymbolId(account.getSymbolId())
                    .setAccountId(account.getAccountId())
                    .setMakerBuyFee(account.getMakerBuyFee())
                    .setMakerSellFee(account.getMakerSellFee())
                    .setTakerBuyFee(account.getTakerBuyFee())
                    .setTakerSellFee(account.getTakerSellFee())
                    .build());
        });

        if (CollectionUtils.isEmpty(marketAccount)) {
            return SaveSymbolMarketAccountResponse.getDefaultInstance();
        }
        return feeServiceBlockingStub().saveSymbolMarketAccount(SaveSymbolMarketAccountRequest.newBuilder().addAllSymbolFee(marketAccount).build());
    }

    @Override
    public List<SymbolMarketAccountDTO> queryAllSymbolMarketAccount(QueryAccountTradeFeeConfigRequest request) {
        QueryAccountTradeFeeConfigResponse response = feeServiceBlockingStub().queryAccountTradeFeeConfig(request);
        if (response.getConfigsCount() == 0) {
            return new ArrayList<>();
        }
        List<SymbolMarketAccountDTO> symbolMarketAccountList = new ArrayList<>();
        response.getConfigsList().forEach(symbolMarketAccount -> {
            symbolMarketAccountList.add(SymbolMarketAccountDTO
                    .builder()
                    .accountId(symbolMarketAccount.getAccountId())
                    .orgId(symbolMarketAccount.getOrgId())
                    .id(symbolMarketAccount.getId())
                    .symbolId(symbolMarketAccount.getSymbolId())
                    .makerBuyFeeRate(symbolMarketAccount.getMakerBuyFeeRate())
                    .makerSellFeeRate(symbolMarketAccount.getMakerSellFeeRate())
                    .takerBuyFeeRate(symbolMarketAccount.getTakerBuyFeeRate())
                    .takerSellFeeRate(symbolMarketAccount.getTakerSellFeeRate())
                    .exchangeId(symbolMarketAccount.getExchangeId())
                    .build());
        });
        log.info("symbolMarketAccountList ", new Gson().toJson(symbolMarketAccountList));
        return symbolMarketAccountList;
    }

    @Override
    public GetBrokerExchangeContractReply getBrokerExchangeContract(GetBrokerExchangeContractRequest request) {
        return getOrgStub().getBrokerExchangeContract(request);
    }

    @Override
    public DeleteAccountTradeFeeConfigResponse deleteSymbolMarketAccount(DeleteAccountTradeFeeConfigRequest request) {
        return feeServiceBlockingStub().deleteAccountTradeFeeConfig(request);
    }
}
