package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

/**
 * 体检套餐的controller
 */
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    //使用JedisPool来操作redis服务
    @Autowired
    private JedisPool jedisPool;
    //文件上传
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){//可以使用@RequestParam从请求中提取查询参数，表单参数甚至文件。
        //System.out.println(imgFile);
        String originalFilename = imgFile.getOriginalFilename();//通过这个方法拿到当前拿到的图片的原始文件名
        int index = originalFilename.lastIndexOf(".");//拿到原始文件名中最后一个点，最后一个点之后就是原始文件的文件格式
        String extention = originalFilename.substring(index - 1);//截取文件的扩展名,结果：.jpg
        String fileName= UUID.randomUUID().toString()+extention;//拼接文件名的后缀
        try {
            //将拿到的图片通过前面写的utils中的方法上传到七牛云云存储中
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            //上传完成后使用redis，将文件存入到redis集合(大集合)中
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);//sadd表示操作的是set集合，向一个指定的set集合中添加元素
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
       }
        return new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
    }

    @Reference
    private SetmealService setmealService;
    //新增套餐controller
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){
        try{
            setmealService.add(setmeal,checkgroupIds);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    //创建分页查询方法
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){//提前封装的QueryPageBean为了接收分页查询的数据

        return setmealService.pageQuery(queryPageBean);
    }
}
