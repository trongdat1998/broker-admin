package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleOptionOrderPO {

    private Long userId;

    private String email;

    private String phone;

    private Long fromId = 0L;

    private Long lastId = 0L;

    private Long startTime = 0L;

    private Long endTime = 0L;

    private String baseTokenId;

    private String quoteTokenId;

    @JsonProperty(defaultValue = "20")
    private Integer pageSize = 20;
}
