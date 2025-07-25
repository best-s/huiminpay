package com.huiminpay.merchant.controller;

import com.huiminpay.merchant.api.AppService;
import com.huiminpay.merchant.common.util.SecurityUtil;
import com.huiminpay.merchant.dto.AppDTO;
import com.huiminpay.transaction.api.PayChannelService;
import com.huiminpay.transaction.api.dto.PayChannelDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "应用管理层接口")
@RestController
public class AppController {

    @Reference
    AppService appService;


    @ApiOperation("创建应用")
    @PostMapping(value = "my/apps")
    public AppDTO createApp(@RequestBody AppDTO appDTO) {

        Long merchantId = SecurityUtil.getMerchantId();

        return appService.createApp(merchantId, appDTO);

    }

    @ApiOperation("查询商户应用")
    @GetMapping(value = "/my/apps")
    public List<AppDTO> getApps() {

        Long merchantId = SecurityUtil.getMerchantId();
        return appService.getAppList(merchantId);

    }

    @ApiOperation("查询应用详情")
    @GetMapping(value = "/my/apps/{appId}")
    public AppDTO getApp(@PathVariable String appId) {

        return appService.getApp(appId);

    }





}
