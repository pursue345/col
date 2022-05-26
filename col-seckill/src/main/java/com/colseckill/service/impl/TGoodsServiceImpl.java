package com.colseckill.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.colseckill.entity.GoodsVo;
import com.colseckill.entity.TGoods;
import com.colseckill.mapper.TGoodsMapper;
import com.colseckill.service.TGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
@Service
public class TGoodsServiceImpl extends ServiceImpl<TGoodsMapper, TGoods> implements TGoodsService {

    @Autowired
    private TGoodsMapper goodsMapper;
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodId) {
        return goodsMapper.findGoodsVoByGoodsId(goodId);
    }

    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }
}
