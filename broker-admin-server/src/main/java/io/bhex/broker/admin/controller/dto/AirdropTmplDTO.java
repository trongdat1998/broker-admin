package io.bhex.broker.admin.controller.dto;

import lombok.Data;

import java.util.Comparator;


/**
 * @Description:
 * @Date: 2019/8/27 下午3:20
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
public class AirdropTmplDTO implements Comparator<AirdropTmplDTO> {

    public static final String UID_COLUMN = "UID";
    public static final String TOKEN_COLUMN = "Token";
    public static final String QUANTITY_COLUMN = "Num";
    public static final String HAVE_TOKEN_COLUMN = "持有币种";
    public static final String HAVE_QUANTITY_COLUMN = "持有数量";
    public static final String LINE_ID_COLUMN = "ID";

    private Integer tmplLineId; //模板中的行号
    private String userId;
    private String airdropTokenId; //空投币种
    private String airdropTokenNum; //空投币种的数量

    private String haveTokenId; //空投比例需要用户持有的币种
    private String haveTokenNum; //用户持有设定币的数量

    @Override
    public int compare(AirdropTmplDTO o1, AirdropTmplDTO o2) {
        return o1.getAirdropTokenId().compareTo(o2.getAirdropTokenId());
    }
}
