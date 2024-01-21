package io.bhex.broker.admin.controller.param;

import lombok.Data;

import java.util.List;

@Data
public class EditCustomerQuoteSymbolsPO {

    private String customerQuoteId;

    private List<String> symbols;
}
