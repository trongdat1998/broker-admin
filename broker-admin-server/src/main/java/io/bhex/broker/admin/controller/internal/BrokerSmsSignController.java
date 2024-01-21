package io.bhex.broker.admin.controller.internal;

import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.param.SmsSignCreatePO;
import io.bhex.broker.admin.grpc.client.BrokerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description:
 * @Date: 2018/10/10 上午11:01
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/sms_sign")
public class BrokerSmsSignController {


    @Autowired
    private BrokerClient brokerClient;


    @AccessAnnotation(verifyLogin = false)
    @RequestMapping(value = "/create")
    public ResultModel<Boolean> createSmsSign(@RequestBody @Valid SmsSignCreatePO po) {

        Boolean result = brokerClient.updateBrokerSignName(po.getOrgId(), po.getSign());
        return ResultModel.ok(result);
    }


}
