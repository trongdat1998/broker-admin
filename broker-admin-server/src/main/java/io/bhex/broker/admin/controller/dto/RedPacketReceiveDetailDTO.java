package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketReceiveDetailDTO {

    private Long id;
    private Long orgId;
    private Long redPacketId;
    private Long themeId; // 红包主题, 包含了红包的背景图和slogan
    private String backgroundUrl; // 背景图
    private String slogan; // 祝福语
    private Long senderUserId; // 冗余
    private String senderAvatar;
    private String senderNickname;
    private String senderUsername;
    private String tokenId;
    private String tokenName;
    private String amount;
//    private BigDecimal equivalentUsdtAmount; // 折合USDT
    private Long receiverUserId; // 领取人
//    private Long receiverAccountId; // 领确认accountId
    private String receiverAvatar; // 冗余用户的头像
    private String receiverNickname;
    private String receiverUsername;
    private Long created;
    private Long updated;
    private Boolean firstOpen;

}
