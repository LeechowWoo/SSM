package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 检查项服务：
 *
 * @Service中的interfaceClass用来指定当前服务实现的是哪个服务接口
 */
@Service(interfaceClass = CheckItemService.class)//要暴露服务，所有需要使用dubbo的service注解
@Transactional
public class CheckItemServiceImpl implements CheckItemService {
    //注入dao对象
    @Autowired
    private CheckItemDao checkItemDao;
    public void add(CheckItem checkItem){
        checkItemDao.add(checkItem);
    }

    //检查项的分页查询
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //完成分页查询是基于mybatis框架的一个分页助手插件完成
        PageHelper.startPage(currentPage,pageSize);
        //相当于sql语句中的：select * from t_checkitem limit 0,10，并将分页数动态写在limit后

        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        long total = page.getTotal();
        List<CheckItem> rows = page.getResult();
        return new PageResult(total,rows);
    }

    //根据id来删除检查项
    @Override
    public void deleteById(Integer id) {
        //检查项如果已经关联到检查组，业务逻辑上不应该删除，所以应该先检查当前检查项是否关联到检查组
        long count = checkItemDao.findCountByCheckItemId(id);
        if(count>0){
            //说明当前检查项已经被关联到检查组了，不允许删除
            new RuntimeException();//直接抛出异常
        }else{
            checkItemDao.deleteById(id);
        }
    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}
