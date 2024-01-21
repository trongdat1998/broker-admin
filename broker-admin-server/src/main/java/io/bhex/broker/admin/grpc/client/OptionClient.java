package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.CreateOptionReply;
import io.bhex.broker.grpc.admin.CreateOptionRequest;
import io.bhex.broker.grpc.admin.QueryOptionListRequest;
import io.bhex.broker.grpc.admin.QueryOptionListResponse;

/**
 * @ProjectName:
 * @Package:
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2020/9/8 下午6:39
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface OptionClient {

    CreateOptionReply createOption (CreateOptionRequest request);

    QueryOptionListResponse queryOptionList (QueryOptionListRequest request);
}
