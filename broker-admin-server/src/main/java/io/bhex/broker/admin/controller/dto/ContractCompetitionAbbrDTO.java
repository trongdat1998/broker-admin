package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractCompetitionAbbrDTO {

    private Long id;

    private String duration;

    private String code;

    private String contractId;

    private String contractName;

    private String tokenId;

    private Integer status;

    private String start;

    private String end;
}
