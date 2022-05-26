package com.colseckill.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.colseckill.entity.*;
import com.colseckill.mapper.TOrderMapper;
import com.colseckill.mapper.TSeckillOrderMapper;
import com.colseckill.service.TOrderService;
import com.colseckill.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TSeckillGoodsServiceImpl seckillGoodsService;
    @Autowired
    TSeckillOrderMapper seckillOrderMapper;
    @Autowired
    TOrderMapper orderMapper;

    @Override
    @Transactional
    public TOrder seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //秒杀商品表减库存
        TSeckillGoods seckillGoods = seckillGoodsService.queryByGoodId(goods.getId());
        seckillGoodsService.updateByGoodId(goods.getId());
        if(seckillGoods.getStockCount() < 1){
            //设置库存为0
            valueOperations.set("isStockEmpty" + goods.getId(),"0");
            return null;
        }

        //生成订单
        TOrder order = new TOrder();
        order.setUserId(Long.valueOf(user.getId()));
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        order.setPayDate(new Date());
        System.out.println(order.getCreateDate());
        orderMapper.insertOrder(order);

        //生成秒杀订单
        TSeckillOrder seckillOrder = new TSeckillOrder();
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(Long.valueOf(user.getId()));
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderMapper.insertSecOrder(seckillOrder);
        valueOperations.set("order:" + user.getId() + ":" + goods.getId(), JsonUtil.object2JsonStr(seckillOrder));

        return order;
    }

    @Override
    public void insertOrder(TOrder order) {
        orderMapper.insertOrder(order);
    }
}
