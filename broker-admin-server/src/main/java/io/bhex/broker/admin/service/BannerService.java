package io.bhex.broker.admin.service;


import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.broker.admin.controller.dto.BannerDTO;
import io.bhex.broker.admin.controller.param.DeleteBannerPO;
import io.bhex.broker.admin.controller.param.SaveBannerPO;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 28/08/2018 8:29 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BannerService {

    PaginationVO<BannerDTO> listBanner(Integer current, Integer pageSize, Long brokerId, String locale,
                                       Integer platform, Integer bannerPosition);

    BannerDTO getBannerById(Long bannerId, Long brokerId, String locale);

    Boolean createBanner(SaveBannerPO param);

    Boolean updateBanner(SaveBannerPO param);

    Boolean deleteBanner(DeleteBannerPO param);
}
