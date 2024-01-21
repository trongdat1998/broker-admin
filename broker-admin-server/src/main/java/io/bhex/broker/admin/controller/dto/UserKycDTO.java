package io.bhex.broker.admin.controller.dto;

import lombok.Data;
/**
 * @Description: 对应broker中的PersonalVerifyInfo
 * @Date: 2018/8/23 下午8:31
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Data
public class UserKycDTO {
    private String nationality;
    private String firstName;
    private String secondName;
    private int gender = 0;
    private String cardType;
    private String cardNo;


    private String cardFrontUrl;
    private String cardBackUrl;

    private String cardHandUrl;

    //kyc 状态：1 审核中  2 审核通过 3 审核未通过，重新上传
    private int verifyStatus;

    //审核完成时间
    private Long passedTime;
    private Long updated;

    private Long userVerifyId;


    private Integer kycLevel;
    private String facePhotoUrl;
    private String faceVideoUrl;
    private String videoUrl;



}
