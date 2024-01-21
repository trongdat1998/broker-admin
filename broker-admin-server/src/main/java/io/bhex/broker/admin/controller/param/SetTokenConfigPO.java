package io.bhex.broker.admin.controller.param;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-05-29 10:29
 */
@Data
public class SetTokenConfigPO {
    @NotNull
    String tokenId;
    @NotNull
    String convertRate;
    @NotNull
    String leverage;

    Boolean canBorrow;

    String maxQuantity;

    String minQuantity;

    int quantityPrecision;

    String repayMinQuantity;

    int isOpen = 1;

    int showInterestPrecision = 4;
}
