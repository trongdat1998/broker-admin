package io.bhex.broker.admin.controller.param;

import lombok.Data;

/**
 * @Description:
 * @Date: 2018/12/14 下午5:28
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class RegStatisticsPO {

    private Long beginDate;

    private Long endDate;

    //1-查汇总数据 0-汇总 daily都查
    private Integer type = 0;
}
