package com.huiminpay.merchant;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huiminpay.common.cache.util.EncryptUtil;
import com.huiminpay.merchant.api.MerchantService;
import com.huiminpay.merchant.dto.MerchantDTO;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenTemp {

    @Reference
    MerchantService merchantService;

    //生成token，指定商户id
    @Test
    public void createTestToken() {
        //填写用于测试的商户id
        Long merchantId = 123123123123L;

        //根据商户id查询商户信息
        MerchantDTO merchantDTO = merchantService.queryMerchantById(merchantId);

        //生成token
        JSONObject token = new JSONObject();
        token.put("mobile", merchantDTO.getMobile());
        token.put("user_name", merchantDTO.getUsername());
        token.put("merchantId", merchantId);

        String jwt_token = "Bearer " + EncryptUtil.encodeBase64(JSON.toJSONString(token).getBytes());
        System.out.println(jwt_token);

    }
}