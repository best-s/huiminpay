package com.huiminpay.merchant.controller;

import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.domain.PageVO;
import com.huiminpay.common.cache.util.QRCodeUtil;
import com.huiminpay.merchant.api.MerchantService;
import com.huiminpay.merchant.common.util.SecurityUtil;
import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.dto.StoreDTO;
import com.huiminpay.transaction.api.TransactionService;
import com.huiminpay.transaction.api.dto.QRCodeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Api(tags = "门店管理接口")
@RestController
public class StoreController {
    @Reference
    private MerchantService merchantService;

    @Reference
    private TransactionService transactionService;

    @Value("${huiminpay.c2b.subject}")
    String subject;

    @Value("${huiminpay.c2b.body}")
    String body;

    @ApiOperation("分页查询门店列表")
    @PostMapping("/my/stores/merchants/page")
    public PageVO<StoreDTO> queryStoreByPage(Integer pageNo, Integer pageSize) {
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(SecurityUtil.getMerchantId());
        return merchantService.queryStoreByPage(pageNo, pageSize, storeDTO);
    }

    @ApiOperation("生成门店二维码")
    @GetMapping("/my/apps/{appId}/stores/{storeId}/app-store-qrcode")
    public String createCScanBStoreQRCode(@PathVariable String appId, @PathVariable Long storeId) {
        // 解析商户ID
        Long merchantId = SecurityUtil.getMerchantId();
        MerchantDTO merchantDto = merchantService.queryMerchantById(merchantId);
        String merchantName = merchantDto.getMerchantName();
        QRCodeDto qrCodeDto = new QRCodeDto();
        qrCodeDto.setMerchantId(merchantId);
        qrCodeDto.setAppId(appId);
        qrCodeDto.setStoreId(storeId);
        // "xxx的商品"
        qrCodeDto.setSubject(String.format(subject, merchantName));
        // "向xxx付款"
        qrCodeDto.setBody(String.format(body, merchantName));
        // 调交易服务
        String url = transactionService.createStoreQRCode(qrCodeDto);
        try {
            // 生成二维码
            return QRCodeUtil.createQRCode(url, 200, 200);
        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.E_200007);
        }
    }
}
