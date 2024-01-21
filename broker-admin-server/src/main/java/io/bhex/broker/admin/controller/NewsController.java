package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.NewsDTO;
import io.bhex.broker.admin.controller.dto.NewsTemplateDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/news")
public class NewsController extends BrokerBaseController {


    @Resource
    NewsService newsService;

    @RequestMapping(value ="/query")
    public ResultModel<List<NewsDTO>> queryNews(@Valid NewsQueryPO queryPO) {
        Long orgId = getOrgId();
        List<NewsDTO> list = newsService.queryNews(orgId, queryPO.getLastId(), queryPO.getPageSize());
        return ResultModel.ok(list);
    }

    @RequestMapping(value ="/create", method = RequestMethod.POST)
    public ResultModel<Integer> createNews(@RequestBody @Valid NewsCreatePO createPO) {
        Long orgId = getOrgId();
        Integer ret = newsService.createNews(orgId, NewsDTO.builder()
                .newsPath(StringUtils.isBlank(createPO.getNewsPath())?"-":createPO.getNewsPath())
                .published(createPO.getPublished())
                .details(createPO.getDetails())
                .build());
        return ResultModel.ok(ret);
    }

    @RequestMapping(value ="/modify", method = RequestMethod.POST)
    public ResultModel<Integer> modifyNews(@RequestBody @Valid NewsModifyPO modifyPO) {
        Long orgId = getOrgId();
        Integer ret = newsService.modifyNews(orgId, NewsDTO.builder()
                .newsPath(StringUtils.isBlank(modifyPO.getNewsPath())?"-":modifyPO.getNewsPath())
                .newsId(modifyPO.getNewsId())
                .published(modifyPO.getPublished())
                .id(modifyPO.getId())
                .details(modifyPO.getDetails())
                .build());
        return ResultModel.ok(ret);
    }

    @RequestMapping(value ="/publish", method = RequestMethod.POST)
    public ResultModel<Integer> publishNews(@RequestBody @Valid NewsStatusPO publishPO) {
        Long orgId = getOrgId();
        Integer ret = newsService.publishNews(orgId, publishPO.getId());
        return ResultModel.ok(ret);
    }

    @RequestMapping(value ="/close", method = RequestMethod.POST)
    public ResultModel<Integer> closeNews(@RequestBody @Valid NewsStatusPO publishPO) {
        Long orgId = getOrgId();
        Integer ret = newsService.closeNews(orgId, publishPO.getId());
        return ResultModel.ok(ret);
    }

    @RequestMapping(value ="/delete", method = RequestMethod.POST)
    public ResultModel<Integer> deleteNews(@RequestBody @Valid NewsStatusPO publishPO) {
        Long orgId = getOrgId();
        Integer ret = newsService.deleteNews(orgId, publishPO.getId());
        return ResultModel.ok(ret);
    }



    @RequestMapping(value ="/get_template")
    public ResultModel<NewsTemplateDTO> getTemplate(@Valid NewsGetTemplatePO getTemplatePO) {
        Long orgId = getOrgId();
        NewsTemplateDTO ret = newsService.getTemplate(orgId, getTemplatePO.getName());
        return ResultModel.ok(ret);
    }

    @RequestMapping(value ="/set_template", method = RequestMethod.POST)
    public ResultModel<Integer> setTemplate(@RequestBody @Valid NewsSetTemplatePO setTemplatePO) {
        Long orgId = getOrgId();
        Integer ret = newsService.setTemplate(orgId, setTemplatePO.getName(), setTemplatePO.getContent(), setTemplatePO.getParams());
        return ResultModel.ok(ret);
    }


}
