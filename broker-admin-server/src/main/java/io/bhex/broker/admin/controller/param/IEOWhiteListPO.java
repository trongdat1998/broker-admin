package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IEOWhiteListPO {

    private Long brokerId;

    private Long projectId;

    private List<String> userList;
}
