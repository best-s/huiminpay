package com.huiminpay.merchant.controller;

import com.huiminpay.merchant.common.util.SecurityUtil;
import com.huiminpay.transaction.api.PayChannelService;
import com.huiminpay.transaction.api.dto.PayChannelDTO;
import com.huiminpay.transaction.api.dto.PayChannelParamDTO;
import com.huiminpay.transaction.api.dto.PlatformChannelDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商户平台-渠道和支付参数相关", tags = "商户平台-渠道和支付参数", description = "商户平台-渠道和支付参数相关")
@RestController
public class PlatformParamController {

    @Reference
    PayChannelService payChannelService;

    @ApiOperation(value = "获取服务类型列表")
    @GetMapping("/my/platform-channels")
    public List<PlatformChannelDTO> queryPlatformChannel() {

        return payChannelService.getPlatformChannels();

    }
    @ApiOperation("给应用绑定服务类型")
    @PostMapping("/my/apps/{appId}/platform-channels")
    public void bindPlatformForApp(@PathVariable String appId,
                                   @RequestParam("platformChannelCodes") String platformChannel) {
        payChannelService.bindPlatformChannelForApp(appId, platformChannel);
    }

    @ApiOperation("查询应用和服务类型绑定状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用ID", required = true,dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "platformChannelCodes", value = "服务类型编码", required = true,dataType = "string", paramType = "query")
    })
    @GetMapping("/my/merchants/apps/platformchannels")
    public int queryAppBindPlatform(String appId,String platformChannelCodes){
        return payChannelService.queryAppBindPlatformChannel(appId,platformChannelCodes);
    }
    @ApiOperation("根据服务类型查询支付渠道列表")
    @GetMapping("/my/pay-channels/platform-channels/{platformChannelCode}")
    public List<PayChannelDTO> queryPayChannelByPlatformChannel(@PathVariable("platformChannelCode") String platformChannel) {

        return payChannelService.queryPayChannelsByPlatformChannel(platformChannel);

    }

    @ApiOperation("配置支付参数")
    @RequestMapping(value = "/my/pay-channel-params", method = {RequestMethod.POST, RequestMethod.PUT})
    public void createPayChannelParam(@RequestBody PayChannelParamDTO payChannelDTO){

        Long merchantId = SecurityUtil.getMerchantId();
        payChannelDTO.setMerchantId(merchantId);
        payChannelService.savePayChannelParam(payChannelDTO);

    }

    @ApiOperation("查询支付参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "platformChannel", value = "服务类型", required = true, dataType = "String", paramType = "path")})
    @GetMapping(value = "/my/pay-channel-params/apps/{appId}/platform-channels/{platformChannel}")
    public List<PayChannelParamDTO> queryPayChannelParams(@PathVariable String appId,
                                                          @PathVariable String platformChannel){

        return payChannelService.queryPayChannelParamByAppAndPlatform(appId,platformChannel);

    }

    @ApiOperation("查询某支付参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId",value = "应用ID",dataType = "String",paramType = "path"),
            @ApiImplicitParam(name = "platformChannel",value = "服务类型code",dataType = "String",paramType = "path"),
            @ApiImplicitParam(name = "payChannel",value = "支付渠道code",dataType = "String",paramType = "path")
    })
    @GetMapping("/my/pay-channel-params/apps/{appId}/platform-channels/{platformChannel}/pay-channels/{payChannel}")
    public PayChannelParamDTO queryPayChannelParam(@PathVariable String appId,
                                                   @PathVariable String platformChannel,
                                                   @PathVariable String payChannel){
        return payChannelService.queryParamByAppPlatformAndPayChannel(appId, platformChannel, payChannel);
    }

}
