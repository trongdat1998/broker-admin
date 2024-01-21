package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.constant.AdminTokenTypeEnum;
import io.bhex.base.token.TokenCategory;
import io.bhex.base.token.TokenDetail;
import io.bhex.bhop.common.controller.BaseController;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.bhop.common.util.validation.ValidUtil;
import io.bhex.broker.admin.controller.dto.SimpleTokenDTO;
import io.bhex.broker.admin.controller.dto.TokenApplyRecordDTO;
import io.bhex.broker.admin.controller.param.TokenApplyPO;
import io.bhex.broker.admin.controller.param.TokenPO;
import io.bhex.broker.admin.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Date: 2020/9/10 下午4:43
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/token")
public class TokenApplyController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/check_token_existed", method = RequestMethod.POST)
    public ResultModel getTokenAvailable(@RequestBody @Validated TokenPO po, AdminUserReply adminUser) {
        String token = po.getToken().toUpperCase();
        TokenDetail tokenDetail = tokenService.getTokenFromBh(token, adminUser.getOrgId());
        if (!tokenDetail.getTokenId().equalsIgnoreCase("")) {
            log.info("TokenId:{} existed in platform", token);
            throw new BizException(ErrorCode.TOKEN_ALREADY_EXIST);
        }
        if (tokenNameExisted(adminUser.getOrgId(), token)) {
            log.info("TokenName:{} existed in Broker", token);
            throw new BizException(ErrorCode.TOKEN_ALREADY_EXIST);
        }
        return ResultModel.ok();
    }

    private boolean tokenNameExisted(long orgId, String tokenName) {
        List<SimpleTokenDTO> list = tokenService.querySimpleTokens(orgId, TokenCategory.MAIN_CATEGORY_VALUE);
        for (SimpleTokenDTO tokenDTO : list) {
            if (tokenDTO.getTokenName().equals(tokenName)) {
                log.info("TokenName:{} existed in Broker", tokenName);
                return true;
            }
        }
        return false;
    }

    @PostMapping("/token_apply")
    public ResultModel applyToken(@RequestBody @Validated TokenApplyPO tokenRecordPO, AdminUserReply adminUser) {
        String tokenName = tokenRecordPO.getTokenName().toUpperCase();
        if (!ValidUtil.isTokenName(tokenName)) {
            return ResultModel.error("tokenName error");
        }
        if ((tokenRecordPO.getId() == null || tokenRecordPO.getId() == 0)
                && tokenNameExisted(adminUser.getOrgId(), tokenName)) {
            log.info("TokenName:{} existed in Broker", tokenName);
            throw new BizException(ErrorCode.TOKEN_ALREADY_EXIST);
        }

        AdminTokenTypeEnum tokenTypeEnum = AdminTokenTypeEnum.getByType(tokenRecordPO.getTokenType());
        System.out.printf("[QuyenTa] - Request token type %s", tokenTypeEnum);
        tokenRecordPO.setTokenType(tokenTypeEnum.getType());

        if (tokenRecordPO.getId() != null && tokenRecordPO.getId() > 0) {
            TokenApplyRecordDTO existedRecord = tokenService.getApplyRecordById(adminUser.getOrgId(), tokenRecordPO.getId());
            if (existedRecord == null || existedRecord.getId() == 0) {
                return ResultModel.error("error id");
            }
            tokenRecordPO.setTokenId(existedRecord.getTokenId());
        } else {
            tokenRecordPO.setTokenId(StringUtils.upperCase(StringUtils.trim(tokenRecordPO.getTokenName())));
        }
        tokenRecordPO.setTokenName(StringUtils.upperCase(StringUtils.trim(tokenRecordPO.getTokenName())));
        tokenRecordPO.setTokenFullName(StringUtils.trim(tokenRecordPO.getTokenFullName()));

        tokenRecordPO.setIconUrl(StringUtils.trim(tokenRecordPO.getIconUrl()));
        if (!AdminTokenTypeEnum.CHAIN.equals(tokenTypeEnum) && StringUtils.isEmpty(tokenRecordPO.getContractAddress())) {
            return ResultModel.error("token.record.contractAddress.required");
        } else if (AdminTokenTypeEnum.CHAIN.equals(tokenTypeEnum)) {
            tokenRecordPO.setContractAddress(StringUtils.EMPTY);
        }
        tokenRecordPO.setContractAddress(StringUtils.trim(tokenRecordPO.getContractAddress()));

        return ResultModel.ok(tokenService.applyToken(getOrgId(), tokenRecordPO, tokenTypeEnum));
    }

    @GetMapping("/apply_record")
    public ResultModel<TokenApplyRecordDTO> getApplyRecord(@RequestParam String tokenId, AdminUserReply adminUser) {
        return ResultModel.ok(tokenService.getApplyRecordByTokenId(adminUser.getOrgId(), tokenId));
    }

    @GetMapping("/apply_list")
    public ResultModel<PaginationVO<TokenApplyRecordDTO>> listApplyRecords(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "30") Integer pageSize,
            @RequestParam(required = false, defaultValue = "-1") Integer tokenType, AdminUserReply adminUser) {
        if (tokenType != -1) {
            AdminTokenTypeEnum tokenTypeEnum = AdminTokenTypeEnum.getByType(tokenType);
            tokenType = tokenTypeEnum.getType();
        }
        return ResultModel.ok(tokenService.listTokenApplyRecords(adminUser.getOrgId(), tokenType, current, pageSize));
    }

}
