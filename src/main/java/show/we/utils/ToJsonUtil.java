/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.di.wx.mq.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ToJsonUtil {

    static Logger logger = Logger.getLogger(ToJsonUtil.class.getName());

    public static String jsonUtil(
            String opid, String areaid,
            String userareaid, String sysid,
            String taskid, String sysuserid,
            String msgid, String name, String value) {

        JSONObject jSONObject = new JSONObject();

        jSONObject.put("opid", opid);
        jSONObject.put("areaid", areaid);//先填死
        jSONObject.put("userareaid", userareaid);//userareaid和destareaid一样
        jSONObject.put("sysid", sysid);
        jSONObject.put("taskid", taskid);//taskid=opid
        jSONObject.put("sysuserid", sysuserid);//一定是admin
        jSONObject.put("msgid", msgid);
        JSONArray paramObject = new JSONArray();
        JSONObject nameJSON = new JSONObject();
        nameJSON.put("name", name);
        nameJSON.put("value", value);
        paramObject.add(nameJSON);
        jSONObject.put("param", paramObject);
        String json = jSONObject.toJSONString();
        return json;
    }

    public static String string2Json(
            String opid, String areaid,
            String userareaid, String sysid,
            String taskid, String sysuserid,
            String msgid, String name, String value) {
        if (sysuserid.equals("") || areaid.equals("") || userareaid.equals("") || sysid.equals("") || name.equals("") || value.equals("") || opid.equals("") || msgid.equals("") || taskid.equals("")) {
            logger.info("parameter error");
            return "";
        } else {

            return "{\"sysuserid\":\"" + sysuserid + "\",\"areaid\":\"" + areaid + "\",\"userareaid\":\"" + userareaid + "\",\"sysid\":\"" + sysid + "\",\"param\":[{\"name\":\"" + name + "\",\"value\":\"" + value + "\"}],\"opid\":\"" + opid + "\",\"msgid\":\"" + msgid + "\",\"taskid\":\"" + taskid + "\"}";
        }
    }

//    public static void main(String[] args) {
//        String opid = "020004";
//        String areaid = "320000";
//        String userareaid = "320000";
//        String sysid = "WXB";
//        String taskid = "020004";
//        String sysuserid = "admin";
//        String msgid = "020004";
//        String name="url";
//        String m_mm_url = "/fs/fhfs/v1/eAHk/20170412/http/HbG8iM/1671/258412/99287";
////        System.out.println(jsonUtil(opid, areaid, userareaid, sysid, taskid, sysuserid, msgid, name, value));
////        System.out.println(string2Json(opid, areaid, userareaid, sysid, taskid, sysuserid, msgid, name, value));
//        System.out.println(ToJsonUtil.jsonUtil(opid, "440300", "440300", "WXB", opid, "admin", opid, "url", m_mm_url));
//    }
    public static void main(String[] args) {
        HashMap<String, String> g_idAndopidMap = new HashMap<String, String>(10);
        System.out.println(g_idAndopidMap.size());

    }
}
