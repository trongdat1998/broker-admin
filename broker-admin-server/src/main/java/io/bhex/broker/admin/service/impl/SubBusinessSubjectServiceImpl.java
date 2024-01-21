package io.bhex.broker.admin.service.impl;

import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.bhex.broker.admin.controller.dto.SubBusinessSubjectDTO;
import io.bhex.broker.admin.controller.param.SubBusinessSubjectPO;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.admin.service.SubBusinessSubjectService;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.sub_business_subject.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(value = "subBusinessSubjectService")
public class SubBusinessSubjectServiceImpl implements SubBusinessSubjectService {

    @Resource
    GrpcClientConfig grpcConfig;

    private SubBusinessSubjectServiceGrpc.SubBusinessSubjectServiceBlockingStub getStub() {
        return grpcConfig.subBusinessSubjectServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public List<SubBusinessSubjectDTO> querySubBusinessSubjectList(Long orgId, Integer parentSubject) {
        QuerySubBusinessSubjectResponse response = getStub().querySubBusinessSubject(QuerySubBusinessSubjectRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setParentSubject(parentSubject)
                .build());
        List<SubBusinessSubject> subjectList = response.getSubBusinessSubjectList();
        return subjectList.stream()
                .map(subject -> {
                    List<SubBusinessSubjectDTO.BusinessName> names = Lists.newArrayList();
                    for (String language : subject.getNamesMap().keySet()) {
                        names.add(SubBusinessSubjectDTO.BusinessName.builder()
                                .language(language)
                                .name(subject.getNamesMap().get(language))
                                .build());
                    }
                    return SubBusinessSubjectDTO.builder()
                            .parentSubject(subject.getParentSubject())
                            .subject(subject.getSubject())
                            .names(names)
                            .build();
                }).sorted(Comparator.comparing(SubBusinessSubjectDTO::getParentSubject)
                        .thenComparing(SubBusinessSubjectDTO::getSubject))
                .collect(Collectors.toList());
    }

    @Override
    public void saveSubBusinessSubject(SubBusinessSubjectPO po) {
        List<SubBusinessSubjectPO.BusinessName> names = po.getNames();
        Map<String, String> nameMap = Maps.newHashMap();
        for (SubBusinessSubjectPO.BusinessName name : names) {
            nameMap.put(name.getLanguage(), Strings.nullToEmpty(name.getName()));
        }
        getStub().replaceSubBusinessSubject(ReplaceSubBusinessSubjectRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(po.getOrgId()).build())
                .setParentSubject(po.getParentSubject())
                .setSubject(po.getSubject())
                .putAllNames(nameMap)
                .build());
    }

}
