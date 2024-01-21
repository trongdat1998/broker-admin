package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.controller.dto.AgentCommissionDTO;
import io.bhex.broker.admin.controller.dto.QueryBrokerAgentDTO;
import io.bhex.broker.admin.controller.dto.QueryBrokerAgentUserDTO;
import io.bhex.broker.admin.controller.param.AddAgentPO;
import io.bhex.broker.admin.controller.param.QueryBrokerAgentPO;
import io.bhex.broker.admin.controller.param.QueryBrokerAgentUserPO;
import io.bhex.broker.admin.grpc.client.AgentClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.agent.AddAgentUserRequest;
import io.bhex.broker.grpc.agent.AddAgentUserResponse;
import io.bhex.broker.grpc.agent.AgentServiceGrpc;
import io.bhex.broker.grpc.agent.CancelAgentUserRequest;
import io.bhex.broker.grpc.agent.CancelAgentUserResponse;
import io.bhex.broker.grpc.agent.QueryAgentCommissionListRequest;
import io.bhex.broker.grpc.agent.QueryAgentCommissionListResponse;
import io.bhex.broker.grpc.agent.QueryBrokerAgentListRequest;
import io.bhex.broker.grpc.agent.QueryBrokerAgentListResponse;
import io.bhex.broker.grpc.agent.QueryBrokerUserListRequest;
import io.bhex.broker.grpc.agent.QueryBrokerUserListResponse;
import io.bhex.broker.grpc.agent.RebindAgentUserRequest;
import io.bhex.broker.grpc.agent.RebindAgentUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AgentClientImpl implements AgentClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AgentServiceGrpc.AgentServiceBlockingStub getAccountStub() {
        return grpcConfig.agentServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public AddAgentUserResponse addAgentUser(Long orgId, AddAgentPO addAgent) {
        AddAgentUserResponse addAgentUserResponse = getAccountStub().addAgentUser(AddAgentUserRequest
                .newBuilder()
                .setOrgId(orgId)
                .setUserId(addAgent.getUserId())
                .setAgentName(StringUtils.isNotEmpty(addAgent.getAgentName()) ? addAgent.getAgentName() : "")
                .setLeader(StringUtils.isNotEmpty(addAgent.getLeader()) ? addAgent.getLeader() : "")
                .setMobile(StringUtils.isNotEmpty(addAgent.getMobile()) ? addAgent.getMobile() : "")
                .setMark(StringUtils.isNotEmpty(addAgent.getMark()) ? addAgent.getMark() : "")
                .setCoinDefaultRate(addAgent.getCoinRate().stripTrailingZeros().toPlainString())
                .setCoinChildrenDefaultRate(addAgent.getLowLevelCoinRate().stripTrailingZeros().toPlainString())
                .setContractDefaultRate(addAgent.getContractRate().stripTrailingZeros().toPlainString())
                .setContractChildrenDefaultRate(addAgent.getLowLevelContractRate().stripTrailingZeros().toPlainString())
                .build());
        return addAgentUserResponse;
    }

    @Override
    public List<QueryBrokerAgentDTO> queryBrokerAgentList(Long orgId, QueryBrokerAgentPO brokerAgent) {
        QueryBrokerAgentListResponse response = getAccountStub().queryBrokerAgentList(QueryBrokerAgentListRequest
                .newBuilder()
                .setOrgId(orgId)
                .setUserId(brokerAgent.getUserId() != null ? brokerAgent.getUserId() : 0L)
                .setAgentName(StringUtils.isNotEmpty(brokerAgent.getAgentName()) ? brokerAgent.getAgentName() : "")
                .setPage(brokerAgent.getPage())
                .setLimit(brokerAgent.getLimit())
                .build());


        List<QueryBrokerAgentDTO> queryBrokerAgentDTOList = new ArrayList<>();
        if (response != null && response.getAgentInfoCount() > 0) {
            response.getAgentInfoList().forEach(agentInfo -> {
                queryBrokerAgentDTOList.add(QueryBrokerAgentDTO.builder()
                        .userId(String.valueOf(agentInfo.getUserId()))
                        .agentName(agentInfo.getAgentName())
                        .mobile(agentInfo.getMobile())
                        .email(agentInfo.getEmail())
                        .level(agentInfo.getLevel())
                        .leader(agentInfo.getLeader())
                        .leaderMobile(agentInfo.getLeaderMobile())
                        .superiorName(agentInfo.getSuperiorName())
                        .peopleNumber(StringUtils.isNotEmpty(agentInfo.getPeopleNumber()) ? Integer.parseInt(agentInfo.getPeopleNumber()) : 0)
                        .time(agentInfo.getTime())
                        .mark(agentInfo.getMark())
                        .coinRate(new BigDecimal(agentInfo.getCoinRate()))
                        .contractRate(new BigDecimal(agentInfo.getContractRate()))
                        .lowLevelCoinRate(new BigDecimal(agentInfo.getLowLevelCoinRate()))
                        .lowLevelContractRate(new BigDecimal(agentInfo.getLowLevelContractRate()))
                        .status(StringUtils.isNotEmpty(agentInfo.getStatus()) ? Integer.parseInt(agentInfo.getStatus()) : 0)
                        .build());
            });
        } else {
            return new ArrayList<>();
        }
        return queryBrokerAgentDTOList;
    }

    @Override
    public List<QueryBrokerAgentUserDTO> queryBrokerUserList(Long orgId, QueryBrokerAgentUserPO brokerAgentUserPO) {
        QueryBrokerUserListResponse response = getAccountStub().queryBrokerUserList(QueryBrokerUserListRequest
                .newBuilder()
                .setOrgId(orgId)
                .setEmail(StringUtils.isNotEmpty(brokerAgentUserPO.getEmail()) ? brokerAgentUserPO.getEmail() : "")
                .setMobile(StringUtils.isNotEmpty(brokerAgentUserPO.getMobile()) ? brokerAgentUserPO.getMobile() : "")
                .setUserId(brokerAgentUserPO.getUserId() != null ? brokerAgentUserPO.getUserId() : 0L)
                .setAgentName(StringUtils.isNotEmpty(brokerAgentUserPO.getAgentName()) ? brokerAgentUserPO.getAgentName() : "")
                .setPage(brokerAgentUserPO.getPage())
                .setLimit(brokerAgentUserPO.getLimit())
                .build());


        List<QueryBrokerAgentUserDTO> queryBrokerAgentDTOList = new ArrayList<>();
        if (response != null && response.getUserInfoCount() > 0) {
            response.getUserInfoList().forEach(agentInfo -> {
                queryBrokerAgentDTOList.add(QueryBrokerAgentUserDTO.builder()
                        .id(String.valueOf(agentInfo.getId()))
                        .userId(String.valueOf(agentInfo.getUserId()))
                        .mobile(agentInfo.getMobile())
                        .email(agentInfo.getEmail())
                        .userName(agentInfo.getUserName())
                        .country(agentInfo.getCountry())
                        .agentName(agentInfo.getAgentName())
                        .agentUserId(agentInfo.getAgentUserId())
                        .agentLevel(StringUtils.isNotEmpty(agentInfo.getAgentLevel()) ? Integer.parseInt(agentInfo.getAgentLevel()) : 0)
                        .registerTime(agentInfo.getRegisterTime())
                        .status(StringUtils.isNotEmpty(agentInfo.getStatus()) ? Integer.parseInt(agentInfo.getStatus()) : 0)
                        .build());
            });
        } else {
            return new ArrayList<>();
        }
        return queryBrokerAgentDTOList;
    }

    @Override
    public CancelAgentUserResponse cancelAgentUser(Long orgId, Long userId) {
        CancelAgentUserResponse cancelAgentUserResponse = getAccountStub().cancelAgentUser(CancelAgentUserRequest
                .newBuilder().setOrgId(orgId).setUserId(userId).build());
        return cancelAgentUserResponse;
    }

    @Override
    public RebindAgentUserResponse rebindAgentUser(Long orgId, Long userId, Long targetUserId) {
        RebindAgentUserResponse rebindAgentUserResponse = getAccountStub().rebindAgentUser(RebindAgentUserRequest
                .newBuilder().setOrgId(orgId).setUserId(userId).setTargetUserId(targetUserId).build());
        return rebindAgentUserResponse;
    }

    @Override
    public List<AgentCommissionDTO> queryAgentCommissionList(Long orgId, QueryBrokerAgentUserPO brokerAgent) {
        QueryAgentCommissionListResponse response = getAccountStub().queryAgentCommissionList(QueryAgentCommissionListRequest
                .newBuilder()
                .setOrgId(orgId)
                .setTargetUserId(brokerAgent.getUserId() != null ? brokerAgent.getUserId() : 0L)
                .setFromId(brokerAgent.getFromId())
                .setEndId(brokerAgent.getEndId())
                .setLimit(brokerAgent.getLimit())
                .setIsAdmin(1)
                .setStartTime(StringUtils.isNotEmpty(brokerAgent.getStartTime()) ? brokerAgent.getStartTime() : "")
                .setEndTime(StringUtils.isNotEmpty(brokerAgent.getEndTime()) ? brokerAgent.getEndTime() : "")
                .setTokenId(StringUtils.isNotEmpty(brokerAgent.getTokenId()) ? brokerAgent.getTokenId() : "")
                .build());


        List<AgentCommissionDTO> agentCommissionDTOList = new ArrayList<>();
        if (response != null && response.getCommissionCount() > 0) {
            response.getCommissionList().forEach(agentInfo -> {
                agentCommissionDTOList.add(AgentCommissionDTO.builder()
                        .id(String.valueOf(agentInfo.getId()))
                        .brokerId(agentInfo.getBrokerId())
                        .agentName(agentInfo.getAgentName())
                        .superiorName(agentInfo.getSuperiorName())
                        .superiorUserId(String.valueOf(agentInfo.getSuperiorUserId()))
                        .tokenId(agentInfo.getTokenId())
                        .agentFee(agentInfo.getAgentFee())
                        .time(agentInfo.getTime())
                        .userId(String.valueOf(agentInfo.getUserId()))
                        .build());
            });
        } else {
            return new ArrayList<>();
        }
        return agentCommissionDTOList;
    }
}
