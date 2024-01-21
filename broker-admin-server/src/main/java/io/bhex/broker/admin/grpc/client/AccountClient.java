package io.bhex.broker.admin.grpc.client;

import io.bhex.base.clear.AssetRequest;
import io.bhex.base.clear.AssetResponse;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 09/11/2018 3:15 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface AccountClient {

    AssetResponse getAsset(AssetRequest request);
}
