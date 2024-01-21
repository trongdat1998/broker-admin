package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionShortUrlDTO {

    private long id;
    private String shortUrlPc;
    private String shortUrlH5;
}
