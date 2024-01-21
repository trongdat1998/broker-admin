package io.bhex.broker.admin.controller.param;


import lombok.Data;

@Data
public class QueryUserLockPo {

    private Long userId;

    private String tokenId;
}
