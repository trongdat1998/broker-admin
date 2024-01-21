package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.AgentCommissionDTO;
import io.bhex.broker.admin.controller.dto.QueryBrokerAgentDTO;
import io.bhex.broker.admin.controller.dto.QueryBrokerAgentUserDTO;
import io.bhex.broker.admin.controller.param.AddAgentPO;
import io.bhex.broker.admin.controller.param.QueryBrokerAgentPO;
import io.bhex.broker.admin.controller.param.QueryBrokerAgentUserPO;
import io.bhex.broker.admin.grpc.client.AgentClient;
import io.bhex.broker.grpc.agent.AddAgentUserResponse;
import io.bhex.broker.grpc.agent.CancelAgentUserResponse;
import io.bhex.broker.grpc.agent.RebindAgentUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/agent")
public class AgentController extends BrokerBaseController {

    @Autowired
    private AgentClient agentClient;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultModel addAgent(@RequestBody @Valid AddAgentPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        if (po.getUserId() == null || po.getUserId().equals(0)) {
            return ResultModel.error("User id not null");
        }

        if (StringUtils.isEmpty(po.getAgentName())) {
            return ResultModel.error("Agent name not null");
        }

        if (po.getCoinRate() == null || po.getCoinRate().compareTo(BigDecimal.ZERO) < 0) {
            return ResultModel.error("Coin rate not null");
        }

        if (po.getCoinRate().compareTo(BigDecimal.ZERO) > 0) {
            if (po.getCoinRate().compareTo(new BigDecimal("0.000001")) < 0) {
                return ResultModel.error("Coin rate not less than 0.0001");
            }
        }

        if (po.getCoinRate().compareTo(new BigDecimal("1")) > 0) {
            return ResultModel.error("Coin rate not big than 100");
        }

        if (po.getContractRate() == null || po.getContractRate().compareTo(BigDecimal.ZERO) < 0) {
            return ResultModel.error("Contract rate not null");
        }

        if (po.getContractRate().compareTo(BigDecimal.ZERO) > 0) {
            if (po.getContractRate().compareTo(new BigDecimal("0.000001")) < 0) {
                return ResultModel.error("Contract rate not less than 0.0001");
            }
        }

        if (po.getContractRate().compareTo(new BigDecimal("100")) > 0) {
            return ResultModel.error("Contract rate not big than 100");
        }

        try {
            AddAgentUserResponse response = agentClient.addAgentUser(orgId, po);
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else if (response != null) {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Add agent fail error {}", ex);
            return ResultModel.error("Add agent fail");
        }

        return ResultModel.ok();
    }

    @RequestMapping(value = "/query/broker/agent/list", method = RequestMethod.POST)
    public ResultModel queryBrokerAgentList(@RequestBody @Valid QueryBrokerAgentPO po, AdminUserReply adminUser) {
        long orgId = adminUser.getOrgId();

        if (po.getPage() == null || po.getPage().equals(0)) {
            po.setPage(1);
        }

        if (po.getLimit() == null || po.getLimit().equals(0)) {
            po.setLimit(20);
        }

        try {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("page", po.getPage());
            List<QueryBrokerAgentDTO> brokerAgentList = agentClient.queryBrokerAgentList(orgId, po);
            if (CollectionUtils.isNotEmpty(brokerAgentList)) {
                resultMap.put("data", brokerAgentList);
                return ResultModel.ok(resultMap);
            } else {
                resultMap.put("data", new ArrayList<>());
                return ResultModel.ok(resultMap);
            }
        } catch (Exception ex) {
            log.info("Query broker agent list fail error {}", ex);
            return ResultModel.error("Query broker agent list fail");
        }
    }

    @RequestMapping(value = "/query/broker/user/list", method = RequestMethod.POST)
    public ResultModel queryBrokerAgentUserList(@RequestBody @Valid QueryBrokerAgentUserPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        if (po.getPage() == null || po.getPage().equals(0)) {
            po.setPage(1);
        }

        if (po.getLimit() == null || po.getLimit().equals(0)) {
            po.setLimit(20);
        }

        try {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("page", po.getPage());
            List<QueryBrokerAgentUserDTO> brokerAgentUserList = agentClient.queryBrokerUserList(orgId, po);
            if (CollectionUtils.isNotEmpty(brokerAgentUserList)) {
                resultMap.put("data", brokerAgentUserList);
                return ResultModel.ok(resultMap);
            } else {
                resultMap.put("data", new ArrayList<>());
                return ResultModel.ok(resultMap);
            }
        } catch (Exception ex) {
            log.info("Query broker agent user list fail error {}", ex);
            return ResultModel.error("Query broker agent user list fail");
        }
    }

    @RequestMapping(value = "/cancel/agent", method = RequestMethod.POST)
    public ResultModel cancelAgentUser(@RequestBody @Valid AddAgentPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        if (po.getUserId() == null || po.getUserId().equals(0)) {
            return ResultModel.error("User id not null");
        }

        try {
            CancelAgentUserResponse response = agentClient.cancelAgentUser(orgId, po.getUserId());
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else if (response != null) {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Cancel agent error {}", ex);
            return ResultModel.error("Cancel agent fail");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/rebind/agent", method = RequestMethod.POST)
    public ResultModel rebindAgentUser(@RequestBody @Valid AddAgentPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }
        if (po.getUserId() == null || po.getUserId().equals(0)) {
            return ResultModel.error("User id not null");
        }
        if (po.getTargetUserId() == null || po.getTargetUserId().equals(0)) {
            return ResultModel.error("Target user id not null");
        }
        try {
            RebindAgentUserResponse response = agentClient.rebindAgentUser(orgId, po.getUserId(), po.getTargetUserId());
            if (response != null && response.getBasicRet().getCode() == 0) {
                return ResultModel.ok();
            } else if (response != null) {
                return ResultModel.error(response.getBasicRet().getMsg());
            }
        } catch (Exception ex) {
            log.info("Rebind agent error orgId {} userId {} targetUserId {} error {} ", orgId, po.getUserId(), po.getTargetUserId(), ex);
            return ResultModel.error("Rebind agent fail");
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/query/agent/commission/list", method = RequestMethod.POST)
    public ResultModel queryAgentCommissionList(@RequestBody @Valid QueryBrokerAgentUserPO po) {
        long orgId = getOrgId();
        if (orgId <= 0) {
            return ResultModel.error("Org id not null");
        }

        if (po.getFromId() == null) {
            po.setFromId(0);
        }

        if (po.getEndId() == null) {
            po.setEndId(0);
        }

        if (po.getLimit() == null) {
            po.setLimit(20);
        }

        try {
            List<AgentCommissionDTO> agentCommissionList = agentClient.queryAgentCommissionList(orgId, po);
            if (CollectionUtils.isNotEmpty(agentCommissionList)) {
                return ResultModel.ok(agentCommissionList);
            } else {
                return ResultModel.ok(new ArrayList<>());
            }
        } catch (Exception ex) {
            log.info("Query agent commission list error {}", ex);
            return ResultModel.error("Query agent commission list fail");
        }
    }

}
