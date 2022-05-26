package com.colredis.bootRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisTestController {
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * redis新增、获取
     * @return
     */
    @RequestMapping("/test01")
    public String testRedis01(){
        //设置值到redis
        redisTemplate.opsForValue().set("name","lucy");

        //从redis中取值
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name);
        return name;
    }


    /**
     * 使用连接池，事务
     * @return
     */
    @RequestMapping("/test02")
    public boolean testRedis02(){
        JedisPool jedisPoolInstance = JedisPoolUtil.getJedisPoolInstance();
        Jedis jedis = jedisPoolInstance.getResource();

        //监视key-count
        String name = jedis.watch("count1");

        //开启事务
        Transaction multi = jedis.multi();
        multi.incr("count1");
        multi.decrBy("count2",100);
        List<Object> exec = multi.exec();
        if(exec==null || exec.size()==0){
            System.out.println("执行失败！！！");
            jedis.close();
            return false;
        }
        return true;
    }

    @RequestMapping("/testLock")
    public void testLock(){
        String uuid = UUID.randomUUID().toString();
        //1.获取锁，setnx
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid);
        //设置锁过期时间
        redisTemplate.expire("lock",10, TimeUnit.SECONDS);
        //2.获取锁成功后，查询num的值
        if(lock){
            Object value = redisTemplate.opsForValue().get("num");
            //2.1判断num为空
            if(StringUtils.isEmpty(value)){
                return;
            }
            //2.2值转换
            int num = Integer.parseInt(value + "");
            //2.3把redis值加1
            redisTemplate.opsForValue().increment("num");
            //2.4释放锁,uuid比较避免误删问题
            String lockUuid = (String) redisTemplate.opsForValue().get("lock");
            if(lockUuid.equals(uuid)){
                redisTemplate.delete("lock");
            }
        }else{
            //3.获取锁失败，每隔0.1秒再获取
            try {
                Thread.sleep(1000);
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 使用lua脚本加锁，保证操作的原子性
     */
    @RequestMapping("/testLockLua")
    public void testLockLua(){
        //1.声明一个uuid,将作为一个value，放入我们的key所对应的值中
        String uuid = UUID.randomUUID().toString();
        //2.定义一个锁，Lua脚本可以使用同一把锁，来实现删除。
        String skuId = "25";//访问skuId为25号的商品
        String lockKey = "lock" + skuId;//锁住的是每个商品的数量
        //3.获取锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 3,TimeUnit.SECONDS);

        //如果true
        if(lock){
            //执行的业务逻辑开始
            //获取缓存中的num数据
            Object value = redisTemplate.opsForValue().get("num");
            //如果为空，直接返回
            if(StringUtils.isEmpty(value)){
                return;
            }
            //不为空，转换值，如果这儿异常，那么delete就删除失败，也就是锁永远存在！
            int num = Integer.parseInt(value + "");
            //2.3把redis值加1
            redisTemplate.opsForValue().increment("num");
            //使用lua脚本来锁
            String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            //使用redis执行lua
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            //设置一下返回值类型 为Long
            //因为删除判断的时候，返回的0，给其封装为数据类型，如果不封装那么默认返回String类型
            //那么返回字符串与0会有错误发生
            redisScript.setResultType(Long.class);
            //第一个要是script脚本，第二个需要判断的key,第三个就是key所对应的值
            redisTemplate.execute(redisScript, Arrays.asList(lockKey),uuid);
        }else{
            //获取锁失败，每隔0.1秒再获取
            try {
                Thread.sleep(1000);
                testLockLua();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
