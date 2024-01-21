package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller.dto
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:50 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ExchangeContractDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long brokerId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long exchangeId;
    private String exchangeName;
    private String remark;
    private String email;
    private String phone;
    private String companyName;
    private String contact;
    private Integer status;
    private Long createdAt;
}
