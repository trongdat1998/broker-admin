package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class RegStatisticDTO {
    private Long id;
    private Long orgId;
    //统计日期 yyyy-MM-dd
    private String statisticDate;
    private Long regNumber = 0L;
    private Long pcRegNumber = 0L;
    private Long androidRegNumber = 0L;
    private Long iosRegNumber = 0L;
    private Long notInvitedRegNumber = 0L;
    private Long pcNotInvitedRegNumber = 0L;
    private Long androidNotInvitedRegNumber = 0L;
    private Long iosNotInvitedRegNumber = 0L;
    private Long inviteRegNumber = 0L;
    private Long pcInviteRegNumber = 0L;
    private Long androidInviteRegNumber = 0L;
    private Long iosInviteRegNumber = 0L;
    private Long directInviteRegNumber = 0L;
    private Long pcDirectInviteRegNumber = 0L;
    private Long androidDirectInviteRegNumber = 0L;
    private Long iosDirectInviteRegNumber = 0L;
    private Long indirectInviteRegNumber = 0L;
    private Long pcIndirectInviteRegNumber = 0L;
    private Long androidIndirectInviteRegNumber = 0L;
    private Long iosIndirectInviteRegNumber = 0L;
    private Long validDirectInviteRegNumber = 0L;
    private Long pcValidDirectInviteRegNumber = 0L;
    private Long androidValidDirectInviteRegNumber = 0L;
    private Long iosValidDirectInviteRegNumber = 0L;
    private Long validIndirectInviteRegNumber = 0L;
    private Long pcValidIndirectInviteRegNumber = 0L;
    private Long androidValidIndirectInviteRegNumber = 0L;
    private Long iosValidIndirectInviteRegNumber = 0L;

}
