package com.huiminpay.merchant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestTemplateTest {

    @Autowired
    private RestTemplate restTemplate;
    @Test
    /**
     * 测试RestTemplate
     */
    public void test() {
        String s = restTemplate.getForObject("https://baidu.com", String.class);
        System.out.println(s);
    }
    /**
     * 测试验证码接口
     */
    @Test
    public void testVerifyCode() {

        //body参数
        Map<String, Object> body = new HashMap<>();
        body.put("mobile", "13811111111");

        //hades请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //url
        String url = "http://localhost:56085/sailing/generate?name=sms&effectiveTime=300";

        // 构造请求参数
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        // 发送请求
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST,entity, Map.class);

        //取数据
        Map map = exchange.getBody();

        if (map!= null && map.get("result") != null){
            Map result = (Map) map.get("result");

            String key =(String) result.get("key");

            System.out.println("key:" + key);

        }else {
            System.out.println("验证码获取失败");
        }

    }

}
