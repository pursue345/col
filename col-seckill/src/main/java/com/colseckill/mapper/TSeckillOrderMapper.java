package com.colseckill.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.colseckill.entity.TSeckillOrder;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
public interface TSeckillOrderMapper extends BaseMapper<TSeckillOrder> {

    void insertSecOrder(TSeckillOrder seckillOrder);

    TSeckillOrder queryByMsg(Integer userId, Long goodsId);
}
