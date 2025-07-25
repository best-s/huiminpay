package com.huiminpay.merchant.convert;

import com.huiminpay.merchant.dto.StaffDTO;
import com.huiminpay.merchant.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StaffConvert {

    StaffConvert INSTANCE = Mappers.getMapper(StaffConvert.class);

    StaffDTO entity2dto(Staff entity);

    Staff dto2entity(StaffDTO dto);

    List<StaffDTO> listEntity2dto(List<Staff> staff);

}