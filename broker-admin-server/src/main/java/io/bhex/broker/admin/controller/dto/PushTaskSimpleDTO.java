package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 15:23
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class PushTaskSimpleDTO {
    Long taskId;
    Long taskRound;
    String name;
    Integer cycleType;
    Integer cycleDayOfWeek;
    Long firstActionTime;
    Long actionTime;
    Integer status;
    Long expireTime;
    Long executeTime;
    Long sendCount;
    Long deliveryCount;
    Long clickCount;
    String remark;
    Long created;
    Long updated;
}
