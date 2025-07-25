package com.huiminpay.transaction.api;

import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.paymentagent.api.dto.PaymentResponseDTO;
import com.huiminpay.transaction.api.dto.PayOrderDTO;
import com.huiminpay.transaction.api.dto.QRCodeDto;

public interface TransactionService {

    /**
     * 组装二维码的URL
     * @param qrCodeDto 二维码参数（商户id，应用id，门店id，订单标题，订单描述）
     * @return 二维码URL
     * @throws BusinessException
     */
    String createStoreQRCode(QRCodeDto qrCodeDto) throws BusinessException;


    /**
     * 保存订单   调代理服务对接支付宝
     * @param payOrderDTO 订单信息
     * @return 支付宝相应的数据
     * @throws BusinessException
     */
    PaymentResponseDTO<String> submitOrderByAli(PayOrderDTO payOrderDTO) throws BusinessException;


    /**
     * 更新订单状态
     * @param tradeNo  订单号
     * @param payChannelTradeNo 支付渠道订单号
     * @param tradeState 订单状态
     * @throws BusinessException
     */
    void updatePayOrder(String tradeNo, String payChannelTradeNo, String tradeState)throws BusinessException;
}
