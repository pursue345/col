package com.colredis.bootRedis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * redis集群
 */
public class RedisClusterDemo {
    public static void main(String[] args) {

        //创建对象
        HostAndPort hostAndPort = new HostAndPort("192.168.3.28", 6379);
        JedisCluster jedisCluster = new JedisCluster(hostAndPort);

        //进行操作
        jedisCluster.set("b1","value");
        String b1 = jedisCluster.get("b1");
        System.out.println(b1);

        jedisCluster.close();
    }
}
