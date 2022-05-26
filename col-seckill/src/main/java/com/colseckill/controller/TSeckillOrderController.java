package com.colseckill.controller;


import com.colseckill.entity.User;
import com.colseckill.service.TSeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author donggl
 * @since 2022-04-26
 */
@Controller
@RequestMapping("/tSeckillOrder")
public class TSeckillOrderController {
    @Autowired
    TSeckillOrderService seckillOrderService;

    /**
     * 查询秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/getResult")
    @ResponseBody
    public Long getResult(User user, Long goodsId){
        return seckillOrderService.getResult(user,goodsId);
    }

}

