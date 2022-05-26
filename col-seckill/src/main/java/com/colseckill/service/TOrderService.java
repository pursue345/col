package com.colseckill.service;


import com.colseckill.entity.GoodsVo;
import com.colseckill.entity.TOrder;
import com.colseckill.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
public interface TOrderService{

    TOrder seckill(User user, GoodsVo goods);

    void insertOrder(TOrder order);
}
