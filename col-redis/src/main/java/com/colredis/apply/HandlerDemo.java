package com.colredis.apply;

public class HandlerDemo {

    /**
     * 缓存穿透：缓存无数据，每次到数据库查询
     * 1.对空值缓存
     * 2.设置可访问的白名单，bitmap
     * 3.采用布隆过滤器，bloom Filter
     * 4.实时监控，监控到Redis性能下降时，排查访问对象和数据，设置黑名单限制服务
     */

    /**
     * 缓存击穿：热点数据过期，查询数据库，数据库压力变大
     * 1.预先设置热点数据，加长过期时间
     * 2.实时调整，监控热门数据，调整过期时长
     * 3.使用锁，
     *      缓存失效先判空，非立即查询数据库；
     *      使用带成功返回的操作，setnx
     *      操作返回成功时，再进行数据库操作，重设缓存，删除mutex key
     *      操作返回失败时，证明有现成在加载数据库，当前线程休眠一段时间再重试整个get缓存的方法
     *
     *      String get(String key) {
     *       String value = redis.get(key);
     *        if (value  == null) {
     *          if (redis.setnx(key_mutex, "1")) {
     *          // 3 min timeout to avoid mutex holder crash
     *          redis.expire(key_mutex, 3 * 60)
     *          value = db.get(key);
     *          redis.set(key, value);
     *          redis.delete(key_mutex);
     *        } else {
     *          //其他线程休息50毫秒后重试
     *          Thread.sleep(50);
     *          get(key);
     *        }
     *       }
     *      }
     */


    /**
     * 缓存雪崩：大量数据过期
     * 1.构建多级缓存架构：nginx缓存+redis缓存+其它缓存（ehcache等）
     * 2.使用锁或队列，加锁或队列保证不会有大量线程对数据库一次性进行读写
     * 3.设置过期标志更新缓存，缓存过期出发线程后台更新时机key的缓存
     * 4.将缓存失效时间分散开，在原有失效的基础上增加一个随机值失效时间，避免缓存过期时间在同一时间过期
     */

    /**
     * 分布式锁：setnx
     * setnx k1 20 给k1加锁
     * expire k1 10 设置看k1的过期时间为10
     * del k1 删除k1
     * set k2 10 nx ex 12 给k2加锁 ，设置过期时间为12
     * ttl k2 查看过期时间
     */


}
