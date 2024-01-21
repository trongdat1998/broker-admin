package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.param.SymbolsPO;
import io.bhex.broker.admin.service.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/broker/config")
public class SymbolConfigController extends BrokerBaseController {

    @Autowired
    private SymbolService symbolService;


    @RequestMapping(value = "/query_recommend_symbols", method = RequestMethod.POST)
    public ResultModel queryRecommendSymbols() {
        List<String> symbols = symbolService.getRecommendSymbols(getOrgId());
        return ResultModel.ok(symbols);
    }

    @BussinessLogAnnotation(opContent = "Edit Recommend Symbols")
    @RequestMapping(value = "/edit_recommend_symbols", method = RequestMethod.POST)
    public ResultModel editRecommendSymbols(@RequestBody @Valid SymbolsPO po) {
        boolean r = symbolService.editRecommendSymbols(getOrgId(), po.getSymbols());
        return ResultModel.ok(r);
    }
}

