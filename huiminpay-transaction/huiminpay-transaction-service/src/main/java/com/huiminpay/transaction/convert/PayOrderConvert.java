package com.huiminpay.transaction.convert;

import com.huiminpay.transaction.api.dto.OrderResultDTO;
import com.huiminpay.transaction.api.dto.PayOrderDTO;
import com.huiminpay.transaction.entity.PayOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayOrderConvert {

    PayOrderConvert INSTANCE = Mappers.getMapper(PayOrderConvert.class);

    OrderResultDTO request2dto(PayOrderDTO payOrderDTO);

    PayOrderDTO dto2request(OrderResultDTO OrderResult);

    PayOrderDTO entity2dto(PayOrder entity);

    PayOrder dto2entity(OrderResultDTO dto);

    PayOrder dto2entity(PayOrderDTO dto);

}