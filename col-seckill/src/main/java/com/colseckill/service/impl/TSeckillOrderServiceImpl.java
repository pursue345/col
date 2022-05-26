package com.colseckill.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.colseckill.entity.TSeckillOrder;
import com.colseckill.entity.User;
import com.colseckill.mapper.TSeckillOrderMapper;
import com.colseckill.service.TSeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
public class TSeckillOrderServiceImpl extends ServiceImpl<TSeckillOrderMapper, TSeckillOrder> implements TSeckillOrderService {

    @Autowired
    TSeckillOrderMapper seckillOrderMapper;
    @Autowired
    RedisTemplate redisTemplate;
    /**
   * 获取秒杀结果
   *
   * @param user
   * @param goodsId
   * @return
   */

    @Override
    public Long getResult(User user, Long goodsId){
        TSeckillOrder seckillOrder = seckillOrderMapper.queryByMsg(user.getId(),goodsId);
        if(null != seckillOrder){
            return seckillOrder.getId();
        }else{
            //判断缓存中是否有库存
            if(redisTemplate.hasKey("isStockEmpty:"+ goodsId)){
                return -1L;
            }else{
                return 0L;
            }
        }

    }

}
