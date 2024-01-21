package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class QueryActivityOrderPO {

    private Long orgId;

    private Long fromId;

    private Long endId;

    private Integer limit;

    private String projectCode;

    private Long projectId;

    private Long userId;

    private String email;

    private String mobile;

    private String language;
}


