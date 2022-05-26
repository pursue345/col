package com.colseckill.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.colseckill.entity.TOrder;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
public interface TOrderMapper extends BaseMapper<TOrder> {

    void insertOrder(TOrder tOrder);
}
