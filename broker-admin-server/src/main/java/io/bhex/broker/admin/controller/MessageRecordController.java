package io.bhex.broker.admin.controller;


import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.DeliveryRecord;
import io.bhex.base.common.MessageReply;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.dto.DeliveryRecordDTO;
import io.bhex.broker.admin.controller.param.EmailTemplatePO;
import io.bhex.broker.admin.controller.param.ReceiverPO;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.grpc.client.impl.MessageRecordClient;
import io.bhex.broker.admin.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date: 2019/7/15 下午1:47
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class MessageRecordController  extends BrokerBaseController {

    @Autowired
    private MessageRecordClient messageRecordClient;

    @Autowired
    private BrokerUserClient brokerUserClient;

    @RequestMapping(value = "/message_record/list_devlivery_records")
    public ResultModel<List<DeliveryRecordDTO>> listDeliveryRecords(@RequestBody @Valid ReceiverPO po, AdminUserReply adminUser) {
        String receiver = Strings.nullToEmpty(po.getReceiver()).trim();
        if (StringUtils.isEmpty(receiver)) {
            return ResultModel.ok(Lists.newArrayList());
        }
        List<DeliveryRecord> records = Lists.newArrayList();
        if (!receiver.contains("@") && receiver.length() == 18 && NumberUtil.isDigits(receiver)) { //userid
            BrokerUserDTO dto = brokerUserClient.getBrokerUser(adminUser.getOrgId(),
                    Long.parseLong(receiver), "", "", "");
            if (dto == null) {
                return ResultModel.ok(Lists.newArrayList());
            }
            if (!StringUtils.isEmpty(dto.getRealMobile())) {
                List<DeliveryRecord> tmpRecords = messageRecordClient.getDeliveryRecords(adminUser.getOrgId(), dto.getRealMobile());
                records.addAll(tmpRecords);
            }
            if (!StringUtils.isEmpty(dto.getRealEmail())) {
                List<DeliveryRecord> tmpRecords = messageRecordClient.getDeliveryRecords(adminUser.getOrgId(), dto.getRealEmail());
                records.addAll(tmpRecords);
            }
            records.sort(Comparator.comparing(DeliveryRecord::getCreated).reversed());
        } else {
            records = messageRecordClient.getDeliveryRecords(adminUser.getOrgId(), Strings.nullToEmpty(po.getReceiver()).trim());
        }

        if (CollectionUtils.isEmpty(records)) {
            return ResultModel.ok(new ArrayList<>());
        }

        List<DeliveryRecordDTO> result = records.stream().map(r -> {
            DeliveryRecordDTO dto = new DeliveryRecordDTO();
            BeanUtils.copyProperties(r, dto);
            if (StringUtils.isEmpty(r.getContent().trim())) {
                dto.setContent("Admin");
            }
            if (dto.getDeliveryStatus().equals("SUCCESS")) {
                dto.setDeliveryStatus("DELIVERED");
            } else if (dto.getDeliveryStatus().equals("request-200")) {
                dto.setDeliveryStatus("ACCEPTED");
                dto.setDescription("");
            }
            if (!dto.getDeliveryStatus().equals("SUCCESS") && r.getChannel().length() > 2) {
                dto.setDeliveryStatus(r.getChannel().substring(0, 2) + " " + dto.getDeliveryStatus());
            }
            return dto;
        }).collect(Collectors.toList());
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/broker/edit_email_template", method = RequestMethod.POST)
    public ResultModel editEmailTemplate(@RequestBody @Valid EmailTemplatePO po, AdminUserReply adminUser) {
        MessageReply reply = messageRecordClient.editEmailTemplate(adminUser.getOrgId(), po);
        return ResultModel.ok();
    }

    @RequestMapping(value = "/broker/get_email_template", method = RequestMethod.POST)
    public ResultModel getEmailTemplate(AdminUserReply adminUser) {
        return ResultModel.ok(messageRecordClient.getEmailTemplate(adminUser.getOrgId()));
    }
}
