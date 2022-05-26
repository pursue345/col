package com.colseckill.service;

import com.colseckill.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
public interface TSeckillOrderService{
    public Long getResult(User user, Long goodsId);

}
