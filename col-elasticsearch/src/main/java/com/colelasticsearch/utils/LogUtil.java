package com.colelasticsearch.utils;


import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

/**
 * 基础日志类
 *
 * @author zhoujh
 * @version 20180709
 */
public class LogUtil {

    private static Log log = LogFactory.getLog(LogUtil.class);
    private static final String thisClassName = LogUtil.class.getName();
    //信息分隔符
    private static final String msgSplit = ":";
    //是否要定位服务
    private static boolean showLocSrc = true;
    //是否显示日志
    private static final boolean enabled = true;
    //显示等级
    private static int level = 1;
    private static final int debug = 1;
    private static final int info = 2;
    private static final int warn = 3;
    private static final int error = 4;




     /*
        日志记录具体使用规则如下：
        Error：所有异常捕获的Catch节点中异常内容用此级别进行记录（如：Exception对象的e.ToString()）
        Warn：所有验证未通过时用此级别进行记录（暂时只用来记录接口请求或通知等验证未通过需要警告进行跟进的操作）（如：签名解析失败记录）
        Info：功能日常交互信息类用此级别进行记录（如：请求接口参数、接口响应参数等）
        Debug：业务代码交互或调试使用此级别进行记录，但只存在于开发及测试环境，生产环境将会禁用不会记录（如：xx对象信息或xx对象json等）
        日志记录格式如下：
        【传入参数（如：操作ID、订单ID、票号、金额等）】【具体内容】 /n Exception堆栈信息（Exception记录必须使用e.ToString()记录堆栈信息）
    */

    /* 日志格式化文本 */
    public static final String LOGFORMAT = "【%s】【%s】";

    public static final String getLogContent(String pContent, Object... pParameters) {

        String fParameters = "";
        if (null != pParameters && 0 < pParameters.length) {
            Gson gson = new Gson();
            fParameters = String.format("参数：%s", gson.toJson(pParameters));
        }

        return String.format(LOGFORMAT, pContent, fParameters);
    }



    /**
     * 记录Debug级日志[业务代码交互或调试使用此级别进行记录，但只存在于开发及测试环境，生产环境将会禁用不会记录（如：xx对象信息或xx对象json等）]
     *
     * @param content 信息综述
     */
    public static void debug(String content) {
        debug(content,"");
    }

    public static void debug(String content, Object parameters) {
        debug(content, JSONUtil.toJson(parameters));
    }

    public static void debug(String content, String parameters) {
        String message=String.format(LOGFORMAT, content, parameters);
        if (!enabled || debug < level)
            return;
        if (showLocSrc) {
            log(debug, message, Thread.currentThread().getStackTrace(),null);
        } else {
            log(debug, message, null,null);
        }
    }






    /**
     * 记录Info级日志[功能日常交互信息类用此级别进行记录（如：请求接口参数、接口响应参数等）]
     *
     * @param content 信息综述
     */
    public static void info(String content) {
        info(content,"");
    }

    public static void info(String content, Object parameters) {
        info(content, JSONUtil.toJson(parameters));
    }

    public static void info(String content, String parameters) {
        String message=String.format(LOGFORMAT, content, parameters);
        if (!enabled || info < level)
            return;
        if (showLocSrc) {
            log(info, message, Thread.currentThread().getStackTrace(),null);
        } else {
            log(info, message, null,null);
        }
    }




    /**
     * 记录Warn级日志[所有验证未通过时用此级别进行记录（暂时只用来记录接口请求或通知等验证未通过需要警告进行跟进的操作）（如：签名解析失败记录）]
     *
     * @param content 信息综述
     */
    public static void warn(String content) {
        warn(content,"");
    }

    public static void warn(String content, Object parameters) {
        warn(content, JSONUtil.toJson(parameters));
    }

    public static void warn(String content, String parameters) {
        String message=String.format(LOGFORMAT, content, parameters);
        if (!enabled || warn < level)
            return;
        if (showLocSrc) {
            log(warn, message, Thread.currentThread().getStackTrace(),null);
        } else {
            log(warn, message, null,null);
        }
    }





    /**
     * Error：所有异常捕获的Catch节点中异常内容用此级别进行记录（如：Exception对象的e.ToString()）
     *
     * @param
     * @param content 信息综述
     */
    public static void error(String content) {
        error(content,"");
    }

    public static void error(String content, Object parameters) {
        error(content, JSONUtil.toJson(parameters));
    }

    public static void error(String content, String parameters) {
        String message=String.format(LOGFORMAT, content, parameters);
        if (!enabled || error < level)
            return;
        if (showLocSrc) {
            log(error, message, Thread.currentThread().getStackTrace(),null);
        } else {
            log(error, message, null,null);
        }
    }

    public static void error(String content,Exception e) {
        error(content,"",e);
    }

    public static void error(String content, Object parameters,Exception e) {
        error(content, JSONUtil.toJson(parameters),e);
    }

    public static void error(String content, String parameters,Exception e) {
        String message=String.format(LOGFORMAT, content, parameters);
        if (!enabled || error < level)
            return;
        if (showLocSrc) {
            log(error, message, Thread.currentThread().getStackTrace(),e);
        } else {
            log(error, message, null,e);
        }
    }


    private static String getStackMsg(StackTraceElement[] ste) {
        if (ste == null) return "";

        boolean srcFlag = false;
        for (int i = 0; i < ste.length; i++) {
            StackTraceElement s = ste[i];

            if(s==null) continue;

            // 如果上一行堆栈代码是本类的堆栈，则该行代码则为源代码的最原始堆栈。
            if (srcFlag) {
                if(!thisClassName.equals(s.getClassName()))
                    return s.toString();
            }

            // 定位本类的堆栈
            if (thisClassName.equals(s.getClassName())) {
                srcFlag = true;
            }
        }
        return "";
    }


    private static void log(int level, Object message, StackTraceElement[] ste, Exception e) {
        if (ste != null) {
            message = getStackMsg(ste) + msgSplit + message;
        }

        switch (level) {
            case info:
                log.info(message);
                break;
            case debug:
                log.debug(message);
                break;
            case warn:
                log.warn(message);
                break;
            case error:
                log.error(message,e);
                break;
            default:
                log.debug(message);
        }
    }

    public static void main(String[] args) {
        debug("ss");
        debug("sdf","sdf");
        debug("sdf",new HashMap());

        info("ss");
        info("sdf","sdf");
        info("sdf",new HashMap());

        warn("ss");
        warn("sdf","sdf");
        warn("sdf",new HashMap());


        error("ss");
        error("sdf","sdf");
        error("sdf",new HashMap());

        error("ss",new Exception());
        error("sdf","sdf",new Exception());
        error("sdf",new HashMap(),new Exception());
    }
}
