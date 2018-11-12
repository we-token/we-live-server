package cn.ac.iie.di.wx.mq.utils;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class JedisConnectionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JedisConnectionHandler.class);
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 1024;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;

    private static int TIMEOUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;

    private JedisPool jedisPool = null;
    private JedisSentinelPool jedisSentPool = null;

    public JedisConnectionHandler(String redisAddr, int redisPort, String auth) {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, redisAddr, redisPort, TIMEOUT, auth);
        } catch (Exception e) {
//            LOG.
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }

    /**
     * redis cluster
     */
    public JedisConnectionHandler(String instance, String masterName) {
        try {
            Set<String> set = new HashSet<String>();
            String[] arr = instance.split(";");
            for (String st : arr) {
                set.add(st);
            }
            jedisSentPool = new JedisSentinelPool(masterName, set);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }

    public JedisConnectionHandler(String instance, String masterName, Integer db) {
        try {
            Set<String> set = new HashSet<String>();
            String[] arr = instance.split(";");
            for (String st : arr) {
                set.add(st);
            }
            if (null == db) {
                jedisSentPool = new JedisSentinelPool(masterName, set);
            } else {
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(MAX_ACTIVE);
                config.setMaxIdle(MAX_IDLE);
                config.setMaxWaitMillis(MAX_WAIT);
                config.setTestOnBorrow(TEST_ON_BORROW);
                jedisSentPool = new JedisSentinelPool(masterName, set, config, TIMEOUT, null, db);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else if (jedisSentPool != null) {
                Jedis resource = jedisSentPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            //            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

    }
}
