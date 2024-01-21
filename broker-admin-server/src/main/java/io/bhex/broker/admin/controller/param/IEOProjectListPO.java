package io.bhex.broker.admin.controller.param;

import lombok.Data;

import java.util.Objects;

@Data
public class IEOProjectListPO {

    private Integer pageNo;
    private Integer size;

    public Integer getPageNo(){
        if(Objects.isNull(pageNo)){
            return 1;
        }

        return pageNo;
    }

    public Integer getSize(){
        if(Objects.isNull(size)){
            return 10;
        }

        return size;
    }
}
