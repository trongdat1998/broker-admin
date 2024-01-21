package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;



/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 14:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletePushTaskPo {
    @NotNull
    private Long taskId;
}
