package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.AnnouncementClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.AdminAnnouncementServiceGrpc;
import io.bhex.broker.grpc.admin.AnnouncementDetail;
import io.bhex.broker.grpc.admin.CreateAnnouncementRequest;
import io.bhex.broker.grpc.admin.DeleteAnnouncementRequest;
import io.bhex.broker.grpc.admin.GetAnnouncementRequest;
import io.bhex.broker.grpc.admin.ListAnnouncementReply;
import io.bhex.broker.grpc.admin.ListAnnouncementRequest;
import io.bhex.broker.grpc.admin.PushAnnouncementRequest;
import io.bhex.broker.grpc.admin.UpdateAnnouncementRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 27/08/2018 5:04 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Component
public class AnnouncementClientImpl implements AnnouncementClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminAnnouncementServiceGrpc.AdminAnnouncementServiceBlockingStub getAnnouncementStub() {
        return grpcConfig.adminAnnouncementServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public ListAnnouncementReply listAnnouncement(ListAnnouncementRequest request) {
        return getAnnouncementStub().listAnnouncement(request);
    }

    @Override
    public AnnouncementDetail getAnnouncementById(GetAnnouncementRequest request) {
        return getAnnouncementStub().getAnnouncement(request);
    }

    @Override
    public Boolean updateAnnouncement(UpdateAnnouncementRequest request) {
        return getAnnouncementStub().updateAnnouncement(request).getResult();
    }

    @Override
    public Boolean deleteAnnouncement(DeleteAnnouncementRequest request) {
        return getAnnouncementStub().deleteAnnouncement(request).getResult();
    }

    @Override
    public Boolean createAnnouncement(CreateAnnouncementRequest request) {
        return getAnnouncementStub().createAnnouncement(request).getResult();
    }

    @Override
    public Boolean pushAnnouncement(PushAnnouncementRequest request) {
        return getAnnouncementStub().pushAnnouncement(request).getResult();
    }
}
