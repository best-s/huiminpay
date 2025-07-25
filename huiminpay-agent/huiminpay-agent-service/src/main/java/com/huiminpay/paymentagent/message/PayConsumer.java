package com.huiminpay.paymentagent.message;

import com.alibaba.fastjson.JSONObject;
import com.huiminpay.paymentagent.api.PayChannelAgentService;
import com.huiminpay.paymentagent.api.conf.AliConfigParam;
import com.huiminpay.paymentagent.api.dto.PaymentResponseDTO;
import com.huiminpay.paymentagent.api.dto.TradeStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(topic = "TP_PAYMENT_ORDER", consumerGroup = "CID_PAYMENT_CONSUMER")
public class PayConsumer implements RocketMQListener<MessageExt> {

    @Reference
    PayChannelAgentService agentService;
    @Autowired
    PayProducer payProducer;

    @Override
    public void onMessage(MessageExt messageExt) {
        // 获取重试次数
        int times = messageExt.getReconsumeTimes();
        if (times > 0) {
            System.out.println("第 " + times + " 次重试");
        }

        if (times > 2) {

            System.out.println("消息处理完成");
            return;
        }

        // 取消息
        byte[] bys = messageExt.getBody();
        String msg = new String(bys);
        log.info("【代理服务-消费查询结果的延迟消息】:{}", msg);

        // 转对象
        PaymentResponseDTO msgDto = JSONObject.parseObject(msg, PaymentResponseDTO.class);

        // 公共参数
        AliConfigParam aliConfigParam = JSONObject.parseObject(msgDto.getContent().toString(), AliConfigParam.class);

        // 商户订单号
        String outTradeNo = msgDto.getOutTradeNo();

        // 支付渠道
        String payChannel = msgDto.getMsg();

        // 根据参数去对应的支付渠道查询支付结果
        PaymentResponseDTO<?> result = new PaymentResponseDTO<>();

        if ("ALIPAY_WAP".equals(payChannel)) {
            // 调支付宝接口
            result = agentService.queryPayOrderByAli(aliConfigParam, outTradeNo);
        } else if ("WX_JSAPI".equals(payChannel)) {
            // 调微信查询接口

            // result = agentService.queryPayOrderByWechat(aliConfigParam, outTradeNo);
        } else if ("XXX".equals(payChannel)) {
            // 调其他查询接口

            // result = agentService.queryPayOrderByXxx(aliConfigParam, outTradeNo);
        }

        // 未查到支付结果，进行消费重试
        if (result.getTradeState().equals(TradeStatus.UNKNOWN)
                || result.getTradeState().equals(TradeStatus.USERPAYING)) {
            throw new RuntimeException("交易结果未知，等待消费重试...");
        }
        // 发送结果消息
        payProducer.payResultNotice(result);
    }

}