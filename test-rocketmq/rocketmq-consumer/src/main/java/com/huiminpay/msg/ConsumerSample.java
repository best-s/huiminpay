package com.huiminpay.msg;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = "demo-consumer-group", topic = "my-topic")
public class ConsumerSample implements RocketMQListener<MessageExt> {


    //接收并处理消息
    @Override
//    public void onMessage(String s) {
    public void onMessage(MessageExt messageExt) {
//        System.out.println("信息: " + orderExt);
        //获取重试次数
        int reconsumeTimes = messageExt.getReconsumeTimes();
        System.out.println("重试次数: " + reconsumeTimes);



        if (reconsumeTimes >10){
            //超过10次，不再重试
            System.out.println("手动处理消息");
            return;
        }
        //解析消息
        byte[] body = messageExt.getBody();
    }
}
