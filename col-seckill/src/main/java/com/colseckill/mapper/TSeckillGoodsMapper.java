package com.colseckill.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.colseckill.entity.TSeckillGoods;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
public interface TSeckillGoodsMapper extends BaseMapper<TSeckillGoods> {

    TSeckillGoods queryByGoodId(Long goodId);

    void updateByGoodId(Long goodId);
}
