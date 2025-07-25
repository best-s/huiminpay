package com.huiminpay.merchant.convert;

import com.huiminpay.merchant.dto.AppDTO;
import com.huiminpay.merchant.entity.App;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AppConvert {
    //static instance
    AppConvert INSTANCE = Mappers.getMapper(AppConvert.class);

    //dto to entity
    App dto2entity(AppDTO dto);
    //entity to dto
    AppDTO entity2dto(App entity);

    //dtoList to entityList
    List<App> dtoList2entityList(List<AppDTO> dtoList);
    //entityList to dtoList
    List<AppDTO> entityList2dtoList(List<App> entityList);
}
