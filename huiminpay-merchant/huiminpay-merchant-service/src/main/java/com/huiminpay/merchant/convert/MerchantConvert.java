package com.huiminpay.merchant.convert;

import com.huiminpay.merchant.dto.MerchantDTO;
import com.huiminpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface MerchantConvert {

    MerchantConvert INSTANCE = Mappers.getMapper(MerchantConvert.class);

    //entity to dto
    MerchantDTO entityToDto(Merchant merchant);

    //dto to entity
    Merchant dtoToEntity(MerchantDTO dto);

    //entityList to dtoList
    List<MerchantDTO> entityListToDtoList(List<Merchant> entityList);

    //dtoList to entityList
    List<Merchant> dtoListToEntityList(List<MerchantDTO> dtoList);


    //测试
    public static void main(String[] args) {

        //test entity to dto
        Merchant merchant = new Merchant();
        merchant.setMerchantName("merchantName");
        merchant.setMerchantAddress("merchantAddress");
        MerchantDTO merchantDTO = MerchantConvert.INSTANCE.entityToDto(merchant);
        System.out.println(merchantDTO);

        //test dto to entity
        MerchantDTO merchantDTO1 = new MerchantDTO();
        merchantDTO1.setMerchantName("merchantName");
        merchantDTO1.setMerchantAddress("merchantAddress");
        Merchant merchant1 = MerchantConvert.INSTANCE.dtoToEntity(merchantDTO1);
        System.out.println(merchant1);

        //test entityList to dtoList
        List<Merchant> entityList = new ArrayList<>();
        entityList.add(merchant);
        List<MerchantDTO> dtoList = MerchantConvert.INSTANCE.entityListToDtoList(entityList);
        System.out.println(dtoList);

        //test dtoList to entityList
        List<MerchantDTO> dtoList1 = new ArrayList<>();
        dtoList1.add(merchantDTO1);
        List<Merchant> entityList1 = MerchantConvert.INSTANCE.dtoListToEntityList(dtoList1);
        System.out.println(entityList1);

    }
}
