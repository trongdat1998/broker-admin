package io.bhex.broker.admin.controller.dto;


import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Date: 2019/8/23 下午5:16
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class UserBlackWhiteSettingDTO {

    private Long id;

    private Long userId = 0L;

    private Integer bwType = 0;

    private Integer listType = 0;

    private String remark;

    //private Long startTime;

    //private Long endTime;

    private Integer status = 1;

    //private String extraInfo;

    private List<Long> userIds; //如果有多个则传userids


}
