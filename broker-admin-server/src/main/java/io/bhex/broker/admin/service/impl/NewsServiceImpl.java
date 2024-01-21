package io.bhex.broker.admin.service.impl;

import io.bhex.broker.admin.controller.dto.NewsDTO;
import io.bhex.broker.admin.controller.dto.NewsDetailsDTO;
import io.bhex.broker.admin.controller.dto.NewsTemplateDTO;
import io.bhex.broker.admin.grpc.client.NewsClient;
import io.bhex.broker.admin.service.NewsService;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.news.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsServiceImpl implements NewsService {

    @Resource
    NewsClient newsClient;

    @Override
    public List<NewsDTO> queryNews(Long orgId, Long id, Integer limit) {
        QueryNewsRequest request = QueryNewsRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setLimit(limit)
                .setLatestId(id == 0l ? Long.MAX_VALUE : id)//
                .setReverse(true)//一直往小了搜索比latestId小的
                .setDesc(true)//倒序返回
                .build();
        QueryNewsResponse response = newsClient.queryNews(request);
        return response.getNewsList().stream().map(news -> {
            return changeNews(news);
        }).collect(Collectors.toList());
    }

    public NewsDTO changeNews(News news) {
        return NewsDTO.builder()
                .created(news.getCreated())
                .updated(news.getUpdated())
                .id(news.getId())
                .status(news.getStatus())
                .newsId(news.getNewsId())
                .newsPath(news.getNewsPath())
                .version(news.getVersion())
                .published(news.getPublished())
                .details(news.getDetailsList() == null ? new LinkedList<>() : news.getDetailsList().stream().map(detail -> {
                    return NewsDetailsDTO.builder()
                            .content(detail.getContent())
                            .id(detail.getId())
                            .images(detail.getImages())
                            .language(detail.getLanguage())
                            .newsId(detail.getNewsId())
                            .orgId(detail.getOrgId())
                            .source(detail.getSource())
                            .summary(detail.getSummary())
                            .tags(detail.getTags())
                            .title(detail.getTitle())
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }

    public News changeNewDTO(Long orgId, NewsDTO newsDTO) {
        return News.newBuilder()
                .setNewsPath(newsDTO.getNewsPath())
                .setNewsId(newsDTO.getNewsId() == null ? 0L : newsDTO.getNewsId())
                .setOrgId(orgId)
                .setId(newsDTO.getId() == null ? 0L : newsDTO.getId())
                .setStatus(newsDTO.getStatus() == null ? 0 : newsDTO.getStatus())
                .setVersion(newsDTO.getVersion() == null ? 0 : newsDTO.getVersion())
                .setPublished(newsDTO.getPublished() == null ? System.currentTimeMillis(): newsDTO.getPublished())
                .addAllDetails(newsDTO.getDetails().stream().map(newsDetailsDTO -> {
                    return NewsDetail.newBuilder()
                            .setSummary(newsDetailsDTO.getSummary() == null ? "" : newsDetailsDTO.getSummary())
                            .setImages(newsDetailsDTO.getImages() == null ? "" : newsDetailsDTO.getImages())
                            .setTitle(newsDetailsDTO.getTitle() == null ? "" : newsDetailsDTO.getTitle())
                            .setTags(newsDetailsDTO.getTags() == null ? "" : newsDetailsDTO.getTags())
                            .setSource(newsDetailsDTO.getSource() == null ? "" : newsDetailsDTO.getSource())
                            .setOrgId(orgId)
                            .setLanguage(newsDetailsDTO.getLanguage() == null ? "" : newsDetailsDTO.getLanguage())
                            .setNewsId(newsDetailsDTO.getNewsId() == null ? 0l : newsDetailsDTO.getNewsId())
                            .setId(newsDetailsDTO.getId() == null ? 0l : newsDetailsDTO.getId())
                            .setContent(newsDetailsDTO.getContent())
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }

    @Override
    public int createNews(Long orgId, NewsDTO newsDTO) {
        CreateNewsRequest request = CreateNewsRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setNews(changeNewDTO(orgId, newsDTO))
                .build();
        CreateNewsResponse response = newsClient.createNews(request);
        return response.getRet();
    }

    @Override
    public int modifyNews(Long orgId, NewsDTO newsDTO) {
        ModifyNewsRequest request = ModifyNewsRequest.newBuilder()
                .setHeader(Header.newBuilder()
                        .setOrgId(orgId)
                        .build())
                .setNews(changeNewDTO(orgId, newsDTO))
                .build();
        ModifyNewsResponse response = newsClient.modifyNews(request);
        return response.getRet();
    }

    @Override
    public int publishNews(Long orgId, Long id) {
        UpdateStatusRequest request = UpdateStatusRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setId(id)
                .setStatus(1)
                .build();
        UpdateStatusResponse response = newsClient.updateStatus(request);
        return response.getRet();
    }

    @Override
    public int closeNews(Long orgId, Long id) {
        UpdateStatusRequest request = UpdateStatusRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setId(id)
                .setStatus(0)
                .build();
        UpdateStatusResponse response = newsClient.updateStatus(request);
        return response.getRet();
    }

    @Override
    public int deleteNews(Long orgId, Long id) {
        UpdateStatusRequest request = UpdateStatusRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setId(id)
                .setStatus(2)
                .build();
        UpdateStatusResponse response = newsClient.updateStatus(request);
        return response.getRet();
    }

    @Override
    public NewsTemplateDTO getTemplate(Long orgId, String name) {
        GetTemplateRequest request = GetTemplateRequest.newBuilder()
                .setName(name)
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .build();
        GetTemplateResponse response = newsClient.getTemplate(request);
        return NewsTemplateDTO.builder()
                .content(response.getContent())
                .orgId(response.getOrgId())
                .updated(response.getUpdated())
                .params(response.getParams())
                .build();
    }

    @Override
    public int setTemplate(Long orgId, String name, String content, String params) {
        SetTemplateRequest request = SetTemplateRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setContent(content)
                .setName(name)
                .setParams(params)
                .build();
        SetTemplateResponse response = newsClient.setTemplate(request);
        return response.getRet();
    }
}
