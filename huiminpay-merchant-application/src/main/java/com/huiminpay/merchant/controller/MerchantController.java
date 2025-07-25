package com.huiminpay.merchant.controller;

import com.alibaba.nacos.client.utils.StringUtils;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.util.PhoneUtil;
import com.huiminpay.merchant.api.MerchantService;
import com.huiminpay.merchant.common.util.SecurityUtil;
import com.huiminpay.merchant.convert.MerchantDetailConvert;
import com.huiminpay.merchant.convert.MerchantRegisterConvert;
import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.service.FileService;
import com.huiminpay.merchant.service.SmsService;
import com.huiminpay.merchant.vo.MerchantDetailVO;
import com.huiminpay.merchant.vo.MerchantRegisterVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MerchantController {

    @Reference
    private MerchantService merchantService;
    @Autowired
    FileService fileService;

    @Autowired
    SmsService smsService;

    @GetMapping("/merchants/{id}")
    @ApiOperation(value = "根据商户ID查询商户信息", notes = "根据商户ID查询商户信息")
    @ApiImplicitParam(name = "id", value = "商户ID", required = true, dataType = "Long", paramType = "path")
    public MerchantDTO getById(@PathVariable Long id) {

        MerchantDTO merchantDTO = merchantService.queryMerchantById(id);
        return merchantDTO;

    }


    @ApiOperation("获取登录用户的商户信息")
    @GetMapping(value="/my/merchants")
    public MerchantDTO getMyMerchantInfo(){
        // 从token 中获取商户id
        Long merchantId = SecurityUtil.getMerchantId();
        MerchantDTO merchant = merchantService.queryMerchantById(merchantId);
        return merchant;
    }

    @ApiOperation(value = "获取短信验证码", notes = "获取短信验证码")
    @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String", paramType = "query")
    @GetMapping("/sms")
    public String getMSCode(String phone){

        //调用验证码接口
        return smsService.sendSms(phone);

    }

    @ApiOperation(value = "注册商户")
    @ApiImplicitParam(name = "merchantDTO", value = "商户信息", required = true, dataType = "MerchantDTO", paramType = "body")
    @PostMapping("/merchants/register")
    public MerchantRegisterVo merchantRegister(@RequestBody MerchantRegisterVo merchantRegisterVo){

        // 校验参数
        if (merchantRegisterVo == null) {
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        // 手机号非空校验
        if (StringUtils.isBlank(merchantRegisterVo.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        // 校验手机号的合法性
        if (!PhoneUtil.isMatches(merchantRegisterVo.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        // 联系人非空校验
        if (StringUtils.isBlank(merchantRegisterVo.getUsername())) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        // 密码非空校验
        if (StringUtils.isBlank(merchantRegisterVo.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100111);
        }
        // 验证码非空校验
        if (StringUtils.isBlank(merchantRegisterVo.getVerifiyCode())
                || StringUtils.isBlank(merchantRegisterVo.getVerifiykey())) {
            throw new BusinessException(CommonErrorCode.E_100103);
        }

        //校验验证码
        smsService.verifySms(merchantRegisterVo.getVerifiyCode(),merchantRegisterVo.getVerifiykey());

        //转换vo到dto
        MerchantDTO merchantDTO = MerchantRegisterConvert.INSTANCE.vo2dto(merchantRegisterVo);

        merchantService.createMerchant(merchantDTO);

        //返回注册结果
        return MerchantRegisterConvert.INSTANCE.dto2vo(merchantDTO);

    }

    @ApiOperation(value = "上传文件至阿里云OSS")
    @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile", paramType = "form")
    @PostMapping("/upload")
    public String upload(MultipartFile file){

        //调用service层方法上传文件
        return fileService.uploadFile(file);

    }

    @ApiOperation(value ="资质申请")
    @ApiImplicitParam(name = "detailVO", value = "商户资质信息", required = true, dataType = "MerchantDetailVO", paramType = "body")
    @PostMapping("/my/merchants/save")
    public void saveMerchant(@RequestBody MerchantDetailVO detailVO){
        //从token中获取用户信息
        Long merchantId = SecurityUtil.getMerchantId();

        MerchantDTO merchantDto = MerchantDetailConvert.INSTANCE.vo2dto(detailVO);
        merchantService.applyMerchant(merchantId, merchantDto);

    }



}
