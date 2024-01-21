package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityProfileDTO {

    private Long  id;
    private String projectCode;
    private String name;
    private String purchaseToken;
    private String offeringsToken;
    private String offeringsPrice;
    private String totalVolume;
    private Long startTime;
    private Long endTime;
    private Long resultTime;
    //2=分配,3=抢购
    private Integer activityType;
    //1预售,2申购中,3结果计算中,4公布结果,5完成
    private Integer status;
    //0=下线,1=上线
    private Integer isShow;
}
