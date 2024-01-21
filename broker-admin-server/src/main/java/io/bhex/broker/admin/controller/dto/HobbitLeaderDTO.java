package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Builder(builderClassName = "builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HobbitLeaderDTO {

    private Long id;

    private String time;

    private Long orgId;

    private Long userId;

    private String remark;

    private String name;

    private Map<String, String> contactInfo;

    private Integer quitApplyStatus; //0-init 1-退出通过 2-退出拒绝 3-退出完成
    private Long quitApplyTime;  //退出申请时间
    private Long quitPassedTime; //退出时间  quitApplyStatus=1的时间
}
