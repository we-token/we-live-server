/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.ac.iie.di.wx.mq.vo;

/**
 *
 * @author Administrator
 */
public class RedisVo {

    public String opid;
    public String redisValue;
    public String requestJson;

    public RedisVo(String opid, String redisValue, String requestJson) {
        this.opid = opid;
        this.redisValue = redisValue;
        this.requestJson = requestJson;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getRedisValue() {
        return redisValue;
    }

    public void setRedisValue(String redisValue) {
        this.redisValue = redisValue;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

}
