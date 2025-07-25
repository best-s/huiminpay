package com.huiminpay.transaction.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiminpay.common.cache.Cache;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.util.RedisUtil;
import com.huiminpay.common.cache.util.StringUtil;
import com.huiminpay.transaction.api.PayChannelService;
import com.huiminpay.transaction.api.dto.PayChannelDTO;
import com.huiminpay.transaction.api.dto.PayChannelParamDTO;
import com.huiminpay.transaction.api.dto.PlatformChannelDTO;
import com.huiminpay.transaction.convert.PayChannelParamConvert;
import com.huiminpay.transaction.convert.PlatformChannelConvert;
import com.huiminpay.transaction.entity.AppPlatformChannel;
import com.huiminpay.transaction.entity.PayChannelParam;
import com.huiminpay.transaction.entity.PlatformChannel;
import com.huiminpay.transaction.mapper.AppPlatformChannelMapper;
import com.huiminpay.transaction.mapper.PayChannelParamMapper;
import com.huiminpay.transaction.mapper.PlatformChannelMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PayChannelServiceImpl implements PayChannelService {

    @Autowired
    PlatformChannelMapper platformChannelMapper;

    @Autowired
    AppPlatformChannelMapper appPlatformChannelMapper;
    @Autowired
    PayChannelParamMapper payChannelParamMapper;
    @Autowired
    Cache cache;

    /**
     * 获取平台服务类型列表
     * @return 平台服务类型列表
     * @throws BusinessException
     */
    @Override
    public List<PlatformChannelDTO> getPlatformChannels() throws BusinessException {

        // 查询平台服务类型列表
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);

        // 转换为DTO列表
        return PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);

    }

    /**
     * 绑定应用与平台服务类型
     * @param appId 应用ID
     * @param platformChannelCode 平台服务类型编码
     * @throws BusinessException
     */
    @Override
    public void bindPlatformChannelForApp(String appId, String platformChannelCode) throws BusinessException {

        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCode));

        if (appPlatformChannel == null) {
            appPlatformChannel = new AppPlatformChannel();
            appPlatformChannel.setAppId(appId);
            appPlatformChannel.setPlatformChannel(platformChannelCode);
            appPlatformChannelMapper.insert(appPlatformChannel);
        }


    }

    /**
     *  查询应用和服务类型绑定状态
     * @param appId 应用ID
     * @param platformChannelCode 平台服务类型编码
     * @return 绑定状态 1：已绑定 0：未绑定
     * @throws BusinessException
     */
    @Override
    public int queryAppBindPlatformChannel(String appId, String platformChannelCode) throws BusinessException {

        Integer count = appPlatformChannelMapper.selectCount(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCode));

        if (count>0){
            return 1;
        }
        return 0;

    }

    /**
     * 查询平台服务类型下的支付渠道列表
     * @param platformChannelCode 平台服务类型编码
     * @return 支付渠道列表
     * @throws BusinessException
     */
    @Override
    public List<PayChannelDTO> queryPayChannelsByPlatformChannel(String platformChannelCode) throws BusinessException {

        return platformChannelMapper.queryPayChannelByPlatformChannelId(platformChannelCode);

    }

    /**
     * 保存支付参数
     * @param paramDTO 支付参数
     * @throws BusinessException
     */
    @Override
    public void savePayChannelParam(PayChannelParamDTO paramDTO) throws BusinessException {

        // 参数校验
        if (paramDTO == null
                || paramDTO.getMerchantId() == null
                || paramDTO.getAppId() == null
                || paramDTO.getPlatformChannelCode() == null
                || paramDTO.getPayChannel() == null ) {

            throw new BusinessException(CommonErrorCode.E_300009);

        }

        // 查询平台服务类型ID
        Long platformChannelId = queryPlatformChannelId(paramDTO.getAppId(), paramDTO.getPlatformChannelCode());

        // 查询平台服务类型ID
        if (platformChannelId == null){
            throw new BusinessException(CommonErrorCode.E_300010);
        }

        // 查询是否存在
        PayChannelParam payChannelParam = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, platformChannelId)
                .eq(PayChannelParam::getPayChannel, paramDTO.getPayChannel()));

        if (payChannelParam == null){
            // 不存在 新增
            payChannelParam = PayChannelParamConvert.INSTANCE.dto2entity(paramDTO);
            // 设置应用和服务类型绑定关系ID
            payChannelParam.setAppPlatformChannelId(platformChannelId);
            payChannelParamMapper.insert(payChannelParam);
        }else {
            // 存在 更新
            payChannelParam.setChannelName(paramDTO.getChannelName());
            payChannelParam.setParam(paramDTO.getParam());
            payChannelParamMapper.updateById(payChannelParam);

        }
        // 更新缓存
        this.updateCache(paramDTO.getAppId(), paramDTO.getPlatformChannelCode());

    }

    /**
     * 查询应用和服务类型下的支付参数
     * @param appId 应用ID
     * @param platformChannel 平台服务类型编码
     * @return
     * @throws BusinessException
     */

    /**
     * 更新缓存
     * @param appId
     * @param platformChannel
     */
    private void updateCache(String appId, String platformChannel){

        String key = RedisUtil.keyBuilder(appId, platformChannel);

        String s = cache.get(key);

        if (!StringUtil.isEmpty(s)){

            cache.del(key);

        }
        //查数据
        List<PayChannelParamDTO> payChannelParamDTOS = this.queryPayChannelParamByAppAndPlatform(appId, platformChannel);
        //更新缓存
        String toJSONString = JSONObject.toJSONString(payChannelParamDTOS);

        cache.set(key, toJSONString);


    }

    /**
     * 查询平台服务类型ID
     * @param appId 应用ID
     * @param platformChannelCode 平台服务类型编码
     * @return 平台服务类型ID
     * @throws BusinessException
     */
    private Long queryPlatformChannelId(String appId, String platformChannelCode) throws BusinessException {
        // 查询应用和服务类型绑定关系
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCode));

        if (appPlatformChannel != null){

            return appPlatformChannel.getId();

        }

        return null;

    }

    /**
     * 查询某服务类型下的支付参数
     * @param appId 应用ID
     * @param platformChannel 平台服务类型编码
     * @return
     * @throws BusinessException
     */
    @Override
    public List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId, String platformChannel) throws BusinessException {

        //查询缓存
        String key = RedisUtil.keyBuilder(appId, platformChannel);

        String s = cache.get(key);
        //缓存不为空
        if (!StringUtil.isEmpty(s)){

            return JSONObject.parseArray(s, PayChannelParamDTO.class);

        }
        //缓存为空  查询数据库
        // 查询平台服务类型ID
        Long appPlatformChannelId = this.queryPlatformChannelId(appId, platformChannel);

        List<PayChannelParam> payChannelParams = payChannelParamMapper
                .selectList(new LambdaQueryWrapper<PayChannelParam>()
                        .eq(appPlatformChannelId != null,
                                PayChannelParam::getAppPlatformChannelId,
                                appPlatformChannelId));

        List<PayChannelParamDTO> payChannelParamDTOS =
                PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);

        // 更新缓存
        String toJSONString = JSONObject.toJSONString(payChannelParamDTOS);

        cache.set(key, toJSONString);

        return payChannelParamDTOS;

    }

    /**
     * 查询应用和服务类型下的支付参数
     * @param appId
     * @param platformChannel
     * @param payChannel
     * @return
     * @throws BusinessException
     */
    /**
     * 查询某支付渠道参数
     * @param appId           应用ID
     * @param platformChannel 服务类型code
     * @param payChannel      支付渠道code
     * @return 某支付参数
     * @throws BusinessException
     */
    @Override
    public PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId, String platformChannel, String payChannel) throws BusinessException {
        // 查询平台服务类型ID
        List<PayChannelParamDTO> payChannelParamDTOS = this.queryPayChannelParamByAppAndPlatform(appId, platformChannel);
        // 查询参数列表
        for (PayChannelParamDTO paramDTO : payChannelParamDTOS) {
            if (paramDTO.getPayChannel().equals(payChannel)) {
                return paramDTO;
            }
        }
        return null;
    }
}
