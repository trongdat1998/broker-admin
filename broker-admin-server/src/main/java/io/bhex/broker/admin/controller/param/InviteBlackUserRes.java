package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @Description:
 * @Date: 2019/1/14 上午10:44
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class InviteBlackUserRes {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

//    @JsonSerialize(using = ToStringSerializer.class)
//    private Long orgId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userContact;

    private Long createdAt;

}
