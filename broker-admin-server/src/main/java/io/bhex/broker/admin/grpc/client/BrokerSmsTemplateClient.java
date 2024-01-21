package io.bhex.broker.admin.grpc.client;

import io.bhex.base.exadmin.SignReply;

import java.util.List;

/**
 * @Description:
 * @Date: 2018/11/1 下午5:22
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface BrokerSmsTemplateClient {

    List<SignReply> getSmsSigns(List<Long> brokerIds,Long lastModify);

}
