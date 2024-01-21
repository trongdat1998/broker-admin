package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.SubBusinessSubjectDTO;
import io.bhex.broker.admin.controller.param.SubBusinessSubjectPO;

import java.util.List;

public interface SubBusinessSubjectService {

    List<SubBusinessSubjectDTO> querySubBusinessSubjectList(Long orgId, Integer parentSubject);

    void saveSubBusinessSubject(SubBusinessSubjectPO po);

}
