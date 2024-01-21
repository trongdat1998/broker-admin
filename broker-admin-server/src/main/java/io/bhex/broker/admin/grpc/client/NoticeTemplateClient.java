package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.notice.NoticesBusinessType;

public interface NoticeTemplateClient {
    public Long getTemplateId(Long brokerId, int noticeType, NoticesBusinessType.TypeEnum businessType, String language);
}
