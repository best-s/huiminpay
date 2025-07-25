package com.huiminpay.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huiminpay.transaction.api.dto.PayChannelDTO;
import com.huiminpay.transaction.entity.PlatformChannel;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2019-11-15
 */
@Repository
public interface PlatformChannelMapper extends BaseMapper<PlatformChannel> {

    /**
     * 根据平台渠道代码查询支付渠道列表
     * @param platformChannelCode 平台渠道代码
     * @return 支付渠道列表
     */
    @Select("SELECT * FROM platform_pay_channel ppc ,pay_channel pc ,platform_channel pla WHERE ppc.PAY_CHANNEL = pc.CHANNEL_CODE AND ppc.PLATFORM_CHANNEL = pla.CHANNEL_CODE AND  ppc.PLATFORM_CHANNEL='wx_native'")
    List<PayChannelDTO> queryPayChannelByPlatformChannelId(String platformChannelCode);

}
