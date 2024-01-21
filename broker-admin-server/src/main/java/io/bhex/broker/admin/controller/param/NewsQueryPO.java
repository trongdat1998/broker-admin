package io.bhex.broker.admin.controller.param;

import com.google.common.base.Strings;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import io.bhex.bhop.common.util.validation.UrlValid;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class NewsQueryPO {
    @NotNull
    private Long lastId;
    @NotNull
    private Integer pageSize;

    private Integer status;
}
