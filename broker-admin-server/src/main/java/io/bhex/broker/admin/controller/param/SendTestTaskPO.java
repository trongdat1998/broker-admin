package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.CommonInputValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 14:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendTestTaskPO {
    @NotEmpty
    private String userIds;

    private String pushSummary = "";

    @NotEmpty
    @CommonInputValid(maxLength = 40)
    private String pushTitle;


    @NotEmpty
    @CommonInputValid(maxLength = 128)
    private String pushContent;

    @NotEmpty
    private String  pushUrl;

    @Max(2)
    private Integer urlType;
}
