package io.bhex.broker.admin.aspect;

import io.bhex.broker.admin.grpc.client.NotificationService;

import javax.annotation.Resource;

public abstract class AbstractAspect {

    @Resource
    protected NotificationService notificationService;
}
