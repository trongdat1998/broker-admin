package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.bhadmin.*;

import io.bhex.base.token.GetSymbolRequest;
import io.bhex.base.token.SymbolDetail;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.controller.dto.ContractApplyDTO;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.admin.util.RedisLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ContractSymbolClient {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SymbolClient symbolClient;

    /**
     * 新建期权生成自增symbolId锁
     *
     * 自增symbolId规则：展示标的id + 6位日期（191010）+ 4位自增id
     *
     * 此处锁用来控制自增id
     */
    private final static String FUTURES_RECORD_INSERT_LOCK_TP = "futures_record_insert_lock_%s";

    private final static String FUTURES_RECORD_ID_SEQUENCE = "futures_record_id_sequence_%s";

    @Resource
    GrpcClientConfig grpcConfig;

    AdminSymbolApplyServiceGrpc.AdminSymbolApplyServiceBlockingStub applyStub() {
        return grpcConfig.adminSymbolApplyServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

    public PaginationVO<ContractApplyDTO> listSymbolRecordList(Long brokerId, Integer current, Integer pageSize) {
        GetSymbolPager pager = GetSymbolPager.newBuilder()
                .setBrokerId(brokerId)
                .setStart(current)
                .setSize(pageSize)
                .setState(-1)
                .build();

        ContractApplyList list = applyStub().listContractApplyRecord(pager);
        PaginationVO<ContractApplyDTO> resultPagination = new PaginationVO<>();
        resultPagination.setList(list.getRecordList()
                .stream()
                .map(ContractApplyDTO::parseFromProtoObj)
                .collect(Collectors.toList()));
        resultPagination.setCurrent(current);
        resultPagination.setTotal(list.getTotal());
        resultPagination.setPageSize(pageSize);
        return resultPagination;
    }


    public int saveSymbolRecord(ContractApplyObj contractApplyObj) {
        if (contractApplyObj.getId() == 0) {
            SymbolDetail symbolDetail = null;
            try {
                symbolDetail = symbolClient.getBhSymbolInfo(GetSymbolRequest.newBuilder()
                        .setSymbolId(contractApplyObj.getSymbolId())
                        .build());
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            if (Objects.nonNull(symbolDetail) && StringUtils.isNotEmpty(symbolDetail.getSymbolId())) {
                throw new BizException(ErrorCode.SYMBOL_ALREADY_EXIST);
            }
        }
        ContractApplyResult res = applyStub().applyContract(contractApplyObj);
        int result = res.getRes();
        if (result == -1) {
            throw new BizException(ErrorCode.SYMBOL_ALREADY_EXIST);
        }
        if (result == -2) {
            throw new BizException(ErrorCode.FORBIDDEN_EDIT);
        }
        return result;
    }

    public String nextFuturesSymbolId(String displayUnderlyingId) {
        String lockKey = null;
        try {
            // 日期只要6位，年月日，两位年 如：191010
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
            String date = sdf.format(new Date());
            // 这两个redis key都是按天更新
            lockKey = String.format(FUTURES_RECORD_INSERT_LOCK_TP, date);
            String sequenceKey = String.format(FUTURES_RECORD_ID_SEQUENCE, date);

            // 重试三次
            boolean b = RedisLockUtils.tryLockAlways(redisTemplate, date, 24 * 60 * 60 * 1000, 3);

            String sequenceId;
            String s = redisTemplate.opsForValue().get(sequenceKey);
            if (StringUtils.isEmpty(s)) {
                sequenceId = toXDigitNum(4, 0);
            } else {
                sequenceId = toXDigitNum(4, Integer.valueOf(s) + 1);
            }
            redisTemplate.opsForValue().set(sequenceKey, sequenceId, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
            return displayUnderlyingId.concat(date).concat(sequenceId);
        } catch (Exception e) {
            log.error("Get Next Futures SymbolId error.");
        } finally {
            RedisLockUtils.releaseLock(redisTemplate, lockKey);
        }
        return null;
    }

    private static String toXDigitNum(Integer digits, Integer num) {
        String numStr = String.valueOf(num);
        if (digits < numStr.length()) {
            return numStr;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits-numStr.length(); i++) {
                sb.append("0");
            }
            sb.append(numStr);
            return sb.toString();
        }
    }

}
