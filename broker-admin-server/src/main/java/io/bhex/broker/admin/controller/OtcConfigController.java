package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.bhex.base.account.ExchangeReply;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerExtDTO;
import io.bhex.broker.admin.controller.dto.TokenDTO;
import io.bhex.broker.admin.controller.dto.TokenStatusDTO;
import io.bhex.broker.admin.controller.dto.OTCBrokerTokenDTO;
import io.bhex.broker.admin.controller.param.IdPO;
import io.bhex.broker.admin.controller.param.OTCBrokerCurrencyPO;
import io.bhex.broker.admin.controller.param.ParamPO;
import io.bhex.broker.admin.controller.param.SortTokenParam;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.grpc.client.impl.OrgClient;
import io.bhex.broker.admin.grpc.client.impl.OtcClient;
import io.bhex.broker.admin.service.BrokerBasicService;
import io.bhex.broker.admin.service.TokenService;
import io.bhex.broker.grpc.admin.BrokerDetail;
import io.bhex.ex.otc.*;
import io.bhex.ex.proto.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/otc/config")
public class OtcConfigController extends BrokerBaseController  implements MessageSourceAware {

    @Resource
    private OtcClient otcClient;

    @Resource
    private OrgClient orgClient;

    @Autowired
    private BrokerClient brokerClient;

    @Resource
    private TokenService tokenService;

    @Resource
    private BrokerBasicService brokerBasicService;

    private MessageSource messageSource;

    private List<String> langs= Lists.newArrayList("zh_CN", "en_US");

    private final static String defaultLang="en_US";


    private final static Map<String,String> currencyMultiLangMap= Maps.newConcurrentMap();

    static{
        currencyMultiLangMap.put("CNY-zh_CN","人民币");
        currencyMultiLangMap.put("HKD-zh_CN","港币");
        currencyMultiLangMap.put("JPY-zh_CN","日元");
        currencyMultiLangMap.put("KRW-zh_CN","韩元");
        currencyMultiLangMap.put("MOP-zh_CN","澳元");
        currencyMultiLangMap.put("RUB-zh_CN","卢布");
        currencyMultiLangMap.put("SGD-zh_CN","新加坡元");
        currencyMultiLangMap.put("THB-zh_CN","泰铢");
        currencyMultiLangMap.put("TWD-zh_CN","新台币");
        currencyMultiLangMap.put("USD-zh_CN","美元");
        currencyMultiLangMap.put("VND-zh_CN","越南盾");
        currencyMultiLangMap.put("MYR-zh_CN","马来西亚令吉");
        currencyMultiLangMap.put("MXN-zh_CN","墨西哥元");
        currencyMultiLangMap.put("IDR-zh_CN","印尼盾");
        currencyMultiLangMap.put("GBP-zh_CN","英镑");
        currencyMultiLangMap.put("KHR-zh_CN","柬埔寨瑞尔");
        currencyMultiLangMap.put("AUD-zh_CN","澳大利亚元");
        currencyMultiLangMap.put("NGN-zh_CN","尼日利亚奈拉");

        currencyMultiLangMap.put("CNY-en_US","CNY");
        currencyMultiLangMap.put("HKD-en_US","HKD");
        currencyMultiLangMap.put("JPY-en_US","JPY");
        currencyMultiLangMap.put("KRW-en_US","KRW");
        currencyMultiLangMap.put("MOP-en_US","MOP");
        currencyMultiLangMap.put("RUB-en_US","RUB");
        currencyMultiLangMap.put("SGD-en_US","SGD");
        currencyMultiLangMap.put("THB-en_US","THB");
        currencyMultiLangMap.put("TWD-en_US","TWD");
        currencyMultiLangMap.put("USD-en_US","USD");
        currencyMultiLangMap.put("VND-en_US","VND");
        currencyMultiLangMap.put("MYR-en_US","MYR");
        currencyMultiLangMap.put("MXN-en_US","MXN");
        currencyMultiLangMap.put("IDR-en_US","IDR");
        currencyMultiLangMap.put("GBP-en_US","GBP");
        currencyMultiLangMap.put("KHR-en_US","KHR");
        currencyMultiLangMap.put("AUD-en_US","AUD");
        currencyMultiLangMap.put("NGN-en_US","NGN");
    }

    @RequestMapping(value = "/token/setStatus", method = RequestMethod.POST)
    public ResultModel updateBrokerTokenStatus(@RequestBody TokenStatusDTO dto){

        if(Objects.isNull(dto.getStatus())){
            log.warn("status is null");
            return ResultModel.error("request.parameter.error");
        }

        ExchangeReply exchange=orgClient.findExchangeByBrokerId(getOrgId());
        if(Objects.isNull(exchange)){
            log.warn("Exchange is null");
            return ResultModel.error("request.parameter.error");
        }

        BrokerTokenStatusRequest req=BrokerTokenStatusRequest.newBuilder()
                .setOrgId(getOrgId())
                .setExchangeId(exchange.getExchangeId())
                .setTokenId(dto.getTokenId())
                .setTokenStatus(dto.getStatus().equals(1)?TokenStatusEnum.VALID:TokenStatusEnum.INVALID)
                .build();

        BaseResponse resp=otcClient.updateBrokerTokenStatus(req);
        if(resp.getResult()==OTCResult.SUCCESS){
            return ResultModel.ok();
        }

        String messageKey="otc.order.unclosed";
        return ResultModel.error(getMultiLanguageMsg(messageKey));
    }

    @RequestMapping(value = "/token/setShareStatus", method = RequestMethod.POST)
    public ResultModel setTokenShareStatus(@RequestBody TokenStatusDTO dto){

        if(Objects.isNull(dto.getShareStatus())){
            log.warn("share status is null");
            return ResultModel.error("request.parameter.error");
        }

        ExchangeReply exchange=orgClient.findExchangeByBrokerId(getOrgId());
        if(Objects.isNull(exchange)){
            log.warn("Exchange is null");
            return ResultModel.error("request.parameter.error");
        }

        ShareStatusEnum sse=dto.getShareStatus().equals(1)?ShareStatusEnum.SHARED:ShareStatusEnum.UN_SHARE;

        BrokerTokenStatusRequest req=BrokerTokenStatusRequest.newBuilder()
                .setOrgId(getOrgId())
                .setTokenId(dto.getTokenId())
                .setExchangeId(exchange.getExchangeId())
                .setShareStatus(sse)
                .build();

        BaseResponse resp=otcClient.updateBrokerTokenShareStatus(req);
        if(resp.getResult()==OTCResult.SUCCESS){
            return ResultModel.ok();
        }

        if(resp.getResult()==OTCResult.PERMISSION_DENIED){
            return ResultModel.error("otc.depth.share.not.allow");
        }

        return ResultModel.error(resp.getResult().name());
    }


    @RequestMapping(value = "/broker/ext/save", method = RequestMethod.POST)
    public ResultModel saveBrokerInfo(@RequestBody BrokerExtDTO dto) {
        Long brokerId=getOrgId();
        if(Objects.isNull(brokerId)||brokerId.longValue()<1){
            log.error("invalid brokerId {}",brokerId);
            return ResultModel.error("brokerId.not.empty");
        }

        if(StringUtils.isBlank(dto.getPhone())){
            return ResultModel.error("phone.not.empty");
        }
        BrokerDetail broker=brokerClient.getByBrokerId(brokerId);
        if(broker.getId()==0){
            return ResultModel.error("broker.not.empty");
        }
        dto.setBrokerId(brokerId);
        dto.setBrokerName(broker.getBrokerName());
        boolean success = otcClient.saveBrokerExt(dto);
        if(success){
            return ResultModel.ok();
        }else{
            return ResultModel.error("fail");
        }
    }

    @AccessAnnotation(authIds = {29, 30, 31, 801}) //订单管理也可以用
    @RequestMapping(value = "/broker/ext/get", method = RequestMethod.POST)
    public ResultModel<BrokerExtDTO> getBrokerInfo(@RequestBody(required = false) IdPO idPo) {
        Long brokerId=getOrgId();

        if(Objects.nonNull(idPo) && Objects.nonNull(idPo.getId()) && idPo.getId().longValue()>0){
            brokerId=idPo.getId();
        }

        BrokerExtDTO dto = otcClient.getBrokerExt(brokerId);
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/token/list")
    public ResultModel<List<OTCBrokerTokenDTO>> listToken(){

        Long brokerId=getOrgId();
        GetOTCTokensRequest req=GetOTCTokensRequest.newBuilder()
                .setOrgId(brokerId)
                .build();
        GetOTCTokensResponse resp=otcClient.listToken(req);

        List<OTCBrokerTokenDTO> list=resp.getTokenList().stream().map(i->{
            return OTCBrokerTokenDTO.builder()
                    .brokerId(brokerId)
                    .tokenName(i.getTokenName())
                    .tokenId(i.getTokenId())
                    .downRange(i.getDownRange())
                    .upRange(i.getUpRange())
                    .minQuote(i.getMinQuote())
                    .maxQuote(i.getMaxQuote())
                    .scale(i.getScale())
                    .sequence(i.getSequence())
                    .status(i.getStatus()==1?1:0)
                    .shareStatus(i.getShareStatus()==ShareStatusEnum.SHARED?1:0)
                    .feeRateOfSell(formatFeeRateOut(i.getExt().getFeeRateSell()))
                    .feeRateOfBuy(formatFeeRateOut(i.getExt().getFeeRateBuy()))
                    .build();
        }).collect(Collectors.toList());

        return ResultModel.ok(list);
    }



    @RequestMapping(value = "/broker/token/save")
    public ResultModel saveBrokerToken(@RequestBody @Valid OTCBrokerTokenDTO param ){

        if(Objects.isNull(param)){
            return ResultModel.error("request.parameter.error");
        }

        if(Strings.isNullOrEmpty(param.getFeeRateOfBuy())){
            param.setFeeRateOfBuy("0");
        }

        if(Strings.isNullOrEmpty(param.getFeeRateOfSell())){
            param.setFeeRateOfSell("0");
        }

        OTCToken token=OTCToken.newBuilder()
                .setScale(param.getScale())
                .setStatus(1)
                .setTokenId(param.getTokenId())
                .setTokenName(param.getTokenName())
                .setDownRange(param.getDownRange())
                .setUpRange(param.getUpRange())
                .setMaxQuote(param.getMaxQuote())
                .setMinQuote(param.getMinQuote())
                .setSequence(100)
                .setShareStatus(ShareStatusEnum.UN_SHARE)
                .setExt(OTCToken.TokenExt.newBuilder()
                        .setFeeRateBuy(formatFeeRateIn(param.getFeeRateOfBuy()))
                        .setFeeRateSell(formatFeeRateIn(param.getFeeRateOfSell()))
                        .build()
                )
                .build();

        SaveOTCBrokerTokenRequest req=SaveOTCBrokerTokenRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(getOrgId()).build())
                .setOtcToken(token)
                .build();
        BaseResponse resp=otcClient.saveBrokerToken(req);
        if(resp.getResult()==OTCResult.SUCCESS){
            return ResultModel.ok();
        }else{
            return ResultModel.error(resp.getResult().name());
        }
    }

    private String formatFeeRateOut(String value){
        if(Strings.isNullOrEmpty(value)){
            return "0";
        }

        return new BigDecimal(value).multiply(BigDecimal.valueOf(100)).setScale(6).stripTrailingZeros().toPlainString();
    }

    private String formatFeeRateIn(String value){
        if(Strings.isNullOrEmpty(value)){
            return "0";
        }

        return new BigDecimal(value).divide(BigDecimal.valueOf(100),6,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
    }

    @RequestMapping(value = "/broker/token/get")
    public ResultModel<OTCBrokerTokenDTO> getBrokerToken(@RequestBody ParamPO param){

        if(Strings.isNullOrEmpty(param.getParam())){
            return ResultModel.error("request.parameter.error");
        }

        GetOTCBrokerTokenRequest req=GetOTCBrokerTokenRequest.newBuilder()
                .setTokenId(param.getParam())
                .setBaseRequest(BaseRequest.newBuilder()
                        .setOrgId(getOrgId())
                        .build())
                .build();
        GetOTCBrokerTokenResponse resp=otcClient.getBrokerToken(req);
        if(resp.getResult()!=OTCResult.SUCCESS){
            return ResultModel.error(resp.getResult().name());
        }

        OTCToken token=resp.getToken();
        OTCBrokerTokenDTO dto=new OTCBrokerTokenDTO();
        dto.setTokenId(token.getTokenId());
        dto.setTokenName(token.getTokenName());
        dto.setDownRange(token.getDownRange());
        dto.setUpRange(token.getUpRange());
        dto.setMaxQuote(token.getMaxQuote());
        dto.setMinQuote(token.getMinQuote());
        dto.setScale(token.getScale());
        dto.setSequence(token.getSequence());
        dto.setShareStatus(token.getShareStatus()==ShareStatusEnum.SHARED?1:0);
        dto.setStatus(token.getStatus());
        dto.setFeeRateOfSell(formatFeeRateOut(token.getExt().getFeeRateSell()));
        dto.setFeeRateOfBuy(formatFeeRateOut(token.getExt().getFeeRateBuy()));

        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/broker/currency/save")
    public ResultModel saveOTCBrokerCurrency(@RequestBody @Valid OTCBrokerCurrencyPO po) {

        Long orgId=getOrgId();
        List<OTCCurrency> list=langs.stream().map(lang->{

            String key=po.getCode()+"-"+lang;
            String name=currencyMultiLangMap.get(key);
            if(StringUtils.isBlank(name)){
                key=po.getCode()+"-"+defaultLang;
                name=currencyMultiLangMap.get(key);
            }

            if(StringUtils.isBlank(name)){
                log.warn("There isn't any currency,key={}",key);
                return null;
            }

            return OTCCurrency.newBuilder()
                    .setAmountScale(po.getAmountScale())
                    .setOrgId(orgId)
                    .setScale(po.getScale())
                    .setStatus(po.getStatus()==1?1:-1)
                    .setCode(po.getCode())
                    .setLang(lang)
                    .setMaxQuote(po.getMaxQuote())
                    .setMinQuote(po.getMinQuote())
                    .setName(name)
                    .build();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(list)){
            return ResultModel.error("Invalid currency "+ po.getCode());
        }

        SaveOTCCurrencyRequest request=SaveOTCCurrencyRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(getOrgId()).build())
                .addAllCurrency(list)
                .build();
        BaseResponse resp = otcClient.saveOTCBrokerCurrency(request);
        if(resp.getResult()==OTCResult.SUCCESS){
            return ResultModel.ok();
        }

        String messageKey="otc.order.unclosed";
        return ResultModel.error(getMultiLanguageMsg(messageKey));
    }


    //已配置法币列表
    @RequestMapping(value = "/broker/currency/list")
    public ResultModel<List<OTCBrokerCurrencyPO>> getBrokerCurrency(){

        GetOTCCurrencysRequest req=GetOTCCurrencysRequest.newBuilder()
                .setOrgId(getOrgId())
                .build();
        GetOTCCurrencysResponse resp=otcClient.listBrokerCurrency(req);
        if(Objects.isNull(resp)){
            return ResultModel.error("fail");
        }
        List<OTCBrokerCurrencyPO> list=resp.getCurrencyList().stream()
                .filter(i->i.getLang().equals(defaultLang))
                .map(i->{
                    OTCBrokerCurrencyPO po=new OTCBrokerCurrencyPO();
                    po.setAmountScale(i.getAmountScale());
                    po.setCode(i.getCode());
                    po.setMaxQuote(i.getMaxQuote());
                    po.setMinQuote(i.getMinQuote());
                    po.setScale(i.getScale());
                    po.setStatus(i.getStatus()==1?1:0);
                    po.setConfiged(true);
                    return po;
            }).collect(Collectors.toList());


        List<String> allCurrencies=brokerBasicService.listCurrency();

        Set<String> exists=resp.getCurrencyList().stream().map(i->i.getCode()).collect(Collectors.toSet());
        List<OTCBrokerCurrencyPO> noneExist= allCurrencies.stream().filter(i->!exists.contains(i)).sorted()
                .map(i->{
                    OTCBrokerCurrencyPO po=new OTCBrokerCurrencyPO();
                    po.setCode(i);
                    po.setConfiged(false);
                    return po;
                }).collect(Collectors.toList());

        list.addAll(noneExist);
        return ResultModel.ok(list);
    }


    //todo 增加token列表
    @RequestMapping(value = "/token/select")
    public ResultModel<List<TokenDTO>> listTokenOpt() {
        Long orgId = getOrgId();
        List<TokenDTO> list = tokenService.listTokenByOrgId(orgId, 1);
        if(CollectionUtils.isEmpty(list)){
            return ResultModel.ok(list);
        }

        //排除已经配置过的token
        GetOTCTokensResponse resp=otcClient.listToken(GetOTCTokensRequest.newBuilder().setOrgId(orgId).build());
        Set<String> exists=resp.getTokenList().stream().map(i->i.getTokenId()).collect(Collectors.toSet());
        Comparator<TokenDTO> comparator=(o,n)->o.getTokenId().compareTo(n.getTokenId());
        list = list.stream().filter(i->!exists.contains(i.getTokenId())).sorted(comparator).collect(Collectors.toList());
        return ResultModel.ok(list);
    }

    @AccessAnnotation(verifyAuth = false)
    @RequestMapping(value = "/otc_tokens")
    public ResultModel<List<String>> listOtcTokens(AdminUserReply adminUser) {
        GetOTCTokensResponse resp = otcClient.listToken(GetOTCTokensRequest.newBuilder().setOrgId(adminUser.getOrgId()).build());
        List<String> tokens = resp.getTokenList().stream().map(OTCToken::getTokenId).collect(Collectors.toList());
        return ResultModel.ok(tokens);
    }

    //todo 增加货币列表
    @RequestMapping(value = "/currency/select")
    public ResultModel<List<String>> listCurrencyOpt(){
        List<String> list=brokerBasicService.listCurrency();
        if(CollectionUtils.isEmpty(list)){
            return ResultModel.ok(list);
        }

        GetOTCCurrencysRequest req=GetOTCCurrencysRequest.newBuilder()
                .setOrgId(getOrgId())
                .build();
        GetOTCCurrencysResponse resp=otcClient.listBrokerCurrency(req);
        Set<String> exists=resp.getCurrencyList().stream().map(i->i.getCode()).collect(Collectors.toSet());
        list = list.stream().filter(i->!exists.contains(i)).sorted().collect(Collectors.toList());
        return ResultModel.ok(list);
    }

    //todo 增加token排序
    @RequestMapping(value = "/token/sort", method = RequestMethod.POST)
    public ResultModel sortToken(@RequestBody SortTokenParam param){

        long brokerId=getOrgId();

        SortTokenRequest request = SortTokenRequest.newBuilder()
                .addAllTokens(param.getList())
                .setBaseRequest(BaseRequest.newBuilder().setOrgId(brokerId).build())
                .build();
        boolean success = otcClient.sortToken(request);
        if(success){
            return ResultModel.ok();
        }

        return ResultModel.error("Sort token fail");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMultiLanguageMsg(String messageKey){
        return messageSource.getMessage(messageKey,null, LocaleContextHolder.getLocale());
    }
}
