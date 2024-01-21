package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2019/1/8 下午4:16
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PositionDto {

    private Long balanceId; //balanceId

    private String tokenId; //tokenId

    private Long accountId; //accountId

    private String symbolId; //tokenId

    private String symbolName; //期权名称

    private Integer isCall; //多空

    private String available; //持仓量

    private String total; //持仓量

    private String margin; // 持仓保证金

    private Long settlementTime; // 交割时间

    private String strikePrice; //行权价格

    private String price; //现价金额

    private String costPrice; //成本金额

    private String availPosition; //可平量

    private String averagePrice; //持仓均价

    private String changedRate; //涨跌幅

    private String changed; //涨跌幅

    private String quoteTokenName;

    private String indices; //标的指数

    private String position;

    private String baseTokenId;

    private String baseTokenName;

    private String quoteTokenId;
}