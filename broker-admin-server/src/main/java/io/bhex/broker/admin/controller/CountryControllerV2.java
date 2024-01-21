package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.bizlog.ExcludeLogAnnotation;
import io.bhex.bhop.common.dto.CountryDTO;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.grpc.client.BrokerConfigClient;
import io.bhex.broker.grpc.basic.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker
 * @Package: io.bhex.bhop.common.controller
 * @Author: ming.xu
 * @CreateDate: 2019/4/10 5:24 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Slf4j
@RestController
@ExcludeLogAnnotation
@RequestMapping("/api/v1/country_v2")
public class CountryControllerV2 {

    @Resource
    private BrokerConfigClient brokerConfigClient;

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/list")
    public ResultModel<Void> countryList() {
        List<Country> countries = brokerConfigClient.queryCountries().getCountryList();
        if (CollectionUtils.isEmpty(countries)) {
            return ResultModel.ok();
        }
        String _language = LocaleUtil.getLanguage();
        boolean matched = countries.stream().anyMatch(c -> c.getLanguage().equals(_language));
        String language = matched ? _language : Locale.US.toString();
        List<CountryDTO> countryDTOS = countries.stream()
                .filter(c -> c.getLanguage().equals(language))
                .map(country -> {
            return CountryDTO.builder()
                    .id(country.getId())
                    .nationalCode(country.getNationalCode())
                    .countryName(country.getName())
                    .shortName(country.getShortName())
                    .indexName(country.getIndexName())
                    .customOrder(country.getCustomOrder())
                    .build();
        }).collect(Collectors.toList());



        return ResultModel.ok(countryDTOS);
    }





}
