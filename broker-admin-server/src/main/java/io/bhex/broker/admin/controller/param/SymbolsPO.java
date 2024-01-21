package io.bhex.broker.admin.controller.param;

import lombok.Data;

import java.util.List;

@Data
public class SymbolsPO {
    private List<String> symbols;
}
