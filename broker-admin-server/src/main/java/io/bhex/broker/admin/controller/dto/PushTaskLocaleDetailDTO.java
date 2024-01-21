package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.Builder;
import lombok.Data;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 15:05
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class PushTaskLocaleDetailDTO {
    private String pushSummary;

    private String pushTitle;

    @JsonSerialize(using = LocaleOutputSerialize.class)
    private String locale;

    private String pushContent;

    private String  pushUrl;

    private Integer urlType;
}
