/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.di.wx.mq.utils;

import cn.ac.iie.di.wx.mq.common.RuntimeEnv;
import static com.sun.deploy.uitoolkit.impl.fx.DeployPerfLogger.timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import static cn.ac.iie.di.wx.mq.common.RuntimeEnv.groupMediaQueryAt;
import cn.ac.iie.di.wx.mq.startup.MqStartup;
import static cn.ac.iie.di.wx.mq.startup.MqStartup.init;
import cn.ac.iie.di.wx.mq.timerTask.RedoTask;
import cn.ac.iie.di.wx.mq.timerTask.RetryFileThread;
import cn.ac.iie.di.wx.mq.vo.RedisVo;
import java.util.Timer;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class TimeStampUtil {

    static Logger logger = Logger.getLogger(TimeStampUtil.class.getName());

    public static void main(String[] args) {
        try {
            init();
            RedoTask redoTask=new RedoTask();
            Timer timer = new Timer();
            timer.schedule(redoTask, 1000, 1000);
            putqueueThread t1 = new putqueueThread();
            Thread t2 = new Thread(t1);
            t2.start();
            RetryFileThread retryTask = new RetryFileThread();
            Thread t = new Thread(retryTask);
            t.start();
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
        }
    }
    private static long INFOID_FLAG = 1490000000000L;
    protected static int SERVER_ID = 1;

    public synchronized long nextId() throws Exception {
        if (SERVER_ID <= 0) {
            throw new Exception("server id is error,please check config file!");
        }
        long infoid = System.currentTimeMillis() - INFOID_FLAG;
//        System.out.println(infoid);
//        infoid = infoid | SERVER_ID;
        Thread.sleep(1);
        return infoid;
    }
}
