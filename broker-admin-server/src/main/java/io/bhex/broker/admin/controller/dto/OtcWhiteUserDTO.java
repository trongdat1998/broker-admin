package io.bhex.broker.admin.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lizhen
 * @date 2018-11-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtcWhiteUserDTO {

    private String userId;

    private String nationalCode;

    private String mobile;

    private String email;

    private String realName;

    private String nickname;

    private String finishOrderfRate30Days;

    private int finishOrderNumber30Days;

    private Long accountId;

    private String usdtValue24HoursBuy;
}