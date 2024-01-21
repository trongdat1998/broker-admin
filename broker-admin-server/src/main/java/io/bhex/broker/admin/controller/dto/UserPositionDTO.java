package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPositionDTO {

    private Long accountId;

    private String locked;

    private String available;

    private String total;

    private String tokenId;

    private String borrowed;
}
