package io.bhex.broker.admin.service;

import io.bhex.base.token.TokenCategory;
import io.bhex.broker.admin.controller.dto.SymbolDTO;

import java.util.List;

public interface BrokerBasicService {

    List<SymbolDTO> listSymbol(long brokerId, TokenCategory category);

    List<String> listCurrency();
}
