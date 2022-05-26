package com.colredis.jedisOpe;

import redis.clients.jedis.Jedis;
import java.util.Random;

/**
 * 模拟验证码发送，每个用户每天三次只能发送三次验证码
 */
public class PhoneCode {
    public static void main(String[] args) {
        // 测试步骤1 生成 成6位数字验证码
//        String code = getCode();
//        System.out.println(code);
        // 输出：565496
        //测试步骤2 模拟验证码发送
        verifyCode("13678765435");
        // 第一次测试输出：生成验证码 =561523
        // 第二次测试输出：生成验证码 =376848
        // 第三次测试输出：生成验证码 =983279
        // 第四次测试输出：今天发送次数已经超过三次
        //测试步骤3 模拟验证码校验
        // getRedisCode("13678765435","561523");
        // 第一次测试输出：成功
        // getRedisCode("13678765435", "1234");
        // 第二次测试输出：失败
        // 过一会再次测试  第三次测试输出：验证码已过期，请重新生成成
    }
    //步骤1 生成6位数字验证码
    public static String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            int rand = random.nextInt(10);
            code += rand;
        }
        return code;
    }
    //步骤2 每个手机每天只能发送三次，验证码放到redis中，设置过期时间120
    public static void verifyCode(String phone) {
        //连接redis
        Jedis jedis = new Jedis("192.168.3.28", 6379);
        //拼接key
        //手机发送次数key
        String countKey = "VerifyCode" + phone + ":count";
        //验证码key
        String codeKey = "VerifyCode" + phone + ":code";
        //每个手机每天只能发送三次，发送次数为 count
        String count = jedis.get(countKey);
        if (count == null) {
            //没有发送次数，第一次发送
            //设置发送次数是1
            jedis.setex(countKey, 24 * 60 * 60, "1");
        } else if (Integer.parseInt(count) <= 2) {
            // 发送次数为2，设置 发送次数+1
            jedis.incr(countKey);
        } else if (Integer.parseInt(count) > 2) {
            //发送三次，不能再发送
            System.out.println("今天发送次数已经超过三次");
            jedis.close();
            return;
        }
        //发送验证码放到redis里面
        // 获取验证码
        String vcode = getCode();
        System.out.println("生成验证码 =" + vcode);
        // 设置过期时间120秒，即2分钟
        jedis.setex(codeKey, 120, vcode);
        jedis.close();
    }
    //步骤3 验证码校验
    public static void getRedisCode(String phone, String code) {
        //从redis获取验证码
        //连接redis
        Jedis jedis = new Jedis("192.168.3.28", 6379);
        //验证码key
        String codeKey = "VerifyCode" + phone + ":code";
        String redisCode = jedis.get(codeKey);
        if (redisCode == null) {
            System.out.println("验证码已过期，请重新生成");
        } else if (redisCode.equals(code)) {
            System.out.println("成功");
        } else {
            System.out.println("失败");
        }
        jedis.close();
    }
}
