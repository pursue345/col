package com.colredis.jedisOpe;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * jedis操作redis的几种数据类型
 */
public class JedisDemo01 {

    public static void main(String[] args) {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.3.28", 6379);

        //测试
        String ping = jedis.ping();
        System.out.println(ping);
    }

    /**
     * 操作String
     */
    @Test
    public void demo01(){
        Jedis jedis = new Jedis("192.168.3.28", 6379);
        //新增
        jedis.set("testJedis","lucky");
        //获取
        String testJedis = jedis.get("testJedis");
        System.out.println(testJedis);

        //设置多个key和value
        jedis.mset("k1","v1","k2","v2");
        List<String> mget = jedis.mget("k1", "k2");
        for (String s : mget) {
            System.out.println(s);
        }
        //匹配所有key
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }
    }

    /**
     * Jedis 操作list
     */
    @Test
    public void demo2() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.3.28", 6379);
        jedis.lpush("key1", "lucy", "mary", "jack");
        List<String> values = jedis.lrange("key1", 0, -1);
        System.out.println("Redis中存在List:" +values);
        jedis.close();
    }

    /**
     *  Jedis 操作set
     */
    @Test
    public void demo3() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.3.28", 6379);
        jedis.sadd("names", "lucy");
        jedis.sadd("names", "mary");
        Set<String> names = jedis.smembers("names");
        System.out.println("Redis中存在Set:" + names);
        jedis.close();
    }

    /**
     * Jedis 操作hash
     */
    @Test
    public void demo4() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.3.28", 6379);
        jedis.hset("users", "age", "20");
        String hget = jedis.hget("users", "age");
        System.out.println("Redis中存在Hash:" + hget);
        jedis.close();
    }

    /**
     * Jedis 操作zset
     */
    @Test
    public void demo5() {
        //创建Jedis对象
        Jedis jedis = new Jedis("192.168.3.28", 6379);
        jedis.zadd("china", 100d, "shanghai");
        Set<String> china = jedis.zrange("china", 0, -1);
        System.out.println("Redis中存在Zset:" + china);
        jedis.close();
    }
}
