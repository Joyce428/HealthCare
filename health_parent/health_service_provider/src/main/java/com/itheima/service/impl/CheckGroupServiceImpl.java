package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;

    //dont forget to connect the checkgroup with checkitems
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //add checkgroup info to table
        checkGroupDao.add(checkGroup);
        //edit t_checkgroup_checkitem table
        Integer checkGroupId = checkGroup.getId();
        setCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }


    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<CheckGroup> page=checkGroupDao.findByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }


    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }


    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }


    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        checkGroupDao.edit(checkGroup);
        //edit checkgroup basic information

        //clear current checkitems of this group
        checkGroupDao.deleteAssociation(checkGroup.getId());
        //rebuild association between the group and the updated checkitems of this group
        Integer checkGroupId = checkGroup.getId();
        setCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }

    //build the association (many-to-many relation) between checkgroup and its chekitems
    public void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkitemIds){
        if(checkitemIds!=null && checkitemIds.length>0){
            for (Integer itemId : checkitemIds){
                Map<String, Integer> map = new HashMap<>();
                map.put("checkgroupId", checkGroupId);
                map.put("checkitemId", itemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }


}
