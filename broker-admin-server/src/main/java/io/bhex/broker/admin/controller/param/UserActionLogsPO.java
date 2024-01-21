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
public class UserActionLogsPO {

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long userId;

    private Integer pageSize = 100;

    private Integer lastId = 0;
}
