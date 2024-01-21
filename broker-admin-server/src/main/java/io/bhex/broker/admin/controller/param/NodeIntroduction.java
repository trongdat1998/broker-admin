package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeIntroduction {

    private String language;

    private String introduction;

    private Boolean enabled;
}
