package io.bhex.broker.admin.controller.dto;

import io.bhex.broker.admin.controller.param.NodeIntroduction;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class VoteNodeDTO {

    private Long nodeId;
    private Integer nodeType;
    private String nodeName;
    private String nodeIcon;
    private Long orgId;
    private Long userId;
    private String userName;
    private String mobile;
    private String lockAmount;
    private String voteAmount;
    private Integer status;
    private Integer lockStatus;
    private Boolean isAdmin;
    private Long createdAt;
    private Long updatedAt;
    private String slogan;
    private List<NodeIntroduction> introductions;
}
