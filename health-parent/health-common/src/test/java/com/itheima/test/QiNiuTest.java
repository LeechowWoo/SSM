package com.itheima.test;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

public class QiNiuTest {
    //使用七牛云提供的SDK实现将本地图片提交到七牛云服务器
    @Test
    public void test01(){
        //构造一个带指定 Zone 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "WcmzH4NHCnYFzKojv0H9ZC3BNztbxXDJFhmQh-0U";
        String secretKey = "GsO3prXN-yV9U6HKET2Acyhuor66rTqNtdbTQZj6";
        String bucket = "leechao-health-space-1";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "C:\\Users\\44816\\Desktop\\wallpaper\\wallhaven-ox6wjl.jpg";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key ="transformer";
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    //测试删除七牛云中的文件
    @Test
    public void test02(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        String accessKey = "WcmzH4NHCnYFzKojv0H9ZC3BNztbxXDJFhmQh-0U";
        String secretKey = "GsO3prXN-yV9U6HKET2Acyhuor66rTqNtdbTQZj6";
        String bucket = "leechao-health-space-1";
        String key = "transformer";//此处不加文件格式后缀，否则报错
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}
