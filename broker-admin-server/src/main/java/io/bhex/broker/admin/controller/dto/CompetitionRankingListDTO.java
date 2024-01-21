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
public class CompetitionRankingListDTO {

    private List<Integer> rankTypes;
    private int currentType;
    private List<String> days;

    private String tokenId;

    private List<Ranking> rankingList;

    private String currentDay;


    @Builder(builderClassName = "builder")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ranking{
        private Long uid;
        private String nickname;
        private String returnRate;
        private String returnAmount;
    }
}
