package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2019/12/6 下午2:29
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class UserRcRecordsPO {

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private String rcType;

    private Integer pageSize = 100;

    private Integer lastId = 0;

    private Long userId = 0L;

    private String nationalCode;

    private String phone;

    private String email;

}
