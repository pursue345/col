package com.colseckill.service;


import com.colseckill.entity.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
public interface TGoodsService {

    GoodsVo findGoodsVoByGoodsId(Long goodId);

    List<GoodsVo> findGoodsVo();
}
