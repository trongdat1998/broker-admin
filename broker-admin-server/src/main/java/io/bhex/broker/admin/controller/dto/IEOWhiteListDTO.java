package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IEOWhiteListDTO {

    private Long brokerId;

    private Long projectId;

    private List<String> userList;
}
