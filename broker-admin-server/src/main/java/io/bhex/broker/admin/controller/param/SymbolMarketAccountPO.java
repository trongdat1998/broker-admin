package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymbolMarketAccountPO {

    private Long orgId;

    private List<SymbolMarketAccountDetailPO> symbolMarketAccountList;
}
