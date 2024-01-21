package io.bhex.broker.admin.service.impl;

import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.broker.admin.controller.dto.BannerDTO;
import io.bhex.broker.admin.controller.dto.BannerLocaleDetailDTO;
import io.bhex.broker.admin.controller.param.BannerLocaleDetail;
import io.bhex.broker.admin.controller.param.DeleteBannerPO;
import io.bhex.broker.admin.controller.param.SaveBannerPO;
import io.bhex.broker.admin.grpc.client.BannerClient;
import io.bhex.broker.admin.service.BannerService;
import io.bhex.broker.admin.service.ImageUtilService;
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
 * @CreateDate: 28/08/2018 8:29 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerClient bannerClient;

    @Autowired
    private ImageUtilService imageUtilService;

    @Override
    public PaginationVO<BannerDTO> listBanner(Integer current, Integer pageSize, Long brokerId, String locale, Integer platform, Integer bannerPosition) {
        ListBannerRequest request = ListBannerRequest.newBuilder()
                .setCurrent(current)
                .setPageSize(pageSize)
                .setBrokerId(brokerId)
                .setPlatform(platform)
                .setBannerPosition(bannerPosition)
                .build();

        ListBannerReply reply = bannerClient.listBanner(request);
        PaginationVO<BannerDTO> vo = new PaginationVO();
        BeanUtils.copyProperties(reply, vo);
        List<BannerDTO> dtos = new ArrayList<>();
        for (BannerDetail detail: reply.getBannerDetailsList()) {
            BannerDTO bannerDetailDTO = new BannerDTO();
            //dto
            BeanUtils.copyProperties(detail, bannerDetailDTO);
            setBannerStatus(bannerDetailDTO);

            bannerDetailDTO.setBannerId(detail.getId());
            //dto details
            List<BannerLocaleDetailDTO> result = detail.getBannerLocalDetailsList().stream().map(d -> {
                BannerLocaleDetailDTO dto = new BannerLocaleDetailDTO();
                BeanUtils.copyProperties(d, dto);
                dto.setImageUrl(imageUtilService.getImageUrl(dto.getImageUrl()));
                return dto;
            }).collect(Collectors.toList());

            bannerDetailDTO.setLocaleDetails(result);
            dtos.add(bannerDetailDTO);
        }
        vo.setList(dtos);
        return vo;
    }

    public void setBannerStatus(BannerDTO dto) {
        if (null != dto) {
            Long now = System.currentTimeMillis();
            if (now < dto.getBeginAt()) {
                dto.setStatus(BannerDTO.UNPUBLISH);
            } else if (now > dto.getBeginAt() && now < dto.getEndAt()) {
                dto.setStatus(BannerDTO.PUBLISH);
            } else if (now > dto.getEndAt()) {
                dto.setStatus(BannerDTO.DOWN);
            }
        }
    }

    @Override
    public BannerDTO getBannerById(Long bannerId, Long brokerId, String locale) {
        GetBannerByIdRequest request = GetBannerByIdRequest.newBuilder()
                .setBannerId(bannerId)
                .setBrokerId(brokerId)
                .build();

        BannerDetail reply = bannerClient.getBannerById(request);

        BannerDTO bannerDetailDTO = new BannerDTO();
        BeanUtils.copyProperties(reply, bannerDetailDTO);
        bannerDetailDTO.setBannerId(reply.getId());
        setBannerStatus(bannerDetailDTO);
        List<BannerLocaleDetailDTO> result = reply.getBannerLocalDetailsList().stream().map(d -> {
                    BannerLocaleDetailDTO dto = new BannerLocaleDetailDTO();
                    BeanUtils.copyProperties(d, dto);
                    dto.setImageUrl(imageUtilService.getImageUrl(dto.getImageUrl()));
                    return dto;
                }).collect(Collectors.toList());
        bannerDetailDTO.setLocaleDetails(result);
        return bannerDetailDTO;
    }

    @Override
    public Boolean createBanner(SaveBannerPO param) {
        CreateBannerRequest.Builder builder = CreateBannerRequest.newBuilder();
        BeanUtils.copyProperties(param, builder);
        List<SaveBannerLocalDetail> localDetailList = new ArrayList<>();
        for (BannerLocaleDetail detail: param.getLocaleDetails()) {
            SaveBannerLocalDetail.Builder detailBuilder = SaveBannerLocalDetail.newBuilder();
            BeanUtils.copyProperties(detail, detailBuilder);
            //detailBuilder.setImageUrl(imageUtilService.getImagePath(detailBuilder.getImageUrl()));
            localDetailList.add(detailBuilder.build());
        }
        builder.addAllLocaleDetails(localDetailList);

        Boolean reply = bannerClient.createBanner(builder.build());
        return reply;
    }

    @Override
    public Boolean updateBanner(SaveBannerPO param) {
        UpdateBannerRequest.Builder builder = UpdateBannerRequest.newBuilder();
        BeanUtils.copyProperties(param, builder);
        List<SaveBannerLocalDetail> localDetailList = new ArrayList<>();
        for (BannerLocaleDetail detail: param.getLocaleDetails()) {
            SaveBannerLocalDetail.Builder detailBuilder = SaveBannerLocalDetail.newBuilder();
            BeanUtils.copyProperties(detail, detailBuilder);
            //detailBuilder.setImageUrl(imageUtilService.getImagePath(detailBuilder.getImageUrl()));
            localDetailList.add(detailBuilder.build());
        }
        builder.addAllLocaleDetails(localDetailList);
        Boolean reply = bannerClient.updateBanner(builder.build());
        return reply;
    }

    @Override
    public Boolean deleteBanner(DeleteBannerPO param) {
        DeleteBannerRequest request = DeleteBannerRequest.newBuilder()
                .setAdminUserId(param.getAdminUserId())
                .setBannerId(param.getBannerId())
                .setBrokerId(param.getBrokerId())
                .build();

        Boolean isOk = bannerClient.deleteBanner(request);
        return isOk;
    }
}
