package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketDTO {

    private Long id;
    private Long orgId;
    private Long userId;
    private String avatar; // 冗余用户的头像
    private String nickname; // 冗余用户的昵称
    private String username;
//    private String inviteUrl;
//    private String inviteCode; // 用户的邀请码
    private Long themeId; // 红包主题, 包含了红包的背景图和slogan
    private String backgroundUrl; // 背景图
    private String slogan; // 祝福语
    private Integer redPacketType; // 红包类型 0 普通红包 红包个数 + 单个红包金额 1 拼手气红包 总金额 + 红包个数
    private Integer needPassword; // 是否设置口令
//    private String password; // 口令
    private String tokenId; // 币种
    private String tokenName;
    private Integer totalCount; // 个数
    private String amount; // 普通红包单个金额
    private String totalAmount; // 总金额 普通红包总金额 或者 拼手气红包总金额
//    private BigDecimal equivalentUsdtAmount; // 折合USDT
    private Integer receiveUserType; // 0 不限制 1 仅限新用户  2 仅限老用户
    private Long expired; // 失效时间
    private Integer remainCount;
    private String remainAmount;
    private String refundAmount;
    private Integer status;
    private Long created;
    private Long updated;

}
