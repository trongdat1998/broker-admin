package io.bhex.broker.admin.grpc.client;


import io.bhex.broker.admin.controller.dto.AgentCommissionDTO;
import io.bhex.broker.admin.controller.dto.QueryBrokerAgentDTO;
import io.bhex.broker.admin.controller.dto.QueryBrokerAgentUserDTO;
import io.bhex.broker.admin.controller.param.AddAgentPO;
import io.bhex.broker.admin.controller.param.QueryBrokerAgentPO;
import io.bhex.broker.admin.controller.param.QueryBrokerAgentUserPO;
import io.bhex.broker.grpc.agent.AddAgentUserResponse;
import io.bhex.broker.grpc.agent.CancelAgentUserResponse;
import io.bhex.broker.grpc.agent.RebindAgentUserResponse;

import java.util.List;

public interface AgentClient {

    AddAgentUserResponse addAgentUser(Long orgId, AddAgentPO addAgent);

    List<QueryBrokerAgentDTO> queryBrokerAgentList(Long orgId, QueryBrokerAgentPO brokerAgent);

    List<QueryBrokerAgentUserDTO> queryBrokerUserList(Long orgId, QueryBrokerAgentUserPO brokerAgentUserPO);

    CancelAgentUserResponse cancelAgentUser(Long orgId, Long userId);

    RebindAgentUserResponse rebindAgentUser(Long orgId, Long userId, Long targetUserId);

    List<AgentCommissionDTO> queryAgentCommissionList(Long orgId, QueryBrokerAgentUserPO brokerAgentUserPO);

}
