package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.*;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 2019/12/12 8:36 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
public interface CustomLabelClient {

    QueryCustomLabelReply queryCustomLabel(QueryCustomLabelRequest request);

    SaveCustomLabelReply saveCustomLabel(SaveCustomLabelRequest request);

    DelCustomLabelReply delCustomLabel(DelCustomLabelRequest request);
}
