package com.huiminpay.merchant.api;

import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.PageVO;
import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.dto.StaffDTO;
import com.huiminpay.merchant.dto.StoreDTO;

public interface MerchantService {

    /**
     * 根据商户ID查询信息
     * @param merchantId 商户ID
     * @return 商户对象
     */
    MerchantDTO queryMerchantById(Long   merchantId);

    /**
     * 添加商户
     * @param merchantDTO 商户对象
     * @return 新增的商户对象
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;


    /**
     * 保存商户信息
     * @param merchantId 商户ID
     * @param merchantDTO 资质信息
     * @throws BusinessException
     */
    void applyMerchant(Long merchantId,MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 新增门店
     * @param storeDTO 门店对象
     * @return 新增的门店对象
     * @throws BusinessException
     */
    StoreDTO createStore(StoreDTO storeDTO) throws BusinessException;

    /**
     * 新增员工
     * @param staffDTO 员工对象
     * @return 新增的员工对象
     * @throws BusinessException
     */
    StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException;

    /**
     * 为门店设置管理员
     * @param staffId 员工ID
     * @param storeId 门店ID
     * @throws BusinessException
     */
    void  bindStaffToStore(Long staffId,Long storeId) throws BusinessException;

    /**
     * 根据租户ID查询商户信息
     * @param tenantId  租户ID
     * @return 商户对象
     */
    MerchantDTO queryMerchantByTenantId(Long tenantId);

    /**
     * 根据门店ID查询门店信息
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param storeDTO 门店对象
     * @return 门店分页对象
     * @throws BusinessException
     */
    PageVO<StoreDTO> queryStoreByPage(Integer pageNum, Integer pageSize, StoreDTO storeDTO) throws BusinessException;

    /**
     * 查询们门店是否属于该商户
     * @param merchantId 商户ID
     * @param storeId 应用ID
     * @return 是否属于该商户
     * @throws BusinessException
     */
    Boolean queryStoreInMerchant(Long merchantId, Long storeId) throws BusinessException;
}
