package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappingPo {

    @NonNull
    private String sourceToken;

    @NonNull
    private String sourceAmount;

    @NonNull
    private Long sourceClientOrderId;

    @NonNull
    private Long targetUserId;

    @NonNull
    private String targetToken;

    @NonNull
    private String targetAmount;

    @NonNull
    private Long targetClientOrderId;

    @NonNull
    private Integer isLock;

    @NonNull
    private Integer businessType;
}
