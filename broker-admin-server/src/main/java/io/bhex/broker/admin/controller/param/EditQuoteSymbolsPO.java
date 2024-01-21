package io.bhex.broker.admin.controller.param;

import lombok.Data;

import java.util.List;

@Data
public class EditQuoteSymbolsPO {

    private String tokenId;
    private List<String> symbols;
}
