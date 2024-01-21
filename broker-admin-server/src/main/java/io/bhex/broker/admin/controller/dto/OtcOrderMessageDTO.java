package io.bhex.broker.admin.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单消息（聊天记录）
 *
 * @author lizhen
 * @date 2018-09-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtcOrderMessageDTO {
    /**
     * 账户id，0表示系统发送
     */
    private Long accountId;
    /**
     * 消息内容
     */
    private String message;
    /**
     * 消息内容
     */
    private Integer msgType;
    /**
     * 消息编码
     */
    private Integer msgCode;
    /**
     * 创建时间
     */
    private Long createDate;
}