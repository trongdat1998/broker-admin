package io.bhex.broker.admin.controller.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenStatusDTO {

    private Long brokerId;

    @NotNull
    private String tokenId;

    //状态，0=禁用,1=启用
    private Integer status;

    //共享状态,0=不共享,1=共享
    private Integer shareStatus;
}
