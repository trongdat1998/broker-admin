package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleTransferPO {

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
}
