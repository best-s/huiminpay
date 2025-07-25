package com.huiminpay.merchant.service.Impl;

import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.merchant.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${sms.url}")
    private String BaseUrl;
    @Value("${sms.effectiveTime}")
    private String effectiveTime;

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return key
     */
    @Override
    public String sendSms(String phone) {
        //url
        String url = BaseUrl+"/generate?name=sms&effectiveTime="+effectiveTime;

        //body参数
        Map<String, Object> body = new HashMap<>();
        body.put("mobile", phone);

        //hades请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构造请求参数
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        // 发送请求
        ResponseEntity<Map> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        } catch (RestClientException e) {
            throw new BusinessException(CommonErrorCode.E_100107);
        }

        //取数据
        Map map = responseEntity.getBody();

        if (map == null || map.get("result") == null){
            throw new BusinessException(CommonErrorCode.E_100107);
        }
        Map result = (Map) map.get("result");
        return (String) result.get("key");

    }

    /**
     * 验证短信验证码
     * @param verifyCode 验证码
     * @param verifyKey 验证码key
     */
    @Override
    public void verifySms(String verifyCode, String verifyKey) {
        //url
        String url = BaseUrl+"/verify?name=sms&verificationCode="+verifyCode+"&verificationKey="+verifyKey;
        // 发送请求
        ResponseEntity<Map> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
        } catch (RestClientException e) {
            throw new BusinessException(CommonErrorCode.E_100102);
        }
        //取数据
        Map map = responseEntity.getBody();

        if (map == null || map.get("result") == null || !(Boolean)map.get("result")){
            throw new BusinessException(CommonErrorCode.E_100102);
        }

    }
}
