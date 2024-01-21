package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @Description:
 * @Date: 2020/3/3 下午6:55
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class QueryExperienceFundPO {

    private String title = "";

    private Integer pageSize = 100;

    private Integer type = 1;

    private Long lastId = 0L;

    private Integer status = 0;

    private Long id = 0L;

}
