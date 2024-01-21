package io.bhex.broker.admin.controller.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @Description:
 * @Date: 2019/8/15 下午2:17
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class InviteBonusRecordDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private long userId;

    private String token;

    private String bonusAmount;

    private long statisticsTime;

    private int status;
}
