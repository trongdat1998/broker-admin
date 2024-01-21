package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName:
 * @Package:
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2019/1/8 下午5:09
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementDto {

    private Long settlementId; //交割id

    private Long accountId;

    private String symbolId; //tokenId

    private String symbolName; //期权名称

    private Integer isCall; //多空

    private String margin; // 持仓保证金

    private Long settlementTime; // 交割时间

    private String strikePrice; //行权价格

    private String settlementPrice; //交割价格

    private String maxPayOff; //最大盈亏

    private String averagePrice; //持仓均价

    private String available; //持仓量

    private String costPrice; //成本金额

    private String changed; //交割收益

    private String changedRate; //涨跌幅

    private String quoteTokenName;

    private String baseTokenId;

    private String baseTokenName;

    private String quoteTokenId;
}
