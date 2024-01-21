package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.CustomLabelDTO;
import io.bhex.broker.admin.controller.dto.SaveCustomLabelDTO;
import io.bhex.broker.admin.controller.param.DelCustomLabelPO;
import io.bhex.broker.admin.controller.param.QueryCustomLabelPO;
import io.bhex.broker.admin.controller.param.SaveCustomLabelPO;
import io.bhex.broker.admin.controller.param.SaveSymbolCustomLabelPO;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 2019/12/12 8:43 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
public interface CustomLabelService {

    List<CustomLabelDTO> queryCustomLabel(QueryCustomLabelPO po);

    Boolean delCustomLabel(DelCustomLabelPO po);

    SaveCustomLabelDTO saveCustomLabel(SaveCustomLabelPO po);

    SaveCustomLabelDTO saveSymbolCustomLabel(SaveSymbolCustomLabelPO po);
}
