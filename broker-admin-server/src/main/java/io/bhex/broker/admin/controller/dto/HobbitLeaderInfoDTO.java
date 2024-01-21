package io.bhex.broker.admin.controller.dto;

import lombok.Data;

@Data
public class HobbitLeaderInfoDTO {

    private String inviteCode;

    private Integer inviteCount;

    private Integer inviteVaildCount;

    /**
     * 直接有效数量
     */
    private Integer inviteDirectVaildCount;

    /**
     * 间接有效数量
     */
    private Integer inviteIndirectVaildCount;

    private Integer inviteLeaderCount;


    private String totalKpi;
    private String monthlyKpi;
    private Integer monthlyKpiStatus;
    private String todayKpi;

    private String totalCommissionUsdt;

    private String totalRepurchase;
    private String totalInviteLeaderIncome;
    private String nickname;
    private int leaderType;

    private Boolean isHobbitLeader;

    private Integer applyStatus;

    private QuitApplyStatus quitApplyStatus;

    @Data
    public static class QuitApplyStatus {
        private Integer quitApplyStatus; //0-init 1-退出通过 2-退出拒绝 3-退出完成
        private Long leftTime;
        //private Long quitPassedTime; //退出时间  quitApplyStatus=1的时间
    }

}
