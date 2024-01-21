package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.model
 * @Author: ming.xu
 * @CreateDate: 09/08/2018 10:19 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerSymbolTaskConfigDTO implements Serializable {

    private Long id;

    private Long exchangeId;

    @NotEmpty
    private String symbolId;

    private Boolean published = false;

    private Boolean banSellStatus = false;

    private Boolean banBuyStatus = false;

    private Boolean showStatus = false;

    @NotNull
    private Long actionTime;

    private Long created;

    private Long updated;

    private Integer status = 1;

    private Integer dailyTask;

    private Boolean publishToken = false; //强制开启 未开启的币种


}
