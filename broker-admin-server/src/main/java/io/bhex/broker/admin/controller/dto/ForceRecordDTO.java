package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-12-24 20:24
 */
@Data
public class ForceRecordDTO {
    Long id;
    Long forceId;
    Long orgId;
    Long adminUserId;
    Long accountId;
    Long userId;
    String safety;
    String allPosition;
    String allLoan;
    //强平类型 1:自动强平 2:手动强平
    Integer forceType;
    //处理结果 1:未完成 2:完成
    Integer dealStatus;
    String forceDesc;
    Long created;
    Long updated;
}
