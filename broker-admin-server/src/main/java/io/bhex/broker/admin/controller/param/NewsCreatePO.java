package io.bhex.broker.admin.controller.param;

import io.bhex.broker.admin.controller.dto.NewsDetailsDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NewsCreatePO {
    @NotNull
    private String newsPath;
    //发布时间
    private Long published;
    @NotNull
    private List<NewsDetailsDTO> details;
}
