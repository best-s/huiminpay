package com.huiminpay.transaction.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.util.AmountUtil;
import com.huiminpay.common.cache.util.EncryptUtil;
import com.huiminpay.common.cache.util.PaymentUtil;
import com.huiminpay.merchant.api.AppService;
import com.huiminpay.merchant.api.MerchantService;
import com.huiminpay.paymentagent.api.PayChannelAgentService;
import com.huiminpay.paymentagent.api.conf.AliConfigParam;
import com.huiminpay.paymentagent.api.dto.AlipayBean;
import com.huiminpay.paymentagent.api.dto.PaymentResponseDTO;
import com.huiminpay.transaction.api.PayChannelService;
import com.huiminpay.transaction.api.TransactionService;
import com.huiminpay.transaction.api.dto.PayChannelParamDTO;
import com.huiminpay.transaction.api.dto.PayOrderDTO;
import com.huiminpay.transaction.api.dto.QRCodeDto;
import com.huiminpay.transaction.convert.PayOrderConvert;
import com.huiminpay.transaction.entity.PayOrder;
import com.huiminpay.transaction.mapper.PayOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    @Reference
    private MerchantService merchantService;
    @Reference
    private AppService appService;
    @Reference
    PayChannelService payChannelService;
    @Value("${huiminpay.payurl}")
    private String payUrl;

    @Autowired
    private PayOrderMapper payOrderMapper;
    @Reference
    PayChannelAgentService agentService;

    /**
     * 组装二维码的URL
     *
     * @param qrCodeDto 二维码参数（商户id，应用id，门店id，订单标题，订单描述）
     * @return 二维码URL
     * @throws BusinessException
     */

    public String createStoreQRCode(QRCodeDto qrCodeDto) throws BusinessException {

        //校验参数
        if (qrCodeDto == null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        this.check(qrCodeDto.getMerchantId(), qrCodeDto.getStoreId(), qrCodeDto.getAppId());

        //取出参数
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        BeanUtils.copyProperties(qrCodeDto, payOrderDTO);

        //转json后编码
        String jsonString = JSONObject.toJSONString(payOrderDTO);
        String base64 = EncryptUtil.encodeUTF8StringBase64(jsonString);

        //http://localhost:56050/transaction/pay-entry/

        return payUrl + base64;
    }

    /**
     * 保存订单   调代理服务对接支付宝
     *
     * @param payOrderDTO 订单信息
     * @return 支付宝相应的数据
     * @throws BusinessException
     */
    @Override
    public PaymentResponseDTO<String> submitOrderByAli(PayOrderDTO payOrderDTO) throws BusinessException {

        //支付渠道
        payOrderDTO.setPayChannel("ALIPAY_WAP");
        //保存订单
        PayOrderDTO saveOrder = this.saveOrder(payOrderDTO);

        //调代理服务对接支付宝
        return this.aliPayH5(saveOrder);

    }

    /**
     * 更新订单状态
     *
     * @param tradeNo           订单号
     * @param payChannelTradeNo 支付渠道订单号
     * @param tradeState        订单状态
     * @throws BusinessException
     */
    @Override
    public void updatePayOrder(String tradeNo, String payChannelTradeNo, String tradeState) throws BusinessException {

        LambdaUpdateWrapper<PayOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PayOrder::getTradeNo, tradeNo)
                .set(PayOrder::getPayChannelTradeNo, payChannelTradeNo)
                .set(PayOrder::getTradeState, tradeState)
                .set(PayOrder::getUpdateTime, LocalDateTime.now());

        if ("2".equals(tradeState)) {
            wrapper.set(PayOrder::getPaySuccessTime, LocalDateTime.now());
        }

        payOrderMapper.update(null, wrapper);
        log.info("【交易服务：更新订单状态】:{},{},{}",tradeNo,payChannelTradeNo,tradeState );

    }

    //调代理服务对接支付宝
    private PaymentResponseDTO<String> aliPayH5(PayOrderDTO payOrderDTO) throws BusinessException {
        //公共参数
        PayChannelParamDTO paramDTO = payChannelService.queryParamByAppPlatformAndPayChannel(payOrderDTO.getAppId(), payOrderDTO.getChannel(), payOrderDTO.getPayChannel());
        if (paramDTO == null) {
            throw new BusinessException(CommonErrorCode.E_300007);
        }
        //取参数
        String param = paramDTO.getParam();
        AliConfigParam aliConfigParam = JSONObject.parseObject(param, AliConfigParam.class);

        //业务参数
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOutTradeNo(payOrderDTO.getTradeNo());
        alipayBean.setSubject(payOrderDTO.getSubject());
        // 金额分转元
        String f2Y = null;
        try {
            f2Y = AmountUtil.changeF2Y(Long.valueOf(payOrderDTO.getTotalAmount()));
            alipayBean.setTotalAmount(f2Y);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.E_300006);
        }

        alipayBean.setProductCode("QUICK_WAP_WAY");
        alipayBean.setBody(payOrderDTO.getBody());
        alipayBean.setExpireTime("30m");
        alipayBean.setStoreId(payOrderDTO.getStoreId());

        return agentService.createPayOrderByAliWAP(aliConfigParam, alipayBean);

    }

    //保存订单
    private PayOrderDTO saveOrder(PayOrderDTO payOrderDTO) throws BusinessException {
        PayOrder payOrder = PayOrderConvert.INSTANCE.dto2entity(payOrderDTO);
        // 订单号
        payOrder.setTradeNo(PaymentUtil.genUniquePayOrderNo());
        // 币种
        payOrder.setCurrency("CNY");
        // 金额 元转分
        String y2F = AmountUtil.changeY2F(Long.valueOf(payOrderDTO.getTotalAmount()));
        payOrder.setTotalAmount(Integer.parseInt(y2F));
        // 订单状态 0：订单生成
        payOrder.setTradeState("0");
        // 创建时间
        payOrder.setCreateTime(LocalDateTime.now());
        // 过期时间
        payOrder.setExpireTime(LocalDateTime.now().plusMinutes(30));
        payOrderMapper.insert(payOrder);
        return PayOrderConvert.INSTANCE.entity2dto(payOrder);
    }

    //校验参数
    private void check(Long merchantId, Long storeId,String appId){

        Boolean aBoolean = appService.queryAppInMerchant(merchantId, appId);
        if (!aBoolean) {
            throw new BusinessException(CommonErrorCode.E_200005);
        }
        Boolean bBoolean = merchantService.queryStoreInMerchant(merchantId, storeId);
        if (!bBoolean) {
            throw new BusinessException(CommonErrorCode.E_200006);
        }

    }
}
