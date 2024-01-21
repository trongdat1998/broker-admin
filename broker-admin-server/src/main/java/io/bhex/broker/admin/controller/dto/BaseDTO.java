package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * dto base class
 *
 * @author songxd
 * @date 2021-01-15
 */
@Data
public class BaseDTO {
    private Integer authType;
    private String verifyCode;
}
