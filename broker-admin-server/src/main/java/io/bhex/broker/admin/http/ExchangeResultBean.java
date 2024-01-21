package io.bhex.broker.admin.http;

import lombok.Data;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.http
 * @Author: ming.xu
 * @CreateDate: 03/09/2018 8:21 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
public class ExchangeResultBean<T> {

    private int code;

    private String msg;

    private T data;
}
