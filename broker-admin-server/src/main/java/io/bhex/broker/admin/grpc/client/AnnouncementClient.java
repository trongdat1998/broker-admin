package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.*;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 27/08/2018 5:02 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface AnnouncementClient {

    ListAnnouncementReply listAnnouncement(ListAnnouncementRequest request);

    AnnouncementDetail getAnnouncementById(GetAnnouncementRequest request);

    Boolean createAnnouncement(CreateAnnouncementRequest request);

    Boolean updateAnnouncement(UpdateAnnouncementRequest request);

    Boolean deleteAnnouncement(DeleteAnnouncementRequest request);

    Boolean pushAnnouncement(PushAnnouncementRequest request);
}
