package io.bhex.broker.admin.controller.param;


import lombok.Data;

import java.util.Objects;

@Data
public class CompetitionTopPO {

    private String day;
    private Integer type;
    private long id;

    public int getTypeSafe(){
        if(Objects.isNull(this.type)){
            return 0;
        }

        return type.intValue();
    }
}
