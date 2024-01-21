/**********************************
 *@项目名称: broker-parent
 *@文件名称: io.bhex.broker.domain
 *@Date 2018/6/26
 *@Author peiwei.ren@bhex.io 
 *@Copyright（C）: 2018 BlueHelix Inc.   All rights reserved.
 *注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 ***************************************/
package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class NewsDTO {
    private Long id;
    private Long orgId;
    private Long newsId;
    private String newsPath;
    private Integer status;
    private Long created;
    private Long updated;
    private Integer version;
    private Long published;

    private List<NewsDetailsDTO> details;
}
