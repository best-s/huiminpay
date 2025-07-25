package com.huiminpay.merchant.convert;

import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.vo.MerchantRegisterVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T20:26:49+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
public class MerchantRegisterConvertImpl implements MerchantRegisterConvert {

    @Override
    public MerchantDTO vo2dto(MerchantRegisterVo vo) {
        if ( vo == null ) {
            return null;
        }

        MerchantDTO merchantDTO = new MerchantDTO();

        merchantDTO.setUsername( vo.getUsername() );
        merchantDTO.setMobile( vo.getMobile() );
        merchantDTO.setPassword( vo.getPassword() );

        return merchantDTO;
    }

    @Override
    public MerchantRegisterVo dto2vo(MerchantDTO dto) {
        if ( dto == null ) {
            return null;
        }

        MerchantRegisterVo merchantRegisterVo = new MerchantRegisterVo();

        merchantRegisterVo.setMobile( dto.getMobile() );
        merchantRegisterVo.setUsername( dto.getUsername() );
        merchantRegisterVo.setPassword( dto.getPassword() );

        return merchantRegisterVo;
    }

    @Override
    public List<MerchantDTO> voList2dtoList(List<MerchantRegisterVo> voList) {
        if ( voList == null ) {
            return null;
        }

        List<MerchantDTO> list = new ArrayList<MerchantDTO>( voList.size() );
        for ( MerchantRegisterVo merchantRegisterVo : voList ) {
            list.add( vo2dto( merchantRegisterVo ) );
        }

        return list;
    }

    @Override
    public List<MerchantRegisterVo> dtoList2voList(List<MerchantDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<MerchantRegisterVo> list = new ArrayList<MerchantRegisterVo>( dtoList.size() );
        for ( MerchantDTO merchantDTO : dtoList ) {
            list.add( dto2vo( merchantDTO ) );
        }

        return list;
    }
}
