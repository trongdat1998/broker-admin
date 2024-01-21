package io.bhex.broker.admin.controller.param;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TokenWithdrawFee implements Serializable {

    @NotNull
    private String tokenId;
    @NotNull
    @Range(min = 0)
    private BigDecimal fee;

}
