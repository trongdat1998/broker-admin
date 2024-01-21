package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.controller.param
 * @Author: ming.xu
 * @CreateDate: 20/08/2018 2:50 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class QueryUserVerifyPO {

    @NotNull
    private Integer current;
    @NotNull
    private Integer pageSize;

    private Long userId;

    private String email;

    private String phone;

    private Integer verifyStatus;

    private Integer nationality;

    private Integer cardType;

    private Long startTime = 0L;

    private Long endTime = 0L;

    private Long lastId = 0L;

    private int level = 0;

    private String cardNo = "";

}
