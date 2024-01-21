package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FCodeApplyDTO {

    private Long id;

    private Long brokerId;

    private String imgUrl;

    private String email;

    private String nationalCode;

    private String phoneNo;

    private Integer approveStatus;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public interface ApproveStatus{

        int NO_APPROVE = 0;

        int PASS = 1;

        int REJECT = 2;

    }





}
