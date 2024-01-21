package io.bhex.broker.admin.controller.param;


import lombok.Data;

@Data
public class ListParticipantPO {

    private int pageNo;
    private int pageSize;
    private long competitionId;
}
