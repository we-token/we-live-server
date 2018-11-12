/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.di.wx.mq.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import cn.ac.iie.di.wx.mq.configuration.Configuration;
import cn.ac.iie.di.wx.mq.vo.RedisVo;

/**
 *
 * @author AlexMu
 */
public class RuntimeEnv {

    private static Configuration conf = null;
    //定义运行时参数及其对应的配置环境的变量名
    public static final String IP = "ip";
    public static final String PORT = "port";
    public static final String THREADNUM = "threadNum";

    public static final String OCRQuery = "OCRQuery";
    public static final String FCMsgByKeywordQuery = "FCMsgByKeywordQuery";
    public static final String friendCircleMsgQuery = "friendCircleMsgQuery";
    public static final String groupMediaQuery = "groupMediaQuery";
    public static final String picByPQuery = "picByPQuery";

    public static final String redis_hosts = "redis_hosts";
    public static final String redis_masterName = "redis_masterName";
    public static final String redis_dbName = "redis_dbName";

    public static final String mq_reconnection = "mq_reconnection";
    public static final String mq_topic = "mq_topic";
    public static final String mq_groupName = "mq_groupName";
    public static final String mq_threadNum = "mq_threadNum";
    //紧急
    public static final String urgent_mq_reconnection = "urgent_mq_reconnection";
    public static final String urgent_mq_topic = "urgent_mq_topic";
    public static final String urgent_mq_groupName = "urgent_mq_groupName";
    public static final String urgent_mq_threadNum = "urgent_mq_threadNum";

    public static final String areaid = "areaid";
    public static final String userareaid = "userareaid";
    public static final String sysid = "sysid";
    public static final String sysuserid = "sysuserid";
    //起始消费时间
    public static final String consum_from_timestamp = "consum_from_timestamp";
    public static final String pipMaxNum = "pipMaxNum";
    public static final String jsonMaxNum = "jsonMaxNum";
    public static AtomicInteger CSCount = new AtomicInteger(0);//消费总数
    public static AtomicInteger GMCount = new AtomicInteger(0);//发送成功总数
    public static AtomicInteger FLCount = new AtomicInteger(0);//是不是总数
    public static LinkedBlockingQueue<RedisVo> retryQueue = new LinkedBlockingQueue<RedisVo>(64);

    public static LinkedBlockingQueue<HashMap<String, String>> redisQueue = new LinkedBlockingQueue<>(64);
    public static LinkedBlockingQueue<HashMap<String, String>> urgent_redisQueue = new LinkedBlockingQueue<>(64);

    public static AtomicInteger groupMediaQueryAt = new AtomicInteger(0);
    private static Map<String, Object> dynamicParams = new HashMap<String, Object>();
    static Logger logger = Logger.getLogger(RuntimeEnv.class.getName());

    public static boolean initialize(Configuration pConf) throws Exception {

        if (pConf == null) {
            logger.error("configuration object is null");
            throw new Exception("configuration object is null");
        }

        conf = pConf;

        //使用添加函数进行运行时参数设置
        setKeyAndValue(IP, "ip");
        setKeyAndValue(PORT, "port");
        setKeyAndValue(THREADNUM, "threadNum");

        setKeyAndValue(OCRQuery, "OCRQuery");
        setKeyAndValue(FCMsgByKeywordQuery, "FCMsgByKeywordQuery");
        setKeyAndValue(friendCircleMsgQuery, "friendCircleMsgQuery");
        setKeyAndValue(groupMediaQuery, "groupMediaQuery");
        setKeyAndValue(picByPQuery, "picByPQuery");

        setKeyAndValue(redis_hosts, "redis_hosts");
        setKeyAndValue(redis_masterName, "redis_masterName");
        setKeyAndValue(redis_dbName, "redis_dbName");

        setKeyAndValue(mq_reconnection, "mq_reconnection");
        setKeyAndValue(mq_topic, "mq_topic");
        setKeyAndValue(mq_groupName, "mq_groupName");
        setKeyAndValue(mq_threadNum, "mq_threadNum");

        setKeyAndValue(urgent_mq_reconnection, "urgent_mq_reconnection");
        setKeyAndValue(urgent_mq_topic, "urgent_mq_topic");
        setKeyAndValue(urgent_mq_groupName, "urgent_mq_groupName");
        setKeyAndValue(urgent_mq_threadNum, "urgent_mq_threadNum");
        setKeyAndValue(areaid, "areaid");
        setKeyAndValue(userareaid, "userareaid");
        setKeyAndValue(sysid, "sysid");
        setKeyAndValue(sysuserid, "sysuserid");
        setKeyAndValue(consum_from_timestamp, "consum_from_timestamp");
        setKeyAndValue(pipMaxNum, "pipMaxNum");
        setKeyAndValue(jsonMaxNum, "jsonMaxNum");
        return true;
    }

    public static boolean setKeyAndValue(String KEY, String parameter) throws Exception {
        String value = conf.getString(KEY, "");
        if (value.isEmpty()) {
            logger.error("parameter " + parameter + " does not exist or is not defined");
            throw new Exception("parameter " + parameter + " does not exist or is not defined");
        }
        addParam(KEY, value);
        return true;
    }

    public static void dumpEnvironment() {
        conf.dumpConfiguration();
    }

    public static void addParam(String pParamName, Object pValue) {
        synchronized (dynamicParams) {
            dynamicParams.put(pParamName, pValue);
        }
    }

    public static Object getParam(String pParamName) {
        return dynamicParams.get(pParamName);
    }
}
