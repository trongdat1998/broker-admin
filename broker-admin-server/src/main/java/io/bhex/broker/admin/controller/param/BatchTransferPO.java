package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchTransferPO {

    /**
     * 批量转账
     */
    private String transfers;

    /**
     * 转账发起ID
     */
    @NonNull
    private Long clientOrderId;

    /**
     * 转账类型
     */
    @NonNull
    private Integer businessType;

    /**
     * token
     */
    @NonNull
    private String token;
}
