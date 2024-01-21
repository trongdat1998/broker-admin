package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewsSetTemplatePO {
    @NotBlank
    private String name;

    @NotBlank
    private String content;

    @NotBlank
    private String params;
}
