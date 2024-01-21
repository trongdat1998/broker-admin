package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTCPaymentDTO {
    
    private long accountId;
    /**
     * 支付方式
     *     0=银行
     *     1=支付宝
     *     2=微信
     */
    private int paymentType;
    //真实姓名
    private String realName;
    //银行编码
    private String bankCode;
    //银行名称
    private String bankName;
    private String branchName;
    //账号
    private String accountNo;
    //二维码
    private String qrcode;
    //是否可见
    private boolean visible;
    //用户id
    private long   id;
}
