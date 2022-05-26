package com.colseckill.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.colseckill.entity.TSeckillGoods;
import com.colseckill.mapper.TSeckillGoodsMapper;
import com.colseckill.service.TSeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
@Service
public class TSeckillGoodsServiceImpl extends ServiceImpl<TSeckillGoodsMapper, TSeckillGoods> implements TSeckillGoodsService {

    @Autowired
    TSeckillGoodsMapper seckillGoodsMapper;
    public TSeckillGoods queryByGoodId(Long goodId) {
        return seckillGoodsMapper.queryByGoodId(goodId);
    }

    public void updateByGoodId(Long goodId) {
        seckillGoodsMapper.updateByGoodId(goodId);
    }
}
