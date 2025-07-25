package com.huiminpay.merchant.api;

import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.merchant.dto.AppDTO;

import java.util.List;

public interface AppService {

    /**
     * 创建应用
     * @param merchantId 商户ID
     * @param appDTO 应用信息
     * @return 创建的应用信息
     */
    AppDTO createApp(Long merchantId, AppDTO appDTO)throws BusinessException;

    /**
     * 获取商户应用列表
     * @param merchantId 商户ID
     * @return 该商户的应用列表
     * @throws BusinessException
     */
    List<AppDTO> getAppList(Long merchantId) throws BusinessException;


    /**
     * 获取应用详情
     * @param appId 应用ID
     * @return 应用详情
     * @throws BusinessException
     */
    AppDTO getApp(String appId) throws BusinessException;

    /**
     * 查询应用是否属于该商户
     * @param merchantId 商户ID
     * @param appId 应用ID
     * @return
     * @throws BusinessException
     */
    Boolean queryAppInMerchant(Long merchantId, String appId) throws BusinessException;

}
