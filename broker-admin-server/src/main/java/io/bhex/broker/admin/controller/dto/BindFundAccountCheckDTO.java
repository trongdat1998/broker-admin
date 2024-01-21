package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindFundAccountCheckDTO {

    private Long accountId;
    private Long userId;
    private Integer authType;
    private Long requestId;

}
