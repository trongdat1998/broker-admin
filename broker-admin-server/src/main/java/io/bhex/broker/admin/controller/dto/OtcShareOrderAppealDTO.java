package io.bhex.broker.admin.controller.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtcShareOrderAppealDTO {

    public final static int CANCEL=1;
    public final static int FINISH=2;
    /**
     * 券商id
     */
    private Long brokerId;

    /**
     * 操作状态 40=撤销,50=成交
     */
    private int type;

    /**
     * 操作备注
     */
    private String remark;
}
