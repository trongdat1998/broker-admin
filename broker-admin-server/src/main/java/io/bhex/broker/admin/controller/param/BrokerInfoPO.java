package io.bhex.broker.admin.controller.param;

import io.bhex.broker.admin.controller.dto.BrokerLanguageDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BrokerInfoPO {
    private Map<String, Boolean> functions;

    private List<BrokerLanguageDTO> supportLanguages;
}
