package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.ExcludeLogAnnotation;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.grpc.client.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/helper")
@ExcludeLogAnnotation
public class HelperController extends BaseController {

    @Resource
    private NotificationService notificationService;

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/notification/query", method = RequestMethod.GET)
    public ResultModel queryNotification() {

        AdminUserReply user = getRequestUser();
        long brokerId = user.getOrgId();
        long userId = user.getId();

        try {
            Map<String, Integer> map = notificationService.listNotification(userId, brokerId);
            return ResultModel.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultModel.error(e.getMessage());
        }
    }

}
