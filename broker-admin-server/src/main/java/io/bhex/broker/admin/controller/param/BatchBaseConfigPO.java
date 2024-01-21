package io.bhex.broker.admin.controller.param;

import lombok.Data;

import java.util.List;

@Data
public class BatchBaseConfigPO {

    private List<BaseConfigPO> configs;

    private String opPlatform;

    private Boolean withLanguage;

    private String opType;
}
