package com.huiminpay.merchant.convert;

import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.vo.MerchantRegisterVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface MerchantRegisterConvert {

    MerchantRegisterConvert INSTANCE = Mappers.getMapper(MerchantRegisterConvert.class);

    //vo to dto
    MerchantDTO vo2dto(MerchantRegisterVo vo);

    //dto to vo
    MerchantRegisterVo dto2vo(MerchantDTO dto);

    //voList to dtoList
    List<MerchantDTO> voList2dtoList(List<MerchantRegisterVo> voList);

    //dtoList to voList
    List<MerchantRegisterVo> dtoList2voList(List<MerchantDTO> dtoList);

}
