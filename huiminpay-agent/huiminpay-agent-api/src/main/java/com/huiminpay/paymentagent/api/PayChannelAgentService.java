package com.huiminpay.paymentagent.api;

import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.paymentagent.api.conf.AliConfigParam;
import com.huiminpay.paymentagent.api.dto.AlipayBean;
import com.huiminpay.paymentagent.api.dto.PaymentResponseDTO;

public interface PayChannelAgentService {

    /**
     * 对接支付宝下单接口
     * @param aliConfigParam 公共参数
     * @param alipayBean 业务参数
     * @return 支付宝下单结果
     * @throws BusinessException
     */
    PaymentResponseDTO<String> createPayOrderByAliWAP(AliConfigParam aliConfigParam, AlipayBean alipayBean) throws BusinessException;

    /**
     * 查询支付宝订单
     * @param aliConfigParam 公共参数
     * @param outTradeNo 订单号
     * @return 支付宝订单信息
     * @throws BusinessException
     */
    PaymentResponseDTO queryPayOrderByAli(AliConfigParam aliConfigParam,String outTradeNo)throws BusinessException;
}
