package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * flow node
 *
 * @author songxd
 * @date 2021-01-15
 */
@Data
public class FlowNodeDTO {
    /**
     * 审批人
     */
    private Long approver;

    /**
     * 是否发送通知:1=是否 1=否
     */
    private Integer allowNotify;

    /**
     * 通知方式:1=邮件 2=短信 3=邮件+短信
     */
    private Integer notifyMode;
}
