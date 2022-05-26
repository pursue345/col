package com.colseckill.rabbitmq;

import com.colseckill.entity.GoodsVo;
import com.colseckill.entity.SeckillMessage;
import com.colseckill.entity.User;
import com.colseckill.service.TGoodsService;
import com.colseckill.service.TOrderService;
import com.colseckill.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MqResive {

    @Autowired
    private TGoodsService goodsService;
    @Autowired
    private TOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = "queue")
    public void resiveForqueue(Object obj){
        log.info((String) obj);
    }


    @RabbitListener(queues = "seckillQueue")
    public void receiveSecKill(String msg){
        log.info("seckillQueue接受消息："+msg);
        SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        User user = message.getUser();
        Long goodsId = message.getGoodId();
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount() < 1)
            return;
        //判断是否重复抢购
        String secKillOrderJson = (String)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(StringUtils.isNotEmpty(secKillOrderJson)){
            return;
        }

        orderService.seckill(user,goods);
    }
}
