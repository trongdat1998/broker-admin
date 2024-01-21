package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.order.SaveShareConfigInfoReply;
import io.bhex.broker.grpc.order.SaveShareConfigInfoRequest;
import io.bhex.broker.grpc.order.ShareConfigInfoByAdminReply;
import io.bhex.broker.grpc.order.ShareConfigInfoByAdminRequest;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 11:23 AM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
public interface ShareConfigClient {

    ShareConfigInfoByAdminReply getShareConfigInfo(ShareConfigInfoByAdminRequest request);

    SaveShareConfigInfoReply saveShareConfigInfo(SaveShareConfigInfoRequest request);
}
