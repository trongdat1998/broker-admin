package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class UserInviteInfoDTO {

    private Long userId;

    private String inviteCode;

    private Integer inviteCount;

    private Integer inviteValidCount;

    /**
     * 直接有效数量
     */
    private Integer inviteDirectValidCount;

    /**
     * 间接有效数量
     */
    private Integer inviteIndirectValidCount;

    private Integer inviteLevel;

    private Double directRate;

    private Double indirectRate;

    private Double bonusCoin;

    private Double bonusPoint;

    private Integer inviteHobbitLeaderCount;

}
