package io.bhex.broker.admin.controller.param;

import lombok.Data;

import java.util.List;

@Data
public class EditQuoteTokensPO {
    private List<String> tokens;
}
