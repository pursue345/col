package com.colseckill.controller;

import com.colseckill.entity.GoodsVo;
import com.colseckill.entity.SeckillMessage;
import com.colseckill.entity.User;
import com.colseckill.rabbitmq.MqSender;
import com.colseckill.service.TGoodsService;
import com.colseckill.utils.JsonUtil;
import com.colseckill.utils.RespBean;
import com.colseckill.utils.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀接口入口
 * 压力测试工具：jemeter、ab
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private TGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MqSender mqSender;

    //RabbitMQ系统管理功能组件(创建和删除Queue,Exchange,Binding)
    @Autowired
    AmqpAdmin amqpAdmin;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping("/doSeckill")
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId){
        //1.判断用户是否为空
        if(user == null)
            return RespBean.error(RespBeanEnum.SESSION_ERROR);

        //2.获取redis连接
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //3.判断是否重复抢购
        String secKillOrderJson = (String)valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if(StringUtils.isNotEmpty(secKillOrderJson)){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //内存标记，减少redis访问，判断是否还存在该物品
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //4.判断库存是否为空，不为空则减少库存
        Long stock = valueOperations.decrement("seckillGoods" + goodsId);
        if(stock < 0){
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods"+ goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //5.请求入队，立即返回排队中，将订单入库
        SeckillMessage message = new SeckillMessage(user,goodsId);
//        rabbitTemplate.convertAndSend("exchange.direct","atguigu.news",JsonUtil.object2JsonStr(message));
        mqSender.sendseckillQueue(JsonUtil.object2JsonStr(message));

        return RespBean.success(0);
    }

    /**
     * 系统初始化，把商品库存数量加载到redis
     */
    @Override
    public void afterPropertiesSet() throws Exception{
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list))
            return;

        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods" + goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(),false);
        });

    }
}
