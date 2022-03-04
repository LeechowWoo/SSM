package com.itheima.jobs;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 自定义的job，实现定时清理垃圾图片
 */
public class ClearImgJob {
    /**
     * 操作redis，拿到两个集合，将两个集合做差，得到的就是垃圾图片
     *    将jedisPool注入
     */
    @Autowired
    private JedisPool jedisPool;
    public void clearImg(){
        //根据redis中保存的两个集合进行差值的计算，获取垃圾图片名称的集合。
        //sdiff的作用就是计算两个集合的差值
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if(set!=null){
            for (String picName : set) {
                //遍历这个垃圾图片的集合
                //删除七牛云服务器上的图片
                QiniuUtils.deleteFileFromQiniu(picName);
                //从redis的大集合中删除垃圾图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,picName);
                System.out.println("清理垃圾图片："+picName);
            }
        }
    }
}
