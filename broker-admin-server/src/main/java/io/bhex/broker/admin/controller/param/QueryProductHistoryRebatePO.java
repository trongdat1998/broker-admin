package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryProductHistoryRebatePO {

    @NotNull
    private Integer productType;

    private Integer pageNo;

    private Integer size;

    public Integer getPageNo() {
        if (pageNo == null){
            return 1;
        }
        return pageNo;
    }

    /**
     * 默认一百
     * @return
     */
    public Integer getSize() {
        if (size == null){
            return 100;
        }
        return size;
    }
}
