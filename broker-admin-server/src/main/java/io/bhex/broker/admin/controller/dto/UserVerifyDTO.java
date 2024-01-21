package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 24/08/2018 5:44 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class UserVerifyDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orgId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String nationality;

    private String firstName;
    private String secondName;
    private Integer gender;
    private Integer cardType;
    private String countryName;
    private String cardTypeStr;
    private String cardNo;
    private String cardFrontUrl;
    private String cardBackUrl;
    private String cardHandUrl;
    private Integer verifyStatus;
    private Long created;
    private Long updated;

    private Integer kycLevel;
    private String facePhotoUrl;
    private String faceVideoUrl;
    private String videoUrl;
}
