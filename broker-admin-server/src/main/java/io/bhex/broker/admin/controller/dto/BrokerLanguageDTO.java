package io.bhex.broker.admin.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerLanguageDTO {

    private String showName;

    private String language;

    private String icon;

    private String jsLoadUrl;

    private String currency;

    private List<String> jsLoadUrls;
}
