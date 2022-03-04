package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 预约设置服务
 */
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService{
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Override
    public void add(List<OrderSetting> list) {
        if(list!=null && list.size()>0){
            for (OrderSetting orderSetting : list) {
                //判断当前日期是否已经进行了预约设置
                long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if(countByOrderDate>0){
                    //当前日期已经进行了预约设置，执行跟新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else{
                    //未进行预约操作，将当前预约加入
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    //根据月份查询对应的预约设置数据
    @Override
    public List<Map> getOrderSettingByMonth(String date) {//格式：yyyy-MM
        String begin=date+"-1";//2022-3-1
        String end=date+"-31";//2022-3-31
        Map<String,String>map= new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        //调用Dao，根据日期范围查询预约设置数据
        List<OrderSetting>list=orderSettingDao.getOrderSettingByMonth(map);
        List<Map>result=new ArrayList<>();
        if(list!=null && list.size()>0){
            for (OrderSetting orderSetting : list) {
                Map<String,Object>m=new HashMap<>();
                m.put("date",orderSetting.getOrderDate().getDate());//获取日期，几号
                m.put("number",orderSetting.getNumber());
                m.put("reservations",orderSetting.getReservations());
                result.add(m);
            }
        }
        return result;
    }

    //根据日期设置对应的预约信息
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        Date orderDate = orderSetting.getOrderDate();
        //根据日期查询当前日期是否已经进行了预约操作
        long count = orderSettingDao.findCountByOrderDate(orderDate);
        if(count>0){
            //说明当前日期已经进行过预约设置，需要执行的是更新操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            //当前日期没有预约，需要进行插入操作
            orderSettingDao.add(orderSetting);
        }
    }
}
