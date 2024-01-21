package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.common.*;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.controller.param.EmailTemplatePO;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.admin.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date: 2019/7/15 下午1:50
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@Service
public class MessageRecordClient {

    @Resource
    GrpcClientConfig grpcConfig;

    public MessageServiceGrpc.MessageServiceBlockingStub getStub() {
        return grpcConfig.messageServiceBlockingStub(GrpcConfig.COMMON_SERVER_CHANNEL_NAME);
    }



    public List<DeliveryRecord> getDeliveryRecords(long orgId, String receiver) {

        ListDeliveryRecordsRequest request = ListDeliveryRecordsRequest.newBuilder()
                .setOrgId(orgId)
                .setReceiver(receiver).build();
        return getStub().listDeliveryRecords(request).getRecordList();
    }

    public MessageReply editEmailTemplate(Long orgId, EmailTemplatePO po) {
        List<EmailTemplate> list = po.getList().stream().filter(i -> i.getEnabled()).map(i -> {
            EmailTemplate.Builder builder = EmailTemplate.newBuilder();
            BeanCopyUtils.copyPropertiesIgnoreNull(i, builder);
            builder.setTemplateContent(i.getEmailTemplate());
            return builder.build();
        }).collect(Collectors.toList());
        EditEmailTemplateRequest request = EditEmailTemplateRequest.newBuilder()
                .setOrgId(orgId)
                .addAllEmailTemplate(list)
                .build();
        MessageReply reply = getStub().editEmailTemplate(request);
        log.info("reply:{}", reply);
        return reply;
    }

    public EmailTemplatePO getEmailTemplate(Long orgId) {
        QueryEmailTemplatesRequest request = QueryEmailTemplatesRequest.newBuilder()
                .setOrgId(orgId)
                .build();

        QueryEmailTemplateReply reply = getStub().queryEmailTemplates(request);
        List<EmailTemplate> images = reply.getEmailTemplateList();
        if (CollectionUtils.isEmpty(images)) {
            return new EmailTemplatePO();
        }
        EmailTemplatePO po = new EmailTemplatePO();
        List<EmailTemplatePO.EmailTemplate> list = images.stream().map(i -> {
            EmailTemplatePO.EmailTemplate vo = new EmailTemplatePO.EmailTemplate();
            BeanCopyUtils.copyPropertiesIgnoreNull(i, vo);
            vo.setEmailTemplate(i.getTemplateContent());
            vo.setEnabled(true);
            return vo;
        }).collect(Collectors.toList());
        po.setList(list);
        return po;
    }

}
