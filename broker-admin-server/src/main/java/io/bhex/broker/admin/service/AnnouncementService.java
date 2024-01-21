package io.bhex.broker.admin.service;

import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.broker.admin.controller.dto.AnnouncementDTO;
import io.bhex.broker.admin.controller.param.DeleteAnnouncementPO;
import io.bhex.broker.admin.controller.param.SaveAnnouncementPO;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 27/08/2018 4:50 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface AnnouncementService {

    PaginationVO<AnnouncementDTO> listAnnouncement(Integer current, Integer pageSize, Long brokerId, Integer platform);

    AnnouncementDTO getAnnouncementById(Long announcementId, Long brokerId);

    Boolean createAnnouncement(SaveAnnouncementPO param);

    Boolean updateAnnouncement(SaveAnnouncementPO param);

    Boolean deleteAnnouncement(DeleteAnnouncementPO param);

    Boolean pushAnnouncement(Long announcementId, Long adminUserId, Long brokerId, Boolean isPublish);
}
