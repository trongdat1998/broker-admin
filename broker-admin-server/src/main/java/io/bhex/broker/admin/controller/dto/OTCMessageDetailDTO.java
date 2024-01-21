package io.bhex.broker.admin.controller.dto;


import lombok.Data;

/**
 * @Description:
 * @Date: 2019/1/18 上午10:49
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class OTCMessageDetailDTO {

    private String messageType;

    private String message;
    /**
     * 创建时间
     */
    private Long createDate;
}
