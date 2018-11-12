/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.di.wx.mq.utils;

import cn.ac.iie.di.wx.mq.common.RuntimeEnv;
import static cn.ac.iie.di.wx.mq.utils.TimeStampUtil.logger;
import cn.ac.iie.di.wx.mq.vo.RedisVo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class putqueueThread implements Runnable {

    @Override
    public void run() {
        try {
            TimeStampUtil stampUtil = new TimeStampUtil();
            long xuleihao = stampUtil.nextId();//不知道英文怎么说
            String redisValue = "{\"g_id\":\"i1508774401@276f7c8a6efdf0eaa9272305ed9feaba\",\"sendtime\":1508812013,\"table\":\"tp_wxq_media\",\"m_ch_id\":\"1508811081\",\"u_ch_id\":\"691558823\",\"m_chat_room\":\"6794786774\"}";
            String requestJson = "{\"msgid\":\"13344030018812013256\",\"sysuserid\":\"admin\",\"param\":[{\"name\":\"url\",\"value\":\"/fs/fhfs/v1/DUM2/20171024/http/RVshpu/892/3187391/77325\"}],\"taskid\":\"13344030018812013256\",\"areaid\":\"440300\",\"opid\":\"13344030018812013256\",\"sysid\":\"WXB\",\"userareaid\":\"440300\"}";
            while (true) {
                try {
                    String opid = "133" + "440300" + String.valueOf(xuleihao);//最后位数最多为10位
                    if (RuntimeEnv.retryQueue.offer(new RedisVo(opid, redisValue, requestJson.replace("13344030018812013256", opid)))) {
                       continue;
                    }
                    Thread.sleep(200);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(putqueueThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
