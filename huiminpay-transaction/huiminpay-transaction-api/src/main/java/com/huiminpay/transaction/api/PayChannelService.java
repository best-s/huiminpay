package com.huiminpay.transaction.api;

import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.transaction.api.dto.PayChannelDTO;
import com.huiminpay.transaction.api.dto.PayChannelParamDTO;
import com.huiminpay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

public interface PayChannelService {

    /**
     * 获取平台服务类型列表
     * @return 平台服务类型列表
     * @throws BusinessException
     */
    List<PlatformChannelDTO> getPlatformChannels()throws BusinessException;

    /**
     * 绑定平台服务类型
     * @param appId 应用ID
     * @param platformChannelCode 平台服务类型编码
     * @throws BusinessException
     */
    void bindPlatformChannelForApp(String appId, String platformChannelCode) throws BusinessException;

    /**
     *  查询应用和服务类型绑定状态
     * @param appId 应用ID
     * @param platformChannelCode 平台服务类型编码
     * @return 绑定状态 0:未绑定 1:已绑定
     * @throws BusinessException
     */
    int queryAppBindPlatformChannel(String appId, String platformChannelCode) throws BusinessException;

    /**
     * 查询平台服务类型下的支付渠道列表
     * @param platformChannelCode 平台服务类型编码
     * @return 支付渠道列表
     * @throws BusinessException
     */
    List<PayChannelDTO> queryPayChannelsByPlatformChannel(String platformChannelCode) throws BusinessException;

    /**
     * 保存支付参数
     * @param payChannelDTO 支付参数
     * @throws BusinessException
     */
    void savePayChannelParam(PayChannelParamDTO payChannelDTO) throws BusinessException;

    /**
     * 根据应用和服务类型查询支付渠道参数列表     结果可能是多个(支付宝param 微信param)
     * @param appId 应用ID
     * @param platformChannel 平台服务类型编码
     * @return
     * @throws BusinessException
     */
    List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId,String platformChannel) throws BusinessException;


    /**
     * 获取指定应用指定服务类型下所包含的某个原始支付参数
     * @param appId
     * @param platformChannel
     * @param payChannel
     * @return
     * @throws BusinessException
     */
    PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId, String platformChannel,String payChannel) throws BusinessException;

}
