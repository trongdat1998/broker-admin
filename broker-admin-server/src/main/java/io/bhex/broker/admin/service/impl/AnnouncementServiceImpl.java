package io.bhex.broker.admin.service.impl;

import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.broker.admin.controller.dto.AnnouncementDTO;
import io.bhex.broker.admin.controller.dto.AnnouncementLocaleDetailDTO;
import io.bhex.broker.admin.controller.param.DeleteAnnouncementPO;
import io.bhex.broker.admin.controller.param.SaveAnnouncementPO;
import io.bhex.broker.admin.grpc.client.AnnouncementClient;
import io.bhex.broker.admin.service.AnnouncementService;
import io.bhex.broker.grpc.admin.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 27/08/2018 5:01 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementClient announcementClient;

    @Override
    public PaginationVO<AnnouncementDTO> listAnnouncement(Integer current, Integer pageSize, Long brokerId, Integer platform) {
        ListAnnouncementRequest request = ListAnnouncementRequest.newBuilder()
                .setCurrent(current)
                .setPageSize(pageSize)
                .setBrokerId(brokerId)
                .setPlatform(platform)
                .build();
        ListAnnouncementReply reply = announcementClient.listAnnouncement(request);

        PaginationVO<AnnouncementDTO> vo = new PaginationVO();
        BeanUtils.copyProperties(reply, vo);

        List<AnnouncementDTO> dtos = new ArrayList<>();
        List<AnnouncementDetail> details = reply.getAnnouncementDetailsList();
        for(AnnouncementDetail detail : details){
            //dto
            AnnouncementDTO announcementDTO = new AnnouncementDTO();
            BeanUtils.copyProperties(detail, announcementDTO);
            setBannerStatus(announcementDTO);
            //dto details
            List<AnnouncementLocaleDetailDTO> result = detail.getLocaleDetailsList().stream().map(d -> {
                AnnouncementLocaleDetailDTO dto = new AnnouncementLocaleDetailDTO();
                BeanUtils.copyProperties(d, dto);
                return dto;
            }).collect(Collectors.toList());
            announcementDTO.setLocaleDetails(result);
            dtos.add(announcementDTO);
        }
        vo.setList(dtos);
        return vo;
    }

    public void setBannerStatus(AnnouncementDTO dto) {
        if (null != dto) {
            Long now = System.currentTimeMillis();
            if (now < dto.getBeginAt()) {
                dto.setStatus(AnnouncementDTO.UNPUBLISH);
            } else if (now > dto.getBeginAt() && now < dto.getEndAt()) {
                dto.setStatus(AnnouncementDTO.PUBLISH);
            } else if (now > dto.getEndAt()) {
                dto.setStatus(AnnouncementDTO.DOWN);
            }
        }
    }

    @Override
    public AnnouncementDTO getAnnouncementById(Long announcementId, Long brokerId) {
        GetAnnouncementRequest request = GetAnnouncementRequest.newBuilder()
                .setAnnouncementId(announcementId)
                .setBrokerId(brokerId)
                .build();


        AnnouncementDetail reply = announcementClient.getAnnouncementById(request);
        //dto
        AnnouncementDTO announcementDTO = new AnnouncementDTO();
        BeanUtils.copyProperties(reply, announcementDTO);
        setBannerStatus(announcementDTO);
        //dto details
        List<AnnouncementLocaleDetailDTO> result = reply.getLocaleDetailsList().stream().map(d -> {
            AnnouncementLocaleDetailDTO dto = new AnnouncementLocaleDetailDTO();
            BeanUtils.copyProperties(d, dto);
            return dto;
        }).collect(Collectors.toList());
        announcementDTO.setLocaleDetails(result);

        return announcementDTO;
    }

    @Override
    public Boolean createAnnouncement(SaveAnnouncementPO param) {
        CreateAnnouncementRequest.Builder builder = CreateAnnouncementRequest.newBuilder();
        BeanUtils.copyProperties(param, builder);
        List<AnnouncementLocaleDetail> localDetailList = param.getLocaleDetails().stream().map(d -> {
            AnnouncementLocaleDetail.Builder detailBulider = AnnouncementLocaleDetail.newBuilder();
            BeanUtils.copyProperties(d, detailBulider);
            return detailBulider.build();
        }).collect(Collectors.toList());
        builder.addAllLocaleDetails(localDetailList);
        return announcementClient.createAnnouncement(builder.build());
    }

    @Override
    public Boolean updateAnnouncement(SaveAnnouncementPO param) {
        UpdateAnnouncementRequest.Builder builder = UpdateAnnouncementRequest.newBuilder();
        BeanUtils.copyProperties(param, builder);
        builder.setId(param.getId());
        List<AnnouncementLocaleDetail> localDetailList = param.getLocaleDetails().stream().map(d -> {
            AnnouncementLocaleDetail.Builder detailBulider = AnnouncementLocaleDetail.newBuilder();
            BeanUtils.copyProperties(d, detailBulider);
            return detailBulider.build();
        }).collect(Collectors.toList());
        builder.addAllLocaleDetails(localDetailList);
        return announcementClient.updateAnnouncement(builder.build());
    }

    @Override
    public Boolean deleteAnnouncement(DeleteAnnouncementPO param) {
        DeleteAnnouncementRequest request = DeleteAnnouncementRequest.newBuilder()
                .setAdminUserId(param.getAdminUserId())
                .setAnnouncementId(param.getAnnouncementId())
                .setBrokerId(param.getBrokerId())
                .build();

        return announcementClient.deleteAnnouncement(request);
    }

    @Override
    public Boolean pushAnnouncement(Long announcementId, Long adminUserId, Long brokerId, Boolean isPublish) {
        PushAnnouncementRequest request = PushAnnouncementRequest.newBuilder()
                .setAdminUserId(adminUserId)
                .setAnnouncementId(announcementId)
                .setBrokerId(brokerId)
                .setStatus(isPublish? 1: 0)
                .build();
        return announcementClient.pushAnnouncement(request);
    }
}
