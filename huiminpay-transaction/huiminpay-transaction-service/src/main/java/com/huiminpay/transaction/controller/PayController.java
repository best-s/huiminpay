package com.huiminpay.transaction.controller;

import com.alibaba.fastjson.JSONObject;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.util.EncryptUtil;
import com.huiminpay.common.cache.util.IPUtil;
import com.huiminpay.common.cache.util.ParseURLPairUtil;
import com.huiminpay.merchant.api.AppService;
import com.huiminpay.paymentagent.api.dto.PaymentResponseDTO;
import com.huiminpay.transaction.api.TransactionService;
import com.huiminpay.transaction.api.dto.PayOrderDTO;
import com.huiminpay.transaction.vo.OrderConfirmVO;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class PayController {

    @Reference
    TransactionService transactionService;
    @Reference
    AppService appService;

    // 支付入口
    @GetMapping("/pay-entry/{ticket}")
    public String payEntry(@PathVariable("ticket") String ticket, HttpServletRequest request) {
        // 解码
        String jsonStr = EncryptUtil.decodeUTF8StringBase64(ticket);
        // 转对象
        PayOrderDTO payOrderDTO = JSONObject.parseObject(jsonStr, PayOrderDTO.class);
        payOrderDTO.setChannel("huimin_c2b");

        String params = "";
        try {
            // 将对象的非空属性组装为参数对
            params = ParseURLPairUtil.parseURLPair(payOrderDTO);

            String header = request.getHeader("User-Agent");
            BrowserType browserType = BrowserType.valueOfUserAgent(header);
            switch (browserType) {
                case ALIPAY:
                    return "forward:/pay-page?" + params;

                case WECHAT:
                    // TODO 获取openId

                    return "forward:/pay-page?" + params;

                case MOBILE_BROWSER:

                    return "forward:/pay-page?" + params;
                default:
                    return "pay_error";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("支付宝门店下单")
    @PostMapping("/createAliPayOrder")
    public void createAlipayOrderForStore(OrderConfirmVO orderConfirmVO,
                                          HttpServletRequest request,
                                          HttpServletResponse response){

        PayOrderDTO payOrderDTO = new PayOrderDTO();
//        BeanUtils.copyProperties(orderConfirmVO, payOrderDTO);
        Long merchantId = appService.getApp(orderConfirmVO.getAppId()).getMerchantId();
        //应用id
        payOrderDTO.setAppId(orderConfirmVO.getAppId());
        //门店id
        payOrderDTO.setStoreId(Long.valueOf(orderConfirmVO.getStoreId()));
        //订单金额
        payOrderDTO.setTotalAmount(Integer.valueOf(orderConfirmVO.getTotalAmount()));
        //商户id
        payOrderDTO.setMerchantId(merchantId);
        //商户端ip
        payOrderDTO.setClientIp(IPUtil.getIpAddr(request));
        //订单描述
        payOrderDTO.setBody(orderConfirmVO.getBody());
        //订单标题
        payOrderDTO.setSubject(orderConfirmVO.getSubject());
        //订单号
        payOrderDTO.setChannel(orderConfirmVO.getChannel());

        //保存顶单 调代理服务对接
        PaymentResponseDTO<String> dto = transactionService.submitOrderByAli(payOrderDTO);
        String content = dto.getContent();

        //响应结果
        try {
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(content);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.E_400002);
        }


    }

    //保存订单
    private PayOrderDTO saveOrder(PayOrderDTO payOrderDTO) throws BusinessException {
        //TODO 保存订单
        return payOrderDTO;
    }

}
