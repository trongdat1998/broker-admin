package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.controller.dto.CustomLabelDTO;
import io.bhex.broker.admin.controller.dto.CustomLabelLocalDetailDTO;
import io.bhex.broker.admin.controller.dto.SaveCustomLabelDTO;
import io.bhex.broker.admin.controller.param.DelCustomLabelPO;
import io.bhex.broker.admin.controller.param.QueryCustomLabelPO;
import io.bhex.broker.admin.controller.param.SaveCustomLabelPO;
import io.bhex.broker.admin.controller.param.SaveSymbolCustomLabelPO;
import io.bhex.broker.admin.grpc.client.CustomLabelClient;
import io.bhex.broker.admin.service.CustomLabelService;
import io.bhex.broker.grpc.admin.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 2019/12/12 8:44 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class CustomLabelServiceImpl implements CustomLabelService {

    @Autowired
    private CustomLabelClient customLabelClient;

    @Override
    public List<CustomLabelDTO> queryCustomLabel(QueryCustomLabelPO po) {
        po.setFromId(Objects.isNull(po.getFromId()) ? 0L : po.getFromId());
        po.setEndId(Objects.isNull(po.getEndId()) ? 0L : po.getEndId());
        po.setLimit(Objects.isNull(po.getLimit()) ? 20 : po.getLimit());
        QueryCustomLabelRequest request = QueryCustomLabelRequest.newBuilder()
                .setOrgId(po.getOrgId())
                .setFromId(po.getFromId())
                .setEndId(po.getEndId())
                .setLimit(po.getLimit())
                .setLabelType(po.getType())
                .build();

        QueryCustomLabelReply reply = customLabelClient.queryCustomLabel(request);
        List<QueryCustomLabelReply.CustomLabelInfo> customLabelInfoList = reply.getCustomLabelInfoList();
        List<CustomLabelDTO> dtoList = customLabelInfoList.stream().map(c -> {
            List<QueryCustomLabelReply.LocaleDetail> localeDetailList = c.getLocaleDetailList();
            List<CustomLabelLocalDetailDTO> localeList = localeDetailList.stream().map(l ->
                    CustomLabelLocalDetailDTO.builder()
                            .language(l.getLanguage())
                            .labelValue(l.getLabelValue())
                            .build()
            ).collect(Collectors.toList());

            return CustomLabelDTO.builder()
                    .orgId(c.getOrgId())
                    .labelId(c.getLabelId())
                    .colorCode(c.getColorCode())
                    .userCount(c.getUserCount())
                    .userIdsStr(c.getUserIdsStr())
                    .localeDetail(localeList)
                    .updatedAt(c.getUpdatedAt())
                    .createdAt(c.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public Boolean delCustomLabel(DelCustomLabelPO po) {
        DelCustomLabelRequest request = DelCustomLabelRequest.newBuilder()
                .setOrgId(po.getOrgId())
                .setLabelId(po.getLabelId())
                .build();

        DelCustomLabelReply reply = customLabelClient.delCustomLabel(request);
        if (reply.getRet() == 0) {
            return Boolean.TRUE;
        }
        // todo: ret to error code
        return Boolean.FALSE;
    }

    @Override
    public SaveCustomLabelDTO saveCustomLabel(SaveCustomLabelPO po) {
        if (Objects.isNull(po.getLabelId()) || po.getLabelId() <= 0L) {
            po.setLabelId(0L);
        }

        List<SaveCustomLabelRequest.LocaleDetail> localeList = po.getLocaleDetail().stream().map(l ->
                SaveCustomLabelRequest.LocaleDetail.newBuilder()
                        .setLanguage(l.getLanguage())
                        .setLabelValue(l.getLabelValue())
                        .build()
        ).collect(Collectors.toList());

        SaveCustomLabelRequest request = SaveCustomLabelRequest.newBuilder()
                .setOrgId(po.getOrgId())
                .setLabelId(po.getLabelId())
                .setLabelType(po.getType())
                .setColorCode(Strings.nullToEmpty(po.getColorCode()))
                .setUserIdsStr(Strings.nullToEmpty(po.getUserIdsStr()))
                .addAllLocaleDetail(localeList)
                .build();

        SaveCustomLabelReply reply = customLabelClient.saveCustomLabel(request);

        SaveCustomLabelDTO dto = SaveCustomLabelDTO.builder()
                .ret(reply.getRet())
                .labelId(reply.getLabelId())
                .errorUserIds(reply.getErrorUserIdsList())
                .build();
        return dto;
    }

    @Override
    public SaveCustomLabelDTO saveSymbolCustomLabel(SaveSymbolCustomLabelPO po) {
        SaveCustomLabelPO saveCustomLabelPO = new SaveCustomLabelPO();
        saveCustomLabelPO.setOrgId(po.getOrgId());
        saveCustomLabelPO.setLabelId(po.getLabelId());
        saveCustomLabelPO.setColorCode(po.getColorCode());
        saveCustomLabelPO.setLocaleDetail(po.getLocaleDetail());
        saveCustomLabelPO.setType(2);
        return saveCustomLabel(saveCustomLabelPO);
    }
}
