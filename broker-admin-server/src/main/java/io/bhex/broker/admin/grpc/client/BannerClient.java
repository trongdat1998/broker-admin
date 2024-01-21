package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.*;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 28/08/2018 8:29 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BannerClient {

    ListBannerReply listBanner(ListBannerRequest request);

    BannerDetail getBannerById(GetBannerByIdRequest request);

    Boolean createBanner(CreateBannerRequest request);

    Boolean updateBanner(UpdateBannerRequest request);

    Boolean deleteBanner(DeleteBannerRequest request);
}
