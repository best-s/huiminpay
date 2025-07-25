package com.huiminpay.transaction.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
public class PayTestController {

    @GetMapping("/alipayTest")
    public void alipayTest(HttpServletResponse response) {

        //支付宝网关地址
        String url = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
        //应用ID
        String AppID = "9021000142687690";
        // 应用私钥
        String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCoCoqhjmqm7sS1d0gaJUmk5U0NzQXCzpe0jeomCj0QKPXGofowQiBZo0Er6mDghD3L29EfGGyCMeICznExSHhHceF+JKYxGs/ns8Fx/xShefN10Ys+hLNJs+dd8SpzjUbJF6IKfFluLC1ic7tZfip8/xAqbw/NcFJgPj0c3nym5V+iK9wgQ+QD+yEon5YVwJVIaaKjb9agbOHm0mPhCNGhb7WIlRfNY4693zxc33q5br+nNRvn3wwDk1OF8iHEXvGXJYboHagqD9Zde3qGMnCwvQ1FSR3pkQDJu1ICUKz0DxEQSlTY8JJcqEm6IYpaMajxvlwxabOS/rF4vtjQdXYPAgMBAAECggEAWaJ13ENpZG4Mg3eDAF9PbyPY/DmWjxlAFtF0SqI/fDU9FJ0mJUPAmc/ZXaGB14DyraEtZVbT2obdNf4EpvMvJXz7eRv3gk35BHEACNy1loTEdt41Vj3WnMPX8GZJ7KU5Ut7GUq2NzpTGCLQPBWelJlOvvdLMLRty9V99nb6X4zWtU7ufb85ADPBAAUwZDAEXg4YMJ5HSqTKrfsxVcSah2WmUMaT9RB66gevlfd1TsHI8lZcYjbFrSppB7gIESx+hw5YxwEhvaZRPfibf1wt1NJlrF1lLIajbkxn+RX9dtHCWD/Dl5fQeOK6mF4htnava+Y3yDrZg+axSME4qSV390QKBgQDZC3/PB1uDBY61AruPoHWqBreg1op2KcGSFoegPZBDaC4vayM5w8VGbbEvXuzuvCkqf+N7KVVgbcQQeFUyM82jHSe8WqQQ0rUjnYfpXfpwqDp2PjfZ02lcO9mB4SlRpYQNLCTymjGapke3zrKSuwLwfJElLpVP3VUmk5ZWCzWxmwKBgQDGM3s928bp2ncMdBIztsoJUhZsYhix+f3O9fLTtYsxWiQHiLgahJ3eJzYvBEiCIld8wR7RwliRtI4gacCY0mcPgPpW7rWZQoANfnOATMuaJ3tuJQQYdp1NB3aBWgNCf2aDaU1Ac0CVsc0fHzBreNt/FBSgeR+VyM9YZAGEuMw+nQKBgGxwIVfnHiJ02Vq4VcspaQAcwQu7hIwKyqpZVUOK3pO+7ixEw+GtXNeWIEtBivCv04VnRdkXyo8V6DouVKpzVU8kVuD1DDlXfKahovDGQkSc2DCRX0gWJvKs+Du6qfEf17zZDFClFxlj8Dr2KyPCCXLrWX0RLAot08glPpwYl6f3AoGAcPZWf2MLc603vLlKxn6fl8BZV1xxhr5ckeBLja4irvYZItIhW7vJ3hurHhDq4oCALHJFBngl0qV9e6vKQU9pvcRc6b8TcU4dfZNccPtT74AhMR2Xv/manZRrTj3nLbc+zhbuGJWXcbnslphIGHjBYSQovW1LOytAQbh9xQ8uGWECgYBw/5hRZ+ew11VGzkZe2mpnmMCK9+Bi9h+kW8CVV6YueMMxShyhBKu7IvJXHQtEtXYzaKppYZb/v4CQGlww39vXnrInUHfPaZ/TdcxXADWXzcqrL5IBjtBp+mmg+C3U/yaH3bwxiKV3rVCwEFJmqFZN9iU8wigUR7rlvgpkpUWFjA==";
        // 支付宝公钥
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg/YARa0Y/XHt2ol6HtEIkRKVN9s9HZbJN021iNgw7/5I3rKywxbr55BMceix9uEX0FurLZrmUAZSPL5MJMmC/PPgZZf4Gt2lTuQWPUah+QBgZVV2x4adaNC6DZfQQVEc5eUf3AZY5LZdR2tpA0XdUY1i1fqmw/H9VxQEVxCyqvE1mpw9Oq2L/bnRS2lxu7AiQopXOE36a3fV3D5p5FbHp2uIAovXWhYM0IwmBPZq2+bnnWD4mbCxm4ori5At1tSxhuacax3ZTPUafdel4cPgjEuCzdmnqegdP3IJkCtdjWLXDnQzUtIk1gAIc9+plgZhAWS2PHF1hNrFaOvXQ8qVjQIDAQAB";
        // 签名方式
        String signType = "RSA2";
        // 编码格式
        String charset = "utf-8";
        //格式化方式
        String format = "json";
        //AliPayClient
        AlipayClient client = new DefaultAlipayClient(url, AppID, privateKey, format, charset, publicKey, signType);
        //支付宝请求对象
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        //回调地址
        request.setReturnUrl("");
        request.setNotifyUrl("");
        //业务参数
        JSONObject jsonObject = new JSONObject();
        //订单标题
        jsonObject.put("subject", "小米");
        //订单描述
        jsonObject.put("body", "测试订单描述");
        //订单号
        jsonObject.put("out_trade_no", "2021031611");
        //订单金额
        jsonObject.put("total_amount", "0.01");
        //产品编号
        jsonObject.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //请求参数
        request.setBizContent(jsonObject.toString());

        try {
            //请求支付宝接口
            AlipayTradeWapPayResponse payResponse = client.pageExecute(request);

            String body = payResponse.getBody();

            //写回到客户端
            response.setContentType("text/html;charset=" + charset);
            response.getWriter().write(body); // 这里是页面跳转，将body写回客户端，用户点击支付后，页面跳转到支付宝支付页面
            response.getWriter().flush();
            response.getWriter().close();

        }catch (AlipayApiException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
