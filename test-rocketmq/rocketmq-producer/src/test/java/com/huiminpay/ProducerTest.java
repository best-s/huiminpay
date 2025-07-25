package com.huiminpay;

import com.huiminpay.model.OrderExt;
import com.huiminpay.producter.msg.ProducerSample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerTest {

    @Autowired
    ProducerSample producerSample;
    //发送同步消息
    @Test
    public void testSend() {

        producerSample.sendSyncMsg("my-topic", "Hello,RocketMQ①");

    }
    //发送异步消息
    @Test
    public void asyncSend() {
        producerSample.sendAsyncMsg("my-topic", "Hello,RocketMQ②");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //发送自定义消息
    @Test
    public void sendCustomMsg() {

        OrderExt orderExt = new OrderExt();

        orderExt.setId("1");
        orderExt.setMoney(10000L);
        orderExt.setTitle("测试消息");
        orderExt.setCreateTime(new Date());

        producerSample.sendCustomMsg("my-topic", orderExt);

    }

}
