package com.huiminpay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.util.RandomUuidUtil;
import com.huiminpay.merchant.api.AppService;
import com.huiminpay.merchant.convert.AppConvert;
import com.huiminpay.merchant.dto.AppDTO;
import com.huiminpay.merchant.entity.App;
import com.huiminpay.merchant.entity.Merchant;
import com.huiminpay.merchant.mapper.AppMapper;
import com.huiminpay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private AppMapper appMapper;

    /**
     * 创建应用
     *
     * @param merchantId 商户ID
     * @param appDTO     应用信息
     * @return
     * @throws BusinessException
     */
    @Override
    public AppDTO createApp(Long merchantId, AppDTO appDTO) throws BusinessException {
        // 参数校验
        if (merchantId == null || appDTO == null) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        // 校验商户是否存在
        if (merchant == null) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        // 校验商户状态
        if (!"2".equals(merchant.getAuditStatus())) {
            throw new BusinessException(CommonErrorCode.E_200003);
        }
        // 校验应用名称是否存在
        if (isExistsAppName(appDTO.getAppName())) {
            throw new BusinessException(CommonErrorCode.E_200004);
        }

        App entity = AppConvert.INSTANCE.dto2entity(appDTO);

        // 设置商户ID
        entity.setMerchantId(merchantId);
        // 设置应用id
        entity.setAppId(RandomUuidUtil.getUUID());

        appMapper.insert(entity);

        return AppConvert.INSTANCE.entity2dto(entity);

    }

    /**
     * 获取应用列表
     * @param merchantId 商户ID
     * @return
     * @throws BusinessException
     */
    @Override
    public List<AppDTO> getAppList(Long merchantId) throws BusinessException {

        List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>().eq(App::getMerchantId, merchantId));

        // 转换成DTO
        return AppConvert.INSTANCE.entityList2dtoList(apps);

    }

    /**
     * 获取应用详情
     * @param appId 应用ID
     * @return 应用详情
     * @throws BusinessException
     */
    @Override
    public AppDTO getApp(String appId) throws BusinessException {

        return AppConvert.INSTANCE.entity2dto
                ((appMapper.selectOne(new LambdaQueryWrapper<App>().eq(App::getAppId, appId))));

    }

    /**
     * 查询应用是否属于该商户
     *
     * @param merchantId 商户ID
     * @param appId      应用ID
     * @return
     * @throws BusinessException
     */
    @Override
    public Boolean queryAppInMerchant(Long merchantId, String appId) throws BusinessException {

        Integer count = appMapper.selectCount(new LambdaQueryWrapper<App>()
                .eq(App::getMerchantId, merchantId)
                .eq(App::getAppId, appId));

        return count > 0;

    }

    // 校验应用名称是否存在
    private Boolean isExistsAppName(String appName) {

        return appMapper.selectCount(new LambdaQueryWrapper<App>().eq(App::getAppName, appName)) > 0;

    }
}
