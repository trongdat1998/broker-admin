package io.bhex.broker.admin.service.impl;

import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import io.bhex.broker.admin.controller.dto.RedPacketDTO;
import io.bhex.broker.admin.controller.dto.RedPacketReceiveDetailDTO;
import io.bhex.broker.admin.grpc.client.impl.RedPacketClient;
import io.bhex.broker.admin.model.RedPacketTheme;
import io.bhex.broker.admin.model.RedPacketTokenConfig;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.red_packet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedPacketService {

    @Resource
    private RedPacketClient redPacketClient;

    public Map<Integer, List<RedPacketTheme>> queryRedPacketTheme(long orgId) {
        QueryRedPacketThemeRequest request = QueryRedPacketThemeRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        QueryRedPacketThemeResponse response = redPacketClient.queryRedPacketTheme(request);
        List<RedPacketTheme> themeList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(response.getThemeList())) {
            for (io.bhex.broker.grpc.red_packet.RedPacketTheme theme : response.getThemeList()) {
                RedPacketTheme redPacketTheme = RedPacketTheme.builder()
                        .id(theme.getId())
                        .orgId(theme.getOrgId())
                        .themeId(theme.getThemeId())
                        .themeContent(JsonUtil.defaultGson().fromJson(theme.getTheme(), new TypeToken<List<RedPacketTheme.Theme>>() {
                        }.getType()))
                        .status(theme.getStatus())
                        .customOrder(theme.getCustomIndex())
                        .type(theme.getPosition())
                        .build();
                themeList.add(redPacketTheme);
            }
        }
        Map<Integer, List<RedPacketTheme>> themeTypeMap = themeList.stream().collect(Collectors.groupingBy(RedPacketTheme::getType));
        return themeTypeMap;
    }

    public List<RedPacketTokenConfig> queryRedPacketTokenConfig(long orgId) {
        QueryRedPacketTokenConfigRequest request = QueryRedPacketTokenConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        QueryRedPacketTokenConfigResponse response = redPacketClient.queryRedPacketTokenConfig(request);
        List<RedPacketTokenConfig> tokenConfigList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(response.getTokenConfigList())) {
            for (io.bhex.broker.grpc.red_packet.RedPacketTokenConfig tokenConfig : response.getTokenConfigList()) {
                RedPacketTokenConfig redPacketTheme = RedPacketTokenConfig.builder()
                        .id(tokenConfig.getId())
                        .orgId(tokenConfig.getOrgId())
                        .tokenId(tokenConfig.getTokenId())
                        .tokenName(tokenConfig.getTokenName())
                        .minAmount(tokenConfig.getMinAmount())
                        .maxAmount(tokenConfig.getMaxAmount())
                        .maxCount(tokenConfig.getMaxCount())
                        .maxTotalAmount(tokenConfig.getMaxTotalAmount())
                        .status(tokenConfig.getStatus())
                        .customOrder(tokenConfig.getCustomIndex())
                        .build();
                tokenConfigList.add(redPacketTheme);
            }
        }
        tokenConfigList.sort(Comparator.comparing(RedPacketTokenConfig::getCustomOrder).reversed());
        return tokenConfigList;
    }

    public void saveRedPacketThemes(Long orgId, List<RedPacketTheme> themes) {
        log.info("save themes:{}", JsonUtil.defaultGson().toJson(themes));
        List<io.bhex.broker.grpc.red_packet.RedPacketTheme> redPacketThemes = Lists.newArrayList();
        for (RedPacketTheme theme : themes) {
            io.bhex.broker.grpc.red_packet.RedPacketTheme redPacketTheme = io.bhex.broker.grpc.red_packet.RedPacketTheme.newBuilder()
                    .setId(theme.getId() == null ? 0 : theme.getId())
                    .setOrgId(orgId)
                    .setThemeId(Strings.nullToEmpty(theme.getThemeId()))
                    .setTheme(JsonUtil.defaultGson().toJson(theme.getThemeContent()))
                    .setStatus(theme.getStatus() == null ? 0 : theme.getStatus())
                    .setCustomIndex(theme.getCustomOrder() == null ? 0 : theme.getCustomOrder())
                    .setPosition(theme.getType())
                    .build();
            redPacketThemes.add(redPacketTheme);
        }
        if (redPacketThemes.size() == 0) {
            return;
        }
        SaveOrUpdateRedPacketThemesRequest request = SaveOrUpdateRedPacketThemesRequest.newBuilder()
                .addAllRedPacketThemes(redPacketThemes)
                .build();
        redPacketClient.saveRedPacketThemes(request);
    }

    public void saveRedPacketTokenConfig(Long orgId, RedPacketTokenConfig config) {
        io.bhex.broker.grpc.red_packet.RedPacketTokenConfig tokenConfig = io.bhex.broker.grpc.red_packet.RedPacketTokenConfig.newBuilder()
                .setId(config.getId() == null ? 0 : config.getId())
                .setOrgId(orgId)
                .setTokenId(config.getTokenId())
                .setMinAmount(config.getMinAmount())
                .setMaxAmount(config.getMaxAmount())
                .setMaxCount(config.getMaxCount())
                .setMaxTotalAmount(config.getMaxTotalAmount())
                .setStatus(config.getStatus() == null ? 0 : config.getStatus())
                .setCustomIndex(config.getCustomOrder() == null ? 0 : config.getCustomOrder())
                .build();
        SaveOrUpdateRedPacketTokenConfigRequest request = SaveOrUpdateRedPacketTokenConfigRequest.newBuilder()
                .setTokenConfig(tokenConfig)
                .build();
        redPacketClient.saveRedPacketTokenConfig(request);
    }

    public void changeCustomOrder(Long orgId, String dataType, Map<Long, Integer> dataMap) {
        switch (Strings.nullToEmpty(dataType)) {
            case "theme":
                ChangeThemeCustomOrderRequest request = ChangeThemeCustomOrderRequest.newBuilder()
                        .setOrgId(orgId)
                        .putAllCustomOrderMap(dataMap)
                        .build();
                redPacketClient.changeThemeCustomOrder(request);
                break;
            case "tokenConfig":
                ChangeTokenConfigCustomOrderRequest request1 = ChangeTokenConfigCustomOrderRequest.newBuilder()
                        .setOrgId(orgId)
                        .putAllCustomOrderMap(dataMap)
                        .build();
                redPacketClient.changeTokenConfigCustomOrder(request1);
                break;
            default:
                return;
        }
    }

    public List<RedPacketDTO> queryRedPacketList(Long orgId, Long userId, Long fromId, Integer limit) {
        QueryRedPacketListRequest request = QueryRedPacketListRequest.newBuilder()
                .setOrgId(orgId)
                .setUserId(userId == null ? 0 : userId)
                .setFromId(fromId == null ? 0 : fromId)
                .setLimit(limit == null ? 100 : limit)
                .build();
        QueryRedPacketListResponse response = redPacketClient.queryRedPacketList(request);
        List<RedPacketDTO> redPacketList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(response.getRedPacketList())) {
            redPacketList = response.getRedPacketList().stream()
                    .map(this::convertRedPacket)
                    .collect(Collectors.toList());
        }
        return redPacketList;
    }

    public List<RedPacketReceiveDetailDTO> queryRedPacketReceiveDetailList(Long orgId, Long redPacketId, Long userId, Long fromId, Integer limit) {
        QueryRedPacketReceiveDetailListRequest request = QueryRedPacketReceiveDetailListRequest.newBuilder()
                .setOrgId(orgId)
                .setRedPacketId(redPacketId == null ? 0 : redPacketId)
                .setReceiveUserId(userId == null ? 0 : userId)
                .setFromId(fromId == null ? 0 : fromId)
                .setLimit(limit == null ? 100 : limit)
                .build();
        QueryRedPacketReceiveDetailListResponse response = redPacketClient.queryRedPacketReceiveDetailList(request);
        List<RedPacketReceiveDetailDTO> redPacketReceiveDetailList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(response.getReceiveDetailList())) {
            redPacketReceiveDetailList = response.getReceiveDetailList().stream()
                    .map(this::convertReceiveDetail)
                    .collect(Collectors.toList());
        }
        return redPacketReceiveDetailList;
    }

    private RedPacketDTO convertRedPacket(RedPacket redPacket) {
//        RedPacketThemeResult theme = getTheme(header, redPacket.getThemeId());
        return RedPacketDTO.builder()
                .id(redPacket.getId())
                .orgId(redPacket.getOrgId())
                .userId(redPacket.getUserId())
                .avatar(redPacket.getAvatar())
                .nickname(redPacket.getNickname())
                .username(redPacket.getUsername())
                .themeId(redPacket.getThemeId())
//                .backgroundUrl(theme == null ? "" : Strings.nullToEmpty(theme.getBackgroundUrl()))
//                .slogan(theme == null ? "" : Strings.nullToEmpty(theme.getSlogan()))
                .redPacketType(redPacket.getRedPacketTypeValue())
//                .password(redPacket.getPassword())
                .tokenId(redPacket.getTokenId())
                .tokenName(redPacket.getTokenName())
                .totalCount(redPacket.getTotalCount())
                .amount(redPacket.getAmount())
                .totalAmount(redPacket.getTotalAmount())
                .receiveUserType(redPacket.getReceiveUserTypeValue())
                .expired(redPacket.getExpired())
                .remainCount(redPacket.getRemainCount())
                .remainAmount(redPacket.getRemainAmount())
                .refundAmount(redPacket.getRefundAmount())
                .status(redPacket.getStatus())
                .created(redPacket.getCreated())
                .updated(redPacket.getUpdated())
                .build();
    }

    private RedPacketReceiveDetailDTO convertReceiveDetail(RedPacketReceiveDetail receiveDetail) {
//        RedPacketThemeResult theme = getTheme(header, receiveDetail.getThemeId());
        return RedPacketReceiveDetailDTO.builder()
                .id(receiveDetail.getId())
                .orgId(receiveDetail.getOrgId())
                .redPacketId(receiveDetail.getRedPacketId())
                .themeId(receiveDetail.getThemeId())
//                .backgroundUrl(theme == null ? "" : Strings.nullToEmpty(theme.getBackgroundUrl()))
//                .slogan(theme == null ? "" : Strings.nullToEmpty(theme.getSlogan()))
                .senderUserId(receiveDetail.getSenderUserId())
                .senderAvatar(receiveDetail.getSenderAvatar())
                .senderNickname(receiveDetail.getSenderNickname())
                .senderUsername(receiveDetail.getSenderUsername())
                .tokenId(receiveDetail.getTokenId())
                .tokenName(receiveDetail.getTokenName())
                .amount(receiveDetail.getAmount())
                .receiverUserId(receiveDetail.getReceiverUserId())
                .receiverAvatar(receiveDetail.getReceiverAvatar())
                .receiverNickname(receiveDetail.getReceiverNickname())
                .receiverUsername(receiveDetail.getReceiverUsername())
                .created(receiveDetail.getCreated())
                .firstOpen(false)
                .build();
    }

}
