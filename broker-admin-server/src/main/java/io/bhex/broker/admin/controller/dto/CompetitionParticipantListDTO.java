package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(builderClassName = "builder")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionParticipantListDTO {

    private int total;

    private List<CompetitionParticipantDTO> list;

    @Builder(builderClassName = "builder")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitionParticipantDTO{
        private long userId;
        private String nickname;
        private boolean isWhiteList;
        private String wechat;
    }
}
