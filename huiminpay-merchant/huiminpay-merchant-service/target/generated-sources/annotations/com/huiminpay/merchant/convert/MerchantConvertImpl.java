package com.huiminpay.merchant.convert;

import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.entity.Merchant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T20:27:17+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
public class MerchantConvertImpl implements MerchantConvert {

    @Override
    public MerchantDTO entityToDto(Merchant merchant) {
        if ( merchant == null ) {
            return null;
        }

        MerchantDTO merchantDTO = new MerchantDTO();

        merchantDTO.setId( merchant.getId() );
        merchantDTO.setMerchantName( merchant.getMerchantName() );
        merchantDTO.setMerchantNo( merchant.getMerchantNo() );
        merchantDTO.setMerchantAddress( merchant.getMerchantAddress() );
        merchantDTO.setMerchantType( merchant.getMerchantType() );
        merchantDTO.setBusinessLicensesImg( merchant.getBusinessLicensesImg() );
        merchantDTO.setIdCardFrontImg( merchant.getIdCardFrontImg() );
        merchantDTO.setIdCardAfterImg( merchant.getIdCardAfterImg() );
        merchantDTO.setUsername( merchant.getUsername() );
        merchantDTO.setMobile( merchant.getMobile() );
        merchantDTO.setContactsAddress( merchant.getContactsAddress() );
        merchantDTO.setAuditStatus( merchant.getAuditStatus() );
        merchantDTO.setTenantId( merchant.getTenantId() );

        return merchantDTO;
    }

    @Override
    public Merchant dtoToEntity(MerchantDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Merchant merchant = new Merchant();

        merchant.setId( dto.getId() );
        merchant.setMerchantName( dto.getMerchantName() );
        merchant.setMerchantNo( dto.getMerchantNo() );
        merchant.setMerchantAddress( dto.getMerchantAddress() );
        merchant.setMerchantType( dto.getMerchantType() );
        merchant.setBusinessLicensesImg( dto.getBusinessLicensesImg() );
        merchant.setIdCardFrontImg( dto.getIdCardFrontImg() );
        merchant.setIdCardAfterImg( dto.getIdCardAfterImg() );
        merchant.setUsername( dto.getUsername() );
        merchant.setMobile( dto.getMobile() );
        merchant.setContactsAddress( dto.getContactsAddress() );
        merchant.setAuditStatus( dto.getAuditStatus() );
        merchant.setTenantId( dto.getTenantId() );

        return merchant;
    }

    @Override
    public List<MerchantDTO> entityListToDtoList(List<Merchant> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<MerchantDTO> list = new ArrayList<MerchantDTO>( entityList.size() );
        for ( Merchant merchant : entityList ) {
            list.add( entityToDto( merchant ) );
        }

        return list;
    }

    @Override
    public List<Merchant> dtoListToEntityList(List<MerchantDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Merchant> list = new ArrayList<Merchant>( dtoList.size() );
        for ( MerchantDTO merchantDTO : dtoList ) {
            list.add( dtoToEntity( merchantDTO ) );
        }

        return list;
    }
}
