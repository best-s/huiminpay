package com.huiminpay.paymentagent.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.paymentagent.api.PayChannelAgentService;
import com.huiminpay.paymentagent.api.conf.AliConfigParam;
import com.huiminpay.paymentagent.api.dto.AlipayBean;
import com.huiminpay.paymentagent.api.dto.PaymentResponseDTO;
import com.huiminpay.paymentagent.api.dto.TradeStatus;
import com.huiminpay.paymentagent.constans.AliCodeConstants;
import com.huiminpay.paymentagent.message.PayProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Slf4j
public class PayChannelAgentServiceImpl implements PayChannelAgentService {

    @Autowired
    PayProducer payProducer;

    /**
     * 对接支付宝下单接口
     *
     * @param aliConfigParam 公共参数
     * @param alipayBean     业务参数
     * @return 支付宝下单结果
     * @throws BusinessException
     */
    @Override
    public PaymentResponseDTO<String> createPayOrderByAliWAP(AliConfigParam aliConfigParam, AlipayBean alipayBean) throws BusinessException {

        // 支付宝网关地址
//        String serverUrl = aliConfigParam.getUrl();
        String serverUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
        // 应用ID
        String appId = aliConfigParam.getAppId();
        // 应用私钥
        String privateKey = aliConfigParam.getRsaPrivateKey();
        // 格式化方式
        String format = aliConfigParam.getFormat();
        // 编码方式
        String charset = aliConfigParam.getCharest();
        // 支付宝公钥
        String alipayPublicKey = aliConfigParam.getAlipayPublicKey();
        // 签名类型
        String signType = aliConfigParam.getSigntype();
        // AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        // 支付的请求对象
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        // 回调地址
        alipayRequest.setReturnUrl(aliConfigParam.getReturnUrl());
        // 通知地址
        alipayRequest.setNotifyUrl(aliConfigParam.getNotifyUrl());
        // 业务参数
        JSONObject jsonObject = new JSONObject();
        // 订单标题
        jsonObject.put("subject", alipayBean.getSubject());
        // 订单号
        jsonObject.put("out_trade_no", alipayBean.getOutTradeNo());
        // 总金额
        jsonObject.put("total_amount", alipayBean.getTotalAmount());
        // 产品码
        jsonObject.put("product_code", alipayBean.getProductCode());
        // 订单超时时间
        jsonObject.put("timeout_express", alipayBean.getExpireTime());
        alipayRequest.setBizContent(jsonObject.toString());
        try {
            // 调支付宝下单接口
            AlipayTradeWapPayResponse payResponse = alipayClient.pageExecute(alipayRequest);
            // 取出支付宝响应的表单
            String form = payResponse.getBody();
            PaymentResponseDTO<String> responseDTO = new PaymentResponseDTO<>();
            responseDTO.setContent(form);

            //组装消息体
            PaymentResponseDTO<AliConfigParam> dto = new PaymentResponseDTO<>();
            //公共参数
            dto.setContent(aliConfigParam);
            //商户订单号
            dto.setOutTradeNo(alipayBean.getOutTradeNo());
            //支付渠道
            dto.setMsg("ALIPAY_WAP");

            //发送查询支付结果的延迟消息
            payProducer.payOrderNotice(dto);

            return responseDTO;
        } catch (AlipayApiException e) {
            throw new BusinessException(CommonErrorCode.E_400002);
        }

    }

    /**
     * 查询支付宝订单
     *
     * @param aliConfigParam 公共参数
     * @param outTradeNo     订单号
     * @return 支付宝订单信息
     * @throws BusinessException
     */
    @Override
    public PaymentResponseDTO queryPayOrderByAli(AliConfigParam aliConfigParam,
                                                 String outTradeNo) throws BusinessException {

        // 支付宝网关地址
//        String serverUrl = aliConfigParam.getUrl();
        String serverUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
        // 应用ID
        String appId = aliConfigParam.getAppId();
        // 应用私钥
        String privateKey = aliConfigParam.getRsaPrivateKey();
        // 格式化方式
        String format = aliConfigParam.getFormat();
        // 编码方式
        String charset = aliConfigParam.getCharest();
        // 支付宝公钥
        String alipayPublicKey = aliConfigParam.getAlipayPublicKey();
        // 签名类型
        String signType = aliConfigParam.getSigntype();
        // AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        // 支付的请求对象

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        // 业务参数

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", outTradeNo);
        request.setBizContent(jsonObject.toString());

        try {
            // 调支付宝查询接口
            AlipayTradeQueryResponse response = alipayClient.execute(request);

            log.info("【代理服务：查询支付结果】:{}", JSONObject.toJSONString(request));


            String code = response.getCode();
            if (AliCodeConstants.SUCCESSCODE.equals(code)) {

                // 取出支付宝响应的状态转为 惠民订单状态
                TradeStatus tradeStatus = this.covertAliTradeStatusTohuiminCode(response.getTradeStatus());

                return PaymentResponseDTO.success(
                        response.getTradeNo(), outTradeNo, tradeStatus,
                        response.getMsg() + "  " + response.getSubMsg());
            } else {
                return PaymentResponseDTO.fail("交易结果查询失败", outTradeNo, TradeStatus.UNKNOWN);
            }

        } catch (AlipayApiException e) {
            throw new BusinessException(CommonErrorCode.E_100104);
        }

    }

    /**
     * 将支付宝查询时订单状态trade_status 转换为 惠民订单状态
     *
     * @param aliTradeStatus 支付宝交易状态
     *                       WAIT_BUYER_PAY（交易创建，等待买家付款）
     *                       TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
     *                       TRADE_SUCCESS（交易支付成功）
     *                       TRADE_FINISHED（交易结束，不可退款）
     * @return
     */
    private TradeStatus covertAliTradeStatusTohuiminCode(String aliTradeStatus) {

        switch (aliTradeStatus) {
            case AliCodeConstants.WAIT_BUYER_PAY:
                return TradeStatus.USERPAYING;
            case AliCodeConstants.TRADE_SUCCESS:
            case AliCodeConstants.TRADE_FINISHED:
                return TradeStatus.SUCCESS;
            case AliCodeConstants.TRADE_CLOSED:
                return TradeStatus.REVOKED;
            default:
                return TradeStatus.FAILED;
        }

    }
}
