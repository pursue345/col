package com.colseckill.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.colseckill.entity.GoodsVo;
import com.colseckill.entity.TGoods;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
public interface TGoodsMapper extends BaseMapper<TGoods> {

    /**
     * 功能描述: 获取商品详情
     * @param goodId
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodId);

    /**
     * 功能描述: 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();
}
