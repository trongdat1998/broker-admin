package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.user.level.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class UserLevelClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private UserLevelServiceGrpc.UserLevelServiceBlockingStub getStub() {
        return grpcConfig.userLevelServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    public UserLevelConfigResponse userLevelConfig(UserLevelConfigRequest request) {
        return getStub().userLevelConfig(request);
    }

    public List<UserLevelConfigObj> listUserLevelConfigs(ListUserLevelConfigsRequest request) {
        return getStub().listUserLevelConfigs(request).getUserLevelConfigList();
    }

    public DeleteUserLevelConfigResponse deleteUserLevelConfig(DeleteUserLevelConfigRequest request) {
        return getStub().deleteUserLevelConfig(request);
    }

    public List<Long> queryLevelConfigUsers(QueryLevelConfigUsersRequest request) {
        return getStub().queryLevelConfigUsers(request).getUserIdList();
    }

    public AddWhiteListUsersResponse addWhiteListUsers(AddWhiteListUsersRequest request) {
        return getStub().addWhiteListUsers(request);
    }

    public Map<String, String> getDefaultWithdrawLimiter(long orgId) {
        return getStub().getDefaultWithdrawConfig(GetDefaultWithdrawConfigRequest.newBuilder().build()
                .newBuilder().setOrgId(orgId).build()).getConfigMap();
    }

    public QueryMyLevelConfigResponse queryMyLevelConfig(QueryMyLevelConfigRequest request) {
        return getStub().queryMyLevelConfig(request);
    }
}
