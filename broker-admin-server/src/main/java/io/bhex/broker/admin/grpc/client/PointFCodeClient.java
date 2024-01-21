package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.admin.controller.dto.FCodeApplyDTO;

import java.util.List;

public interface PointFCodeClient {

    List<FCodeApplyDTO> getFCodeApplyList(long brokerId , int status , int page ,int pageSize);

    int changeFcodeApplyStatus(long applyId , long brokerId,int status);

}
