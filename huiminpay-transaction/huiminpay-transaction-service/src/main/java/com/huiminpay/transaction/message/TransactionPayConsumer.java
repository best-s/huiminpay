package com.huiminpay.transaction.message;

import com.alibaba.fastjson.JSONObject;
import com.huiminpay.paymentagent.api.dto.PaymentResponseDTO;
import com.huiminpay.paymentagent.api.dto.TradeStatus;
import com.huiminpay.transaction.api.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@RocketMQMessageListener(topic = "TP_PAYMENT_RESULT", consumerGroup = "CID_ORDER_CONSUMER")
@Slf4j
@Component
public class TransactionPayConsumer implements RocketMQListener<MessageExt> {

    @Reference
    TransactionService transactionService;

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String msg = new String(body);

        log.info("【交易服务：消费结果消息】:{}", msg);

        //转对象
        PaymentResponseDTO resultDTO = JSONObject.parseObject(body, PaymentResponseDTO.class);
        //商户订单号
        String outTradeNo = resultDTO.getOutTradeNo();

        //支付渠道交易流水号
        String payChannelTradeNo = resultDTO.getTradeNo();

        //支付状态
        TradeStatus tradeState = resultDTO.getTradeState();

        //更新订单

        switch (tradeState) {
            case SUCCESS:
                transactionService.updatePayOrder(outTradeNo, payChannelTradeNo, "2");
                break;
            case REVOKED:
                transactionService.updatePayOrder(outTradeNo, payChannelTradeNo, "4");
                break;
            case FAILED:
                transactionService.updatePayOrder(outTradeNo, payChannelTradeNo, "5");
                break;
            default:
                throw new RuntimeException(String.format("无法解析的交易结果:%s",tradeState));
        }

    }
}
