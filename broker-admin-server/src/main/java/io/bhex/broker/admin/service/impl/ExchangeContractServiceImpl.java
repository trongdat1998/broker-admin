package io.bhex.broker.admin.service.impl;

import io.bhex.base.admin.AddContractRequest;
import io.bhex.base.admin.ContractDetail;
import io.bhex.base.admin.ListContractReply;
import io.bhex.base.admin.UpdateContactInfoRequest;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.broker.admin.controller.dto.ContractExchangeInfo;
import io.bhex.broker.admin.controller.dto.ExchangeContractDTO;
import io.bhex.broker.admin.controller.param.ExchangeContractPO;
import io.bhex.broker.admin.grpc.client.ExchangeContractClient;
import io.bhex.broker.admin.service.ExchangeContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:14 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class ExchangeContractServiceImpl implements ExchangeContractService {

    private final static Boolean ENABLE_CONTRACT = true;
    private final static Boolean CLOSE_CONTRACT = false;

    @Autowired
    private ExchangeContractClient exchangeContractClient;

    @Override
    public PaginationVO<ExchangeContractDTO> listExchangeContract(Long brokerId, Integer current, Integer pageSize) {
        ListContractReply reply = exchangeContractClient.listExchangeContract(brokerId, current, pageSize);

        PaginationVO<ExchangeContractDTO> vo = new PaginationVO();
        BeanUtils.copyProperties(reply, vo);

        List<ExchangeContractDTO> dtos = new ArrayList<>();
        List<ContractDetail> details = reply.getContractDetailList();
        for(ContractDetail detail : details){
            ExchangeContractDTO dto = new ExchangeContractDTO();
            BeanUtils.copyProperties(detail, dto);
            dto.setExchangeName(detail.getContractOrgName());
            log.info("exchange contract id => " + dto.getContractId());
            dtos.add(dto);
        }
        vo.setList(dtos);
        return vo;
    }

    @Override
    public Boolean reopenExchangeContract(Long brokerId, Long cotractId, Long exchangeId, Long adminUserId) {
        return exchangeContractClient.reopenExchangeContract(brokerId, cotractId);
    }

    @Override
    public Boolean closeExchangeContract(Long brokerId, Long cotractId, Long exchangeId, Long adminUserId) {
        return exchangeContractClient.closeExchangeContract(brokerId, cotractId);
    }

    @Override
    public PaginationVO<ExchangeContractDTO> listApplication(Long brokerId, Integer current, Integer pageSize) {
        ListContractReply reply = exchangeContractClient.listApplication(brokerId, current, pageSize);

        PaginationVO<ExchangeContractDTO> vo = new PaginationVO();
        BeanUtils.copyProperties(reply, vo);

        List<ExchangeContractDTO> dtos = new ArrayList<>();
        List<ContractDetail> details = reply.getContractDetailList();
        for(ContractDetail detail : details){
            ExchangeContractDTO dto = new ExchangeContractDTO();
            BeanUtils.copyProperties(detail, dto);
            dto.setExchangeName(detail.getContractOrgName());
            dtos.add(dto);
        }
        vo.setList(dtos);
        return vo;
    }

    @Override
    public Boolean enableApplication(Long brokerId, Long cotractId, Long exchangeId, Long adminUserId) {
        return exchangeContractClient.enableApplication(brokerId, cotractId);
    }

    @Override
    public Boolean rejectApplication(Long brokerId, Long cotractId, Long exchangeId, Long adminUserId) {
        return exchangeContractClient.rejectApplication(brokerId, cotractId);
    }

    @Override
    public Boolean addApplication(ExchangeContractPO param) {
        AddContractRequest request = AddContractRequest.newBuilder()
                .setBrokerId(param.getBrokerId())
                .setExchangeId(param.getExchangeId())
                .setContractId(param.getContractId())
                .setContractOrgName(param.getExchangeName())
//                .setRemark(param.getRemark())
//                .setEmail(param.getEmail())
//                .setContact(param.getContact())
                .build();

        return exchangeContractClient.addApplication(request);
    }

    @Override
    public Boolean editContactInfo(ExchangeContractPO param) {
        UpdateContactInfoRequest request = UpdateContactInfoRequest.newBuilder()
                .setOrgId(param.getBrokerId())
                .setContractId(param.getContractId())
                .setContractOrgName(param.getExchangeName())
                .setRemark(param.getRemark())
                .setEmail(param.getEmail())
                .setContact(param.getContact())
                .setCompanyName(param.getCompanyName())
                .setPhone(param.getPhone())
                .build();
        log.info(request.toString());
        return exchangeContractClient.editContactInfo(request);
    }

    @Override
    public List<ContractExchangeInfo> listALlExchangeContractInfo(Long brokerId) {
        ListContractReply reply = exchangeContractClient.listAllExchangeContractInfo(brokerId);

        List<ContractExchangeInfo> dtos = new ArrayList<>();
        List<ContractDetail> details = reply.getContractDetailList();
        for(ContractDetail detail : details){
            ContractExchangeInfo dto = new ContractExchangeInfo();
            dto.setExchangeId(detail.getExchangeId());
            dto.setExchangeName(detail.getContractOrgName());
            dto.setRemark(detail.getRemark());
            dtos.add(dto);
        }
        return dtos;
    }
}
