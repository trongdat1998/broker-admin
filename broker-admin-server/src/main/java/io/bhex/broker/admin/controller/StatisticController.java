package io.bhex.broker.admin.controller;

import com.google.api.client.http.HttpStatusCodes;
import com.google.common.io.Files;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.KycStatisticDTO;
import io.bhex.broker.admin.controller.dto.RegStatisticDTO;
import io.bhex.broker.admin.controller.param.RegStatisticsPO;
import io.bhex.broker.admin.service.StatisticService;
import io.bhex.broker.common.objectstorage.ObjectStorage;
import io.bhex.broker.common.util.CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class StatisticController extends BrokerBaseController {

    @Autowired
    private StatisticService statisticService;

    @Resource
    private ObjectStorage objectStorage;

    @RequestMapping(value = "/statistic/reg_statistics", method = RequestMethod.POST)
    public ResultModel queryRegStatistics(@RequestBody @Valid RegStatisticsPO po) {
        Long startDate = po.getBeginDate();
        Long endDate = po.getEndDate();
        if (startDate == null || startDate == 0) {
            startDate = DateTime.now().plusDays(-7).toDate().getTime();
            endDate = new Date().getTime();
        }
        if ((endDate - startDate) / (3600 * 24 * 1000) > 62) { //最多可选择2个月
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        RegStatisticDTO aggregateRegStatistic = statisticService.queryAggregateRegStatistic(getOrgId());

        List<RegStatisticDTO> dailyStatistics = statisticService.queryDailyRegStatistic(getOrgId(), startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("aggerateStatistic", aggregateRegStatistic);
        result.put("dailyStatistics", dailyStatistics);
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/statistic/kyc_statistics", method = RequestMethod.POST)
    public ResultModel queryKycStatistics(@RequestBody @Valid RegStatisticsPO po) {
        Long startDate = po.getBeginDate();
        Long endDate = po.getEndDate();
        if (startDate == null || startDate == 0) {
            startDate = DateTime.now().plusDays(-7).toDate().getTime();
            endDate = new Date().getTime();
        }
        if ((endDate - startDate) / (3600 * 24 * 1000) > 62) { //最多可选择2个月
            return ResultModel.error(ErrorCode.ERR_REQUEST_PARAMETER.getDesc());
        }
        KycStatisticDTO aggregateRegStatistic = statisticService.queryAggregateKycStatistic(getOrgId());

        List<KycStatisticDTO> dailyStatistics = statisticService.queryDailyKycStatistic(getOrgId(), startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("aggerateStatistic", aggregateRegStatistic);
        result.put("dailyStatistics", dailyStatistics);
        return ResultModel.ok(result);
    }

    @BussinessLogAnnotation(opContent = "Download Data date:{#date}  dataType:{#dataType}")
    @RequestMapping(value = "/statistic/download_data")
    public void downloadData(@RequestParam(value = "type") String dataType,
                             @RequestParam String date,
                             HttpServletResponse response) throws Exception {
        String showFileName = date + "_" + dataType + "_" + getOrgId() + ".xlsx";
        String fileName = "data_" + dataType + "_" + getOrgId() + ".xlsx";
        String fileKey = "data/" + getOrgId() + "/" + date + "/" + fileName;
        if (objectStorage.doesObjectExists(fileKey)) {
            File tmpFile = File.createTempFile("data-file-" + CryptoUtil.getRandomCode(16), ".tmp");
            objectStorage.downloadObject(fileKey, tmpFile);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(showFileName, "UTF-8"));
            response.setContentType(com.google.common.net.MediaType.MICROSOFT_EXCEL.toString());
            Files.copy(tmpFile, response.getOutputStream());
        } else {
            log.warn("statistics download file {} not found", fileKey);
            response.setStatus(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
            response.getWriter().write("Resource not found");
        }
    }
}
