package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ProjectName: exchange
 * @Package: io.bhex.ex.admin.dto.param
 * @Author: ming.xu
 * @CreateDate: 2019/9/12 3:29 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
public class ListApplyRecordPO {

    private Long brokerId;

    @NotNull
    private Integer current;
    @NotNull
    private Integer pageSize = 30;

    private String symbolId;

    private Integer category;

}
