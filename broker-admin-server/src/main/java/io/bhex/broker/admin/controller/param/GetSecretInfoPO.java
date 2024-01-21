package io.bhex.broker.admin.controller.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class GetSecretInfoPO {

    @NotNull
    private Long userId;

    //页面来源
    @NotNull
    @Length(min = 1, max = 10)
    private String pageSource;

}
