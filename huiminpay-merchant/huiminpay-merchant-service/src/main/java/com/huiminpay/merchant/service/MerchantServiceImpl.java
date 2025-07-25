package com.huiminpay.merchant.service;

import java.time.LocalDateTime;
import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import com.huiminpay.common.cache.domain.PageVO;
import com.huiminpay.common.cache.util.PhoneUtil;
import com.huiminpay.merchant.api.MerchantService;
import com.huiminpay.merchant.convert.MerchantConvert;
import com.huiminpay.merchant.convert.StaffConvert;
import com.huiminpay.merchant.convert.StoreConvert;
import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.dto.StaffDTO;
import com.huiminpay.merchant.dto.StoreDTO;
import com.huiminpay.merchant.entity.Merchant;
import com.huiminpay.merchant.entity.Staff;
import com.huiminpay.merchant.entity.Store;
import com.huiminpay.merchant.entity.StoreStaff;
import com.huiminpay.merchant.mapper.MerchantMapper;
import com.huiminpay.merchant.mapper.StaffMapper;
import com.huiminpay.merchant.mapper.StoreMapper;
import com.huiminpay.merchant.mapper.StoreStaffMapper;
import com.huiminpay.user.api.TenantService;
import com.huiminpay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.huiminpay.user.api.dto.tenant.TenantDTO;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    StaffMapper staffMapper;
    @Autowired
    StoreStaffMapper storeStaffMapper;

    @Reference
    TenantService tenantService;

    /**
     * 根据id查询商户信息
     *
     * @param merchantId 商户ID
     * @return 商户信息
     */
    @Override
    public MerchantDTO queryMerchantById(Long merchantId) {
        //根据id查询商户信息
        Merchant merchant = merchantMapper.selectById(merchantId);

        return MerchantConvert.INSTANCE.entityToDto(merchant);

    }

    /**
     * 添加商户
     *
     * @param merchantDto 商户对象
     * @return 新增的商户
     */
    @Override
    public MerchantDTO createMerchant(MerchantDTO merchantDto) throws BusinessException {
        // 参数非空校验
        if (merchantDto == null) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        // 用户名非空校验
        if (StringUtils.isEmpty(merchantDto.getUsername())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        // 密码非空检验
        if (StringUtils.isEmpty(merchantDto.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100111);
        }
        // 手机号非空校验
        if (StringUtils.isEmpty(merchantDto.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        // 手机号格式校验
        if (!PhoneUtil.isMatches(merchantDto.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        // 手机号唯一性校验
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMobile, merchantDto.getMobile()));
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        //调saas服务接口
        /*
           1.手机号
           2.密码
           3.用户名
           4.租户类型 huimin-merchant
           5.默认套餐 huimin-merchant
           6.租户名称，同账号名
         */
        CreateTenantRequestDTO dto = new CreateTenantRequestDTO();
        dto.setMobile(merchantDto.getMobile());
        dto.setPassword(merchantDto.getPassword());
        dto.setName(merchantDto.getUsername());
        dto.setTenantTypeCode("huimin-merchant");
        dto.setBundleCode("huimin-merchant");
        dto.setUsername(merchantDto.getUsername());

        TenantDTO tenantAndAccount = tenantService.createTenantAndAccount(dto);

        if (tenantAndAccount == null || tenantAndAccount.getId() == null) {
            throw new BusinessException(CommonErrorCode.E_200012);
        }

        //租户id不能重复
        Long id = tenantAndAccount.getId();
        // 设置租户id
        Integer integer = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getTenantId, id));
        if (integer > 0) {
            throw new BusinessException(CommonErrorCode.E_200017);
        }

        Merchant merchant = MerchantConvert.INSTANCE.dtoToEntity(merchantDto);
        // 设置状态
        merchant.setAuditStatus("0"); // 0：未申请
        // 设置租户id
        merchant.setTenantId(id);
        merchantMapper.insert(merchant);

        //新增门店
        StoreDTO store = new StoreDTO();
        store.setStoreName("根门店");
        store.setStoreNumber(0L);
        store.setMerchantId(merchant.getId());
        store.setStoreStatus(true);
        this.createStore(store);

        //新增员工
        StaffDTO staff = new StaffDTO();
        staff.setMerchantId(merchant.getId());
        staff.setUsername(merchant.getUsername());
        staff.setMobile(merchant.getMobile());
        staff.setStoreId(store.getId());
        staff.setStaffStatus(true);
        this.createStaff(staff);

        //新增门店管理员
        this.bindStaffToStore(merchant.getId(), store.getId());


        return MerchantConvert.INSTANCE.entityToDto(merchant);
    }

    /**
     * 保存商户信息
     *
     * @param merchantId  商户ID
     * @param merchantDTO 资质信息
     * @throws BusinessException
     */
    @Override
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        // 根据id查询商户信息
        if (merchantId == null || merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }

        //dto 转 entity
        Merchant entity = MerchantConvert.INSTANCE.dtoToEntity(merchantDTO);
        // 设置id
        entity.setId(merchantId);

        // 设置状态
        entity.setAuditStatus("1"); // 1：已申请
        entity.setMobile(merchant.getMobile()); // 手机号不能修改
        entity.setTenantId(merchant.getTenantId()); // 租户id不能修改

        // 更新商户信息
        merchantMapper.updateById(entity);


    }

    /**
     * 新增门店
     *
     * @param storeDTO 门店对象
     * @return 新增的门店
     * @throws BusinessException
     */
    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException {
        Store store = StoreConvert.INSTANCE.dto2entity(storeDTO);
        storeMapper.insert(store);
        return StoreConvert.INSTANCE.entity2dto(store);
    }

    /**
     * 新增员工
     *
     * @param staffDTO 员工对象
     * @return
     * @throws BusinessException
     */
    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException {

        // 手机号非空校验
        if (StringUtils.isEmpty(staffDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        // 手机号格式校验
        if (!PhoneUtil.isMatches(staffDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        // 手机号唯一性校验
        Integer count = staffMapper.selectCount(
                new LambdaQueryWrapper<Staff>().
                        eq(Staff::getMobile, staffDTO.getMobile())
                        .eq(Staff::getMerchantId, staffDTO.getMerchantId()));
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        //用户名非空校验
        if (StringUtils.isEmpty(staffDTO.getUsername())) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        //用户名唯一性校验
        count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getUsername, staffDTO.getUsername())
                .eq(Staff::getMerchantId, staffDTO.getMerchantId()));
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_100114);
        }


        Staff staff = StaffConvert.INSTANCE.dto2entity(staffDTO);
        staffMapper.insert(staff);
        return StaffConvert.INSTANCE.entity2dto(staff);
    }

    /**
     * 绑定管理员到门店
     *
     * @param staffId 管理员ID
     * @param storeId 门店ID
     * @throws BusinessException
     */
    @Override
    public void bindStaffToStore(Long staffId, Long storeId) throws BusinessException {

        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStaffId(staffId);
        storeStaff.setStoreId(storeId);
        storeStaffMapper.insert(storeStaff);

    }

    @Override
    public MerchantDTO queryMerchantByTenantId(Long tenantId) {
        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getTenantId, tenantId));
        return MerchantConvert.INSTANCE.entityToDto(merchant);
    }

    /**
     * 根据门店ID查询门店信息
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param storeDTO 门店对象
     * @return 门店分页对象
     * @throws BusinessException
     */
    @Override
    public PageVO<StoreDTO> queryStoreByPage(Integer pageNum, Integer pageSize,
                                             StoreDTO storeDTO) throws BusinessException{

        //分页对象
        Page<Store> page = new Page<>(pageNum, pageSize);

        //条件对象
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!StringUtils.isEmpty(String.valueOf(storeDTO.getMerchantId())), Store::getMerchantId , storeDTO.getMerchantId());

        IPage<Store> storeIPage = storeMapper.selectPage(page, queryWrapper);
        //分页对象转换
        List<Store> storeList = storeIPage.getRecords();
        List<StoreDTO> storeDTOS = StoreConvert.INSTANCE.listEntity2dto(storeList);

        return new PageVO<>(storeDTOS, storeIPage.getTotal(), pageNum, pageSize);

    }

    /**
     * 查询们门店是否属于该商户
     *
     * @param merchantId 商户ID
     * @param storeId    应用ID
     * @return 是否属于该商户
     * @throws BusinessException
     */
    @Override
    public Boolean queryStoreInMerchant(Long merchantId, Long storeId) throws BusinessException {

        Integer count = storeMapper.selectCount(new LambdaQueryWrapper<Store>()
                .eq(Store::getId, storeId)
                .eq(Store::getMerchantId, merchantId));
        return count > 0;

    }

}
