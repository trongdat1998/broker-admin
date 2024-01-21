package io.bhex.broker.admin.service.impl;

import io.bhex.base.exadmin.SignReply;
import io.bhex.bhop.common.grpc.client.AdminUserClient;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.grpc.client.BrokerSmsTemplateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class BrokerSmsSignServiceImpl {

    private static Long lastModifyTime = 1541064985972L;
    @Autowired
    private BrokerClient brokerClient;
    @Autowired
    private BrokerSmsTemplateClient brokerSmsTemplateClient;
    @Autowired
    private AdminUserClient adminUserClient;

    @Scheduled(cron = "0 0/10 * * * ?")
    private void syncSmsSigns(){
        List<Long> ids = adminUserClient.getOrgIds();
        List<SignReply> list = brokerSmsTemplateClient.getSmsSigns(ids, lastModifyTime);
        if(CollectionUtils.isEmpty(list)){
            return;
        }

        for(SignReply sign : list){
            log.info("smsSign:{}", sign);
            Boolean result = brokerClient.updateBrokerSignName(sign.getOrgId(), sign.getSign());
            log.info("update sign : {} {}", sign, result);
        }

        SignReply maxReply = list.stream().max(Comparator.comparing(SignReply::getUpdatedAt)).get();
        lastModifyTime = maxReply.getUpdatedAt();
        log.info("lastModifyTime:{}", lastModifyTime);
    }
}
