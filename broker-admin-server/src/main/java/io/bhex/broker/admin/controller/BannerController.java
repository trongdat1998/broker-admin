package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BannerDTO;
import io.bhex.broker.admin.controller.param.DeleteBannerPO;
import io.bhex.broker.admin.controller.param.GetBannerPO;
import io.bhex.broker.admin.controller.param.PageRequestPO;
import io.bhex.broker.admin.controller.param.SaveBannerPO;
import io.bhex.broker.admin.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Locale;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 28/08/2018 8:28 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/banner")
public class BannerController extends BaseController {

    @Autowired
    private BannerService bannerService;

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResultModel listBanner(@RequestBody @Valid PageRequestPO page,
                                  Locale locale) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        PaginationVO<BannerDTO> vo = bannerService.listBanner(page.getCurrent(), page.getPageSize(), brokerId,
                LocaleUtil.getLanguage(locale), page.getPlatform(), page.getBannerPosition());
        return ResultModel.ok(vo);
    }

    @RequestMapping(value = "/show", method = RequestMethod.POST)
    public ResultModel getBanner(@RequestBody @Valid GetBannerPO po,
                                 Locale locale) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        BannerDTO bannerDetailDTO = bannerService.getBannerById(po.getBannerId(), brokerId, LocaleUtil.getLanguage(locale));
        return ResultModel.ok(bannerDetailDTO);
    }

    @BussinessLogAnnotation(opContent = "Add Banner")
    @RequestMapping(method = RequestMethod.POST)
    public ResultModel addBanner(@RequestBody @Valid SaveBannerPO param) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        param.setAdminUserId(requestUser.getId());
        param.setBrokerId(brokerId);
        Boolean isOk = bannerService.createBanner(param);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "Modify Banner ID:{#po.bannerId}")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultModel updateBanner(@RequestBody @Valid SaveBannerPO po) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        po.setAdminUserId(requestUser.getId());
        po.setBrokerId(brokerId);
        Boolean isOk = bannerService.updateBanner(po);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "Delete Banner ID:{#po.bannerId}")
    @RequestMapping(value = "/delete")
    public ResultModel deleteBanner(@RequestBody @Valid DeleteBannerPO po) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        po.setAdminUserId(requestUser.getId());
        po.setBrokerId(brokerId);
        Boolean isOk = bannerService.deleteBanner(po);
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }
}
