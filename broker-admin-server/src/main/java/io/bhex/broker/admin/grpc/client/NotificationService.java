package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.NotifyType;

import java.util.Map;

public interface NotificationService {

    Map<String,Integer> listNotification(long userId, long brokerId);

    void decreNotification(long brokerId, NotifyType notifyType);

    void clearNotification(Long brokerId, NotifyType notifyType);
}
