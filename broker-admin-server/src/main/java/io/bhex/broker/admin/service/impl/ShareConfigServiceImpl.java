package io.bhex.broker.admin.service.impl;

import io.bhex.broker.admin.controller.dto.ShareConfigDTO;
import io.bhex.broker.admin.controller.dto.ShareConfigLocaleDTO;
import io.bhex.broker.admin.controller.param.ShareConfigLocalePO;
import io.bhex.broker.admin.controller.param.ShareConfigPO;
import io.bhex.broker.admin.grpc.client.ShareConfigClient;
import io.bhex.broker.admin.service.ShareConfigService;
import io.bhex.broker.grpc.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 2:20 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class ShareConfigServiceImpl implements ShareConfigService {

    @Autowired
    private ShareConfigClient shareConfigClient;

    @Override
    public ShareConfigDTO getShareConfigInfo(Long brokerId) {
        ShareConfigDTO shareConfigDTO = new ShareConfigDTO();
        shareConfigDTO.setLocaleInfo(new ArrayList<>());

        ShareConfigInfoByAdminRequest request = ShareConfigInfoByAdminRequest.newBuilder()
                .setBrokerId(brokerId)
                .build();

        ShareConfigInfoByAdminReply shareConfigInfo = shareConfigClient.getShareConfigInfo(request);
        if (Objects.nonNull(shareConfigInfo)) {
            BeanUtils.copyProperties(shareConfigInfo, shareConfigDTO);

            if (!CollectionUtils.isEmpty(shareConfigInfo.getLocaleInfoList())) {
                List<ShareConfigLocaleInfo> localeInfoList = shareConfigInfo.getLocaleInfoList();
                localeInfoList.forEach(info -> {
                    ShareConfigLocaleDTO localeDTO = new ShareConfigLocaleDTO();
                    BeanUtils.copyProperties(info, localeDTO);
                    shareConfigDTO.getLocaleInfo().add(localeDTO);
                });
            }
        }
        return shareConfigDTO;
    }

    @Override
    public Boolean saveShareConfigInfo(ShareConfigPO param) {
        SaveShareConfigInfoRequest.Builder builder = SaveShareConfigInfoRequest.newBuilder();

        BeanUtils.copyProperties(param, builder);

        List<ShareConfigLocalePO> localeInfoList = param.getLocaleInfo();
        localeInfoList.forEach(info -> {
            ShareConfigLocaleInfo.Builder localeBUilder = ShareConfigLocaleInfo.newBuilder();
            BeanUtils.copyProperties(info, localeBUilder);
            builder.addLocaleInfo(localeBUilder);
        });

        SaveShareConfigInfoReply reply = shareConfigClient.saveShareConfigInfo(builder.build());
        if (0 == reply.getRet()) {
            return Boolean.TRUE;
        } else {
            log.error("Save Share Config Error: brokerId => {}, error code => {}.", param.getBrokerId(), reply.getRet());
            return Boolean.FALSE;
        }
    }
}
