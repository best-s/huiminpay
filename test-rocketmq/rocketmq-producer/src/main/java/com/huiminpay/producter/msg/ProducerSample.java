package com.huiminpay.producter.msg;

import com.huiminpay.model.OrderExt;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProducerSample {

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    //发送同步消息
    public void sendSyncMsg(String topic, String msg) {

        rocketMQTemplate.syncSend(topic, msg);

    }

    //发送异步消息
    public void sendAsyncMsg(String topic, String msg) {
        rocketMQTemplate.asyncSend(topic, msg,new SendCallback() {

            //发送成功回调
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult);
            }

            //发送失败回调
            @Override
            public void onException(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }
    //发送自定义消息
    public void sendCustomMsg(String topic, OrderExt orderExt) {
        rocketMQTemplate.convertAndSend(topic, orderExt);
    }
}
