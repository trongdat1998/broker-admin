package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.NewsDTO;
import io.bhex.broker.admin.controller.dto.NewsTemplateDTO;
import io.bhex.broker.admin.controller.dto.OTCPaymentDTO;
import io.bhex.broker.admin.controller.dto.OtcWhiteUserDTO;

import java.util.List;

public interface NewsService {

    List<NewsDTO> queryNews(Long orgId, Long latestId, Integer limit);

    int createNews(Long orgId, NewsDTO news);

    int modifyNews(Long orgId, NewsDTO news);

    int publishNews(Long orgId, Long id);

    int closeNews(Long orgId, Long id);

    int deleteNews(Long orgId, Long id);

    NewsTemplateDTO getTemplate(Long orgId, String name);

    int setTemplate(Long orgId, String name, String content, String params);
}
