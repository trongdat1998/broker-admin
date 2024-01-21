package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.app_push.*;

public interface PushAdminClient {

    SendAdminTestPushResponse sendAdminTestPush(SendAdminTestPushRequest request);

    AddAdminPushTaskResponse addAdminPushTask(AddAdminPushTaskRequest request);

    EditAdminPushTaskResponse editAdminPushTask(EditAdminPushTaskRequest request);

    QueryAdminPushTaskResponse queryAdminPushTask(QueryAdminPushTaskRequest request);

    DeleteAdminPushTaskResponse deleteAdminPushTask(DeleteAdminPushTaskRequest request);

    CancelAdminPushTaskResponse cancelAdminPushTask(CancelAdminPushTaskRequest request);

    QueryAdminPushTaskSimplesResponse queryAdminPushTaskSimples(QueryAdminPushTaskSimplesRequest request);

}
