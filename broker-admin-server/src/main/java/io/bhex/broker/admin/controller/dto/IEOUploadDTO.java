package io.bhex.broker.admin.controller.dto;

import lombok.Data;


@Data
public class IEOUploadDTO {

    public static final String ID = "ID";
    public static final String USER_ID = "UID";
    public static final String ORDER_ID = "订单号";
    public static final String AMOUNT = "申购金额";
    public static final String USE_AMOUNT = "消耗金额";
    public static final String LUCKY_AMOUNT = "获取数量";
    public static final String BACK_AMOUNT = "返还金额";

    private Integer tmplId;
    private Long id;
    private Long userId;
    private Long orderId;
    private String amount;
    private String useAmount;
    private String luckyAmount;
    private String backAmount;
}
