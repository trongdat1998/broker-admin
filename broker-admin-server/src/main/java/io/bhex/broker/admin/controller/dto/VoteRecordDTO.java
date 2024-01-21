package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class VoteRecordDTO {

    private Long voteId;
    private Long nodeId;
    private Long orgId;
    private Long userId;
    private String voteAmount;
    private Integer status;
    private Long unlockTime;
    private Long createdAt;
    private Long updatedAt;
}
