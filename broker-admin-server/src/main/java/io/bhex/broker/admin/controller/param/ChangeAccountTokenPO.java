package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAccountTokenPO {

    private Long targetAccountId;

    private String targetTokenId;

    private String targetAmount;

    private Integer targetType;

    private Long sourceAccountId;

    private String sourceTokenId;

    private String sourceAmount;

    private String clientOrderId;
}
