package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.AnnouncementDTO;
import io.bhex.broker.admin.controller.param.AnnouncementIdPO;
import io.bhex.broker.admin.controller.param.DeleteAnnouncementPO;
import io.bhex.broker.admin.controller.param.SaveAnnouncementPO;
import io.bhex.broker.admin.controller.param.PageRequestPO;
import io.bhex.broker.admin.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 27/08/2018 5:23 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/announcement")
public class AnnouncementController extends BaseController {

    @Autowired
    private AnnouncementService announcementService;

    @RequestMapping(value = "query", method = RequestMethod.POST)
    public ResultModel listAnnouncement(@RequestBody PageRequestPO page,
                                        Locale locale) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        PaginationVO<AnnouncementDTO> vo = announcementService.listAnnouncement(page.getCurrent(), page.getPageSize(), brokerId, page.getPlatform());
        return ResultModel.ok(vo);
    }

    @RequestMapping(value = "/show", method = RequestMethod.POST)
    public ResultModel getAnnouncement(@RequestBody AnnouncementIdPO param) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        AnnouncementDTO announcement = announcementService.getAnnouncementById(param.getAnnouncementId(), brokerId);
        return ResultModel.ok(announcement);
    }

    @BussinessLogAnnotation(opContent = "Add Announcement")
    @RequestMapping(method = RequestMethod.POST)
    public ResultModel addAnnouncement(@RequestBody  @Valid SaveAnnouncementPO param) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        param.setAdminUserId(requestUser.getId());
        param.setBrokerId(brokerId);
        Boolean isOk = announcementService.createAnnouncement(param);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "Modify Announcement ID:{#po.id}")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultModel updateAnnouncement(@RequestBody  @Valid SaveAnnouncementPO po, AdminUserReply requestUser) {
        long brokerId = requestUser.getOrgId();
        po.setAdminUserId(requestUser.getId());
        po.setBrokerId(brokerId);
        Boolean isOk = announcementService.updateAnnouncement(po);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "Enable Announcement ID:{#po.announcementId}")
    @RequestMapping(value = "/allow_publish", method = RequestMethod.POST)
    public ResultModel publishAnnouncement(@RequestBody @Valid AnnouncementIdPO po, Locale locale) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        Boolean isOk = announcementService.pushAnnouncement(po.getAnnouncementId(), requestUser.getId(), brokerId, Boolean.TRUE);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "Disable Announcement ID:{#po.announcementId}")
    @RequestMapping(value = "/forbid_publish", method = RequestMethod.POST)
    public ResultModel forbidPublish(@RequestBody @Valid AnnouncementIdPO po, Locale locale) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        Boolean isOk = announcementService.pushAnnouncement(po.getAnnouncementId(), requestUser.getId(), brokerId, Boolean.FALSE);
        return ResultModel.ok(isOk);
    }

    @BussinessLogAnnotation(opContent = "Delete Announcement ID:{#po.announcementId}")
    @RequestMapping(value = "/delete")
    public ResultModel delete(@RequestBody @Valid DeleteAnnouncementPO po) {
        AdminUserReply requestUser = getRequestUser();
        po.setAdminUserId(requestUser.getId());
        po.setBrokerId(requestUser.getOrgId());
        Boolean isOk = announcementService.deleteAnnouncement(po);
        if (isOk) {
            return ResultModel.ok(isOk);
        } else {
            return ResultModel.error("internal.error");
        }
    }
}
