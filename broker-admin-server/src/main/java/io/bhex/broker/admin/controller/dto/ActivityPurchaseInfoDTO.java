package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityPurchaseInfoDTO {

    private String projectName;

    //申购目标
    private String totalPurchaseVolume;

    //实际申购数量
    private String actualPurchaseVolume;

    //每申购份数可兑换发行币种数量
    private String offingsVolumeEachPurchaseUnit;

    private String realOffingsVolumeEachPurchaseUnit;

    //分配总量
    //募集金额
    //实际分配总量
    //实际募集金额

    private int buyerCount;

    private String realSoldAmount;

    private String realRaiseAmount;
}
