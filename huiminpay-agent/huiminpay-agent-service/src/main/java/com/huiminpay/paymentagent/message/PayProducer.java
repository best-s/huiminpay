package com.huiminpay.paymentagent.message;

import com.alibaba.fastjson.JSONObject;
import com.huiminpay.paymentagent.api.conf.AliConfigParam;
import com.huiminpay.paymentagent.api.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class PayProducer {

    @Resource
    RocketMQTemplate rocketMQTemplate;
    private static final String TOPIC_ORDER = "TP_PAYMENT_ORDER";
    private static final String TOPIC_RESULT = "TP_PAYMENT_RESULT";
    //发送查询结果的延迟消息
    public void payOrderNotice(PaymentResponseDTO<AliConfigParam> result){

        Message<PaymentResponseDTO<AliConfigParam>> message = MessageBuilder.withPayload(result).build();
        rocketMQTemplate.syncSend(TOPIC_ORDER, message, 30000,3);
        log.info("【代理服务：发送查询结果的延时消息】:{}", JSONObject.toJSONString(result));

    }

    //发送结果消息
    public void payResultNotice(PaymentResponseDTO<?> result){

        rocketMQTemplate.convertAndSend(TOPIC_RESULT, result);
        log.info("【代理服务：发送结果消息】:{}", JSONObject.toJSONString(result));

    }

}
