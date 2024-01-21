package io.bhex.broker.admin.service;

import java.util.List;

import io.bhex.broker.admin.controller.dto.OptionInfoDto;
import io.bhex.broker.admin.controller.param.OptionCreatePO;
import io.bhex.broker.grpc.admin.CreateOptionReply;
import io.bhex.broker.grpc.admin.QueryOptionListRequest;

/**
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2019-01-31 17:28
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface OptionService {

    CreateOptionReply createOption(Long orgId, OptionCreatePO optionCreatePO);

    List<OptionInfoDto> queryOptionList(QueryOptionListRequest request);
}
