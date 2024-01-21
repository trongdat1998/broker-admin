package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.admin.controller.dto.NewsDTO;
import io.bhex.broker.admin.controller.dto.NewsTemplateDTO;
import io.bhex.broker.grpc.news.*;

import java.util.List;

public interface NewsClient {
    QueryNewsResponse queryNews(QueryNewsRequest request);

    CreateNewsResponse createNews(CreateNewsRequest request);

    ModifyNewsResponse modifyNews(ModifyNewsRequest request);

    UpdateStatusResponse updateStatus(UpdateStatusRequest request);

    GetTemplateResponse getTemplate(GetTemplateRequest request);

    SetTemplateResponse setTemplate(SetTemplateRequest request);
}
