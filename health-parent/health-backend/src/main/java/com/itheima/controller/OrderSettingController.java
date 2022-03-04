package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约设置
 */

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;

    //文件上传，将预约设置数据批量导入
    @RequestMapping("/upload")
    /**
     * @RequestParam：将请求参数绑定到控制器的方法参数上（是springmvc中接收普通参数的注解）
     *
    语法：@RequestParam(value=”参数名”,required=”true/false”,defaultValue=””)
    value：参数名
    required：是否包含该参数，默认为true，表示该请求路径中必须包含该参数，如果不包含就报错。
    defaultValue：默认参数值，如果设置了该值，required=true将失效，自动为false,如果没有传该参数，就使用默认值
     */
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile){
        //得到excel文件后解析这个文件
        try {
            List<String[]> list = POIUtils.readExcel(excelFile);
            List<OrderSetting>data=new ArrayList<>();
            for(String[] strings:list){
                String orderDate = strings[0];//strings为一行数据，第一个数据为日期，第二个数据为预约人数
                String number = strings[1];//第二个数据为预约人数
                OrderSetting orderSetting = new OrderSetting(new Date(orderDate),Integer.parseInt(number));
                data.add(orderSetting);//得到List<OrderSetting>格式的对象,data是一个集合，里面保存着excel中每一行数据对应的oederSetting对象
            }
            //通过dubbo远程调用service实现数据批量导入到数据库中
            orderSettingService.add(data);
            return new Result(true,MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            //文件解析失败
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }

    //根据月份查对应的预约信息
    @RequestMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){//date的格式为：yyyy-MMM
        /*
        这里使用Map的原因：
            服务层调用持久层的sql语句，将预约数据查询出来，查出来的是ordersetting格式的数据，而根据前端页面分析：
            { date: 1, number: 120, reservations: 1 }前端是通过这样的键值对的形式绑定日历上每个日期的，而ordersetting中，
            数据包装的是：private Integer id ;
                        private Date orderDate;//预约设置日期
                        private int number;//可预约人数
                        private int reservations ;//已预约人数
             不能直接用于页面展示
         */
        try{
            List<Map>list=orderSettingService.getOrderSettingByMonth(date);
            return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,list);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_ORDERSETTING_FAIL);
        }
    }

    //根据日期设置对应的预约信息
    @RequestMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        try{
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true,MessageConstant.ORDERSETTING_SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ORDERSETTING_FAIL);
        }
    }
}
