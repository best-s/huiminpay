package com.huiminpay.merchant.service;

public interface SmsService {

    /**
     * 发送验证码
     * @param phone 手机号
     * @return 验证码key
     */
    String sendSms(String phone);
    /**
     * 校验验证码
     * @param verifyCode 验证码
     * @param verifyKey 验证码key
     */
    void verifySms(String verifyCode, String verifyKey);
}
