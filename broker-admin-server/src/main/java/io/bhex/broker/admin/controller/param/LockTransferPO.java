package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockTransferPO {

    /**
     * 转账用户ID
     */
    @NonNull
    private Long userId;

    /**
     * 数量
     */
    @NonNull
    private String amount;

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

    /**
     * 0到可用 1到锁仓
     */
    @NonNull
    private Integer lock;
}
