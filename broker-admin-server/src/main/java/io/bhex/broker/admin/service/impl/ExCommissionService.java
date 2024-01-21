package io.bhex.broker.admin.service.impl;

import io.bhex.base.account.*;
import io.bhex.base.exception.ErrorStatusRuntimeException;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.grpc.client.AdminUserClient;
import io.bhex.broker.admin.controller.dto.ExCommissionFeeDTO;
import io.bhex.broker.admin.controller.param.ExCommissionFeePO;
import io.bhex.broker.admin.grpc.client.impl.ExFeeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExCommissionService {


    @Autowired
    private AdminUserClient adminUserClient;

    @Autowired
    private ExFeeClient exFeeClient;

    public static Integer COMMISSION_TYPE_COIN = 1;
    public static Integer COMMISSION_TYPE_OPTION = 3;
    public static Integer COMMISSION_TYPE_FUTURES = 4;




    /**
     * 获取 - 交易所佣金分成比率
     *
     * @param exchangeId
     * @return
     */
    public ExCommissionFeeDTO getExCommissionFee(Long exchangeId, Integer commissionType) {
        GetExCommissionFeeRequest request = GetExCommissionFeeRequest.newBuilder()
                .setExchangeId(exchangeId)
                .setCommissionType(commissionType)
                .build();
        GetExCommissionFeeResponse exCommissionFeeResponse = exFeeClient.getExCommissionFee(request);
        return ExCommissionFeeDTO.builder()
                .exchangeId(exchangeId)
                .commissionRate(DecimalUtil.toBigDecimal(exCommissionFeeResponse.getCommissionRate()))
                .build();
    }

    /**
     * 更新 - 交易所佣金分成比率
     *
     * @param param
     * @return
     */
    public int updateExcommissionFee(ExCommissionFeePO param, Integer commissionType) {
        UpdateExCommissionFeeRequest request = UpdateExCommissionFeeRequest.newBuilder()
                .setExchangeId(param.getExchangeId())
                .setCommissionRate(DecimalUtil.fromBigDecimal(param.getCommissionRate()))
                .setCommissionType(commissionType)
                .build();
        try{
            UpdateExCommissionFeeResponse response = exFeeClient.updateExCommissionFee(request);
            log.info("req:{} res:{}", request, response);
            return response.getErrCode();
        }
        catch (ErrorStatusRuntimeException e){
            log.info("updateExcommissionFee failed",e);
            return e.getCode().getNumber();
        }
    }

    /**
     * 获取 - 交易所撮合费率
     *
     * @param exchangeId
     * @return
     */
    public ExCommissionFeeDTO getMatchCommissionFee(Long exchangeId, Integer commissionType) {
        GetMatchCommissionFeeRequest request = GetMatchCommissionFeeRequest.newBuilder()
                .setExchangeId(exchangeId)
                .setCommissionType(commissionType)
                .build();
        GetMatchCommissionFeeResponse matchCommissionFeeResponse = exFeeClient.getMatchCommissionFee(request);
        return ExCommissionFeeDTO.builder()
                .exchangeId(exchangeId)
                .commissionRate(DecimalUtil.toBigDecimal(matchCommissionFeeResponse.getCommissionRate()))
                .build();
    }

    /**
     * 更新 - 交易所撮合费率
     *
     * @param param
     * @return
     */
    public int updateMatchcommissionFee(ExCommissionFeePO param, Integer commissionType) {
        UpdateMatchCommissionFeeRequest request = UpdateMatchCommissionFeeRequest.newBuilder()
                .setExchangeId(param.getExchangeId())
                .setCommissionRate(DecimalUtil.fromBigDecimal(param.getCommissionRate()))
                .setCommissionType(commissionType)
                .build();
        try{
            UpdateMatchCommissionFeeResponse response = exFeeClient.updateMatchCommissionFee(request);
            return response.getErrCode();
        } catch (ErrorStatusRuntimeException e){
            log.info("updateExcommissionFee failed",e);
            return e.getCode().getNumber();
        }
    }

}
