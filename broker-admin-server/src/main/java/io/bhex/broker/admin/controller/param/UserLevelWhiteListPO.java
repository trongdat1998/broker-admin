package io.bhex.broker.admin.controller.param;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserLevelWhiteListPO {

    @NotNull
    private Long levelConfigId;

    private List<Long> userIds = Lists.newArrayList();

}
