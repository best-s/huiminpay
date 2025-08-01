package com.huiminpay.transaction.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QRCodeDto implements Serializable {
    private Long merchantId;
    private String appId;
    private Long storeId;
    private String subject;//商品标题
    private String body;//订单描述
}