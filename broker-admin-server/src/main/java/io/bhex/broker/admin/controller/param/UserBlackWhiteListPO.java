
package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Date: 2019/12/20 上午11:36
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class UserBlackWhiteListPO {

    private Long userId = 0L;

    private Long lastId = 0L;

    @NotNull
    private Integer pageSize = 1000;

    private Integer bwType = 0;

    private Integer listType = 0;
}