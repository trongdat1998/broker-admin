package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuerySymbolListPO {
    private Integer category;

    private Long exchangeId;
}
