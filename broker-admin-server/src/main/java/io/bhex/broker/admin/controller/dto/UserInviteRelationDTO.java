package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class UserInviteRelationDTO {

    private Long inviteId;
    private Long userId;
    private String nationalCode;
    private String mobile;
    private String email;
    private Integer registerType;
    private Integer verifyStatus;
    private String source;
    private Integer inviteType;
    private Long registerDate;


    private String name;
    private Boolean inviteLeader;
    private Integer inviteIndirectCount; //邀请人通过此直接邀请人获得的间接邀请人数

}
