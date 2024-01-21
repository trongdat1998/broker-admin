package io.bhex.broker.admin.controller;


import com.alibaba.fastjson.JSON;
import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import io.bhex.base.account.ExchangeReply;
import io.bhex.base.token.TokenCategory;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.CompetitionTopPO;
import io.bhex.broker.admin.controller.param.IdPO;
import io.bhex.broker.admin.controller.param.ListCompetitionPO;
import io.bhex.broker.admin.controller.param.ListParticipantPO;
import io.bhex.broker.admin.grpc.client.impl.OrgClient;
import io.bhex.broker.admin.service.BrokerBasicService;
import io.bhex.broker.admin.service.ContractCompetitionService;
import io.bhex.broker.common.exception.BrokerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/broker/contract/competition")
public class ContractCompetitionController extends BaseController {

    private static final String XLS = ".xls";
    private static final String XLSX = ".xlsx";

    @Resource
    private ContractCompetitionService contractCompetitionService;

    @Resource
    private BrokerBasicService  brokerBasicService;

    @Resource
    private OrgClient orgClient;

    @RequestMapping(value = "/symbol/list")
    public ResultModel<List<SymbolDTO>> listSymbol(){

        try{
            long brokerId=getOrgId();
/*
            ExchangeReply reply=orgClient.findExchangeByBrokerId(brokerId);
            if(Objects.isNull(reply)){
                return ResultModel.error("Hasn't any exchange");
            }*/

            List<SymbolDTO> list=brokerBasicService.listSymbol(brokerId, TokenCategory.FUTURE_CATEGORY);
            //List<SymbolDTO> afterFilter = list.stream().filter(i->i.getExchangeId().equals(reply.getExchangeId())).collect(Collectors.toList());
            return ResultModel.ok(list);
        }catch (BrokerException be){
            return ResultModel.error(be.getMessage());
        }

    }


    @RequestMapping(value = "/list")
    public ResultModel<List<ContractCompetitionAbbrDTO>> listContractCompetition(@RequestBody ListCompetitionPO po){

        try{
            long brokerId=getOrgId();
            List<ContractCompetitionAbbrDTO> list=contractCompetitionService.listCompetition(brokerId,po.getPageNo(),po.getPageSize());
            return ResultModel.ok(list);
        }catch (BrokerException be){
            return ResultModel.error(be.getMessage());
        }
    }


    @RequestMapping(value = "/detail")
    public ResultModel<ContractCompetitionDTO> getContractCompetitionDetail(@RequestBody IdPO idPO){

        try{
            long brokerId=getOrgId();
            ContractCompetitionDTO detail=contractCompetitionService.getDetail(brokerId,idPO.getId());
            return ResultModel.ok(detail);
        }catch (BrokerException be){
            return ResultModel.error(be.getMessage());
        }
    }

    @RequestMapping(value = "/save")
    public ResultModel<Boolean> save(HttpServletRequest request, @RequestBody ContractCompetitionDTO param){

        try{
            long brokerId=getOrgId();
            String domain=parseDomain(request);
            log.info("save competition,domain={}",domain);
            boolean success=contractCompetitionService.save(brokerId,param,domain);
            return ResultModel.ok(success);
        }catch (BrokerException be){
            return ResultModel.error(be.getMessage());
        }
    }

    @RequestMapping(value = "/top")
    public ResultModel<CompetitionRankingListDTO> listTop(@RequestBody CompetitionTopPO param){

        try{
            long brokerId=getOrgId();
            String day= Strings.nullToEmpty(param.getDay());
            int type=param.getTypeSafe();
            long id=param.getId();
            CompetitionRankingListDTO list=contractCompetitionService.listTop(brokerId,id,type,day);
            return ResultModel.ok(list);
        }catch (BrokerException be){
            return ResultModel.error(be.getMessage());
        }
    }

    @RequestMapping(value = "/participant/list")
    public ResultModel<CompetitionParticipantListDTO> listParticipant(@RequestBody ListParticipantPO param){

        try{
            long brokerId=getOrgId();

            long id=param.getCompetitionId();
            int pageSize=param.getPageSize();
            int pageNo=param.getPageNo();

            CompetitionParticipantListDTO dto=contractCompetitionService.listParticipant(brokerId,id,pageNo,pageSize);
            return ResultModel.ok(dto);
        }catch (BrokerException be){
            return ResultModel.error(be.getMessage());
        }
    }

    //下载excel模版
    @RequestMapping(value = "/participant/template")
    public void getTemplate(HttpServletResponse response){

        XSSFWorkbook wb=null;
        try {
            response.setHeader("Content-type","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition","attachment;filename=register.xlsx");
            // 模板导出Excel

            InputStream in=this.getClass().getClassLoader().getResourceAsStream("template/register.xlsx");
            wb=new XSSFWorkbook(in);
            wb.write(response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }finally {
            if(Objects.nonNull(wb)){
                try {
                    wb.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }

    }

    //todo 上传excel
    @RequestMapping(value = "/participant/upload")
    public ResultModel uploadRegister(@RequestParam("uploadFile") MultipartFile file,
                                      @RequestParam(value = "id", required = true, defaultValue = "0") long id){

        long brokerId=getOrgId();
        List<CompetitionParticipantListDTO.CompetitionParticipantDTO> importList= Lists.newArrayList();
        String originalFilename = file.getOriginalFilename();
        Workbook workbook = null;

        List<Integer> errorRows=Lists.newArrayList();
        try {
            if (originalFilename.endsWith(XLS)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (originalFilename.endsWith(XLSX)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            //序号0行为标题
            for (int i = 1; i <= lastRowNum; i++) {
                try{
                    Row row = sheet.getRow(i);
                    CompetitionParticipantListDTO.CompetitionParticipantDTO dto=new CompetitionParticipantListDTO.CompetitionParticipantDTO();
                    if(Objects.nonNull(row.getCell(0))){
                        String uid=excelCellValueToString(row.getCell(0));
                        long userId=Long.parseLong(uid);
                        dto.setUserId(userId);
                    }

                    if(Objects.nonNull(row.getCell(1))){
                        String nickname=excelCellValueToString(row.getCell(1));
                        dto.setNickname(nickname);
                    }

                    if(Objects.nonNull(row.getCell(2))){
                        String whitelistStr=excelCellValueToString(row.getCell(2));
                        boolean whitelist=false;
                        if(StringUtils.isNoneBlank(whitelistStr) && StringUtils.startsWithIgnoreCase(whitelistStr,"y")){
                            whitelist=true;
                        }
                        dto.setWhiteList(whitelist);
                    }

                    if(dto.getUserId()>0){
                        importList.add(dto);
                    }
                }catch (Exception e){
                    log.info("parse excel fail,orgId={},rowId={}",brokerId,i+1);
                    log.error(e.getMessage(),e);
                    errorRows.add(i+1);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            String msg="Error cause by parse excel";
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(errorRows)){
                msg="Invalid cell format,"+ JSON.toJSONString(errorRows);
            }
            return ResultModel.error(-1,msg);

        }finally {
            if(Objects.nonNull(workbook)){
                try {
                    workbook.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }

        if(CollectionUtils.isEmpty(importList)){
            return ResultModel.error(-1,"Empty excel");
        }

        log.info("Prepare import competition participants,total={}",importList.size());
        boolean success=contractCompetitionService.saveParticipant(brokerId,id,importList);
        if(success){
            return ResultModel.ok();
        }

        return ResultModel.error("fail");
    }

    public String excelCellValueToString(Cell cell){

        if(cell.getCellType()== CellType.NUMERIC){
            return new BigDecimal(cell.getNumericCellValue()+"").stripTrailingZeros().toPlainString();
        }

        if(cell.getCellType()== CellType.BOOLEAN){
            return cell.getBooleanCellValue()+"";
        }

        if(cell.getCellType()== CellType.STRING){
            return cell.getStringCellValue().trim();
        }

        return "";

    }

    @RequestMapping(value = "/short_url")
    public ResultModel<CompetitionShortUrlDTO> getShortUrl(HttpServletRequest request, @RequestBody IdPO param){

        try{
            long brokerId=getOrgId();
            String domain=parseDomain(request);
            log.info("getShortUrl,domain={}",domain);
            CompetitionShortUrlDTO dto=contractCompetitionService.getShortUrl(brokerId,param.getId(),domain);
            return ResultModel.ok(dto);
        }catch (BrokerException be){
            return ResultModel.error(be.getMessage());
        }
    }

    public String parseDomain(HttpServletRequest request){
        if(Objects.isNull(request)){
            return "";
        }
        String url=request.getScheme()+"://"+request.getServerName();
        if(request.getServerPort()==80){
            return url;
        }

        return url+":"+request.getServerPort();
    }


}
