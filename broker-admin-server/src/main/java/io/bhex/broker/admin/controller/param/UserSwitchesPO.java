package io.bhex.broker.admin.controller.param;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserSwitchesPO {

    private List<String> groups;

    @NotNull
    private Long userId;
}
