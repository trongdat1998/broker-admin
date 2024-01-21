package io.bhex.broker.admin.controller.param;


import lombok.Data;

@Data
public class QueryUserLockListPo {

    private Long userId;

    private Integer type = 1; //1其他锁仓，4位空投锁仓

    private Integer page;

    private Integer size;
}
