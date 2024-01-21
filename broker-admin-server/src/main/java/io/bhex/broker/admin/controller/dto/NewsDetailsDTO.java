/**********************************
 *@项目名称: broker-parent
 *@文件名称: io.bhex.broker.domain
 *@Date 2018/6/26
 *@Author peiwei.ren@bhex.io 
 *@Copyright（C）: 2018 BlueHelix Inc.   All rights reserved.
 *注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 ***************************************/
package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.locale.LocaleInputDeserialize;
import io.bhex.bhop.common.util.locale.LocaleOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NewsDetailsDTO {
    private Long id;
    private Long orgId;
    private Long newsId;
    @NotNull
    @JsonDeserialize(using = LocaleInputDeserialize.class)
    @JsonSerialize(using = LocaleOutputSerialize.class)
    private String language;
    @NotNull
    private String title;
    @NotNull
    private String tags;
    @NotNull
    private String source;
    @NotNull
    private String content;
    @NotNull
    private String images;
    @NotNull
    private String summary;
}
