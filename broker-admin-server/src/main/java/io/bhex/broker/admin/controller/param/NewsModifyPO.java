package io.bhex.broker.admin.controller.param;

import io.bhex.broker.admin.controller.dto.NewsDetailsDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NewsModifyPO {
    @NotNull
    private Long id;
    @NotNull
    private Long newsId;
    @NotNull
    private String newsPath;
    private Long published;
    @NotNull
    private List<NewsDetailsDTO> details;
}
