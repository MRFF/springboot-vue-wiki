package com.yifei.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yifei.wiki.domain.Category;
import com.yifei.wiki.domain.CategoryExample;
import com.yifei.wiki.mapper.CategoryMapper;
import com.yifei.wiki.req.CategoryQueryReq;
import com.yifei.wiki.req.CategorySaveReq;
import com.yifei.wiki.resp.CategoryQueryResp;
import com.yifei.wiki.resp.PageResp;
import com.yifei.wiki.util.CopyUtil;
import com.yifei.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SnowFlake snowFlake;

    public List<CategoryQueryResp> all(){
        CategoryExample example = new CategoryExample();
        example.setOrderByClause("sort asc");
        List<Category> categorys = categoryMapper.selectByExample(example);
        List<CategoryQueryResp> categoryQueryResps = CopyUtil.copyList(categorys, CategoryQueryResp.class);
        return categoryQueryResps;
    }


    // 参数名一致时，会自动找到类中的属性映射
    public PageResp<CategoryQueryResp> get(CategoryQueryReq req){
        CategoryExample example = new CategoryExample();
        example.setOrderByClause("sort asc");
        CategoryExample.Criteria criteria = example.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName()))
            criteria.andNameLike("%" + req.getName() + "%");

        // 使用分页插件，实现分页。每次设置分页，都只会生效一次，因此注意要与分页查询语句紧邻，中间不能有其它查询
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categorys = categoryMapper.selectByExample(example);


        // 获取分页后的信息，可以获取结果总条数
        PageInfo<Category> pageInfo = new PageInfo<>(categorys);
        PageResp<CategoryQueryResp> pageResp = new PageResp<>();

        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setRecords(CopyUtil.copyList(categorys, CategoryQueryResp.class));

        return pageResp;
    }

    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if(ObjectUtils.isEmpty(req.getId())){
            category.setId(snowFlake.nextId());
            // 新增
            categoryMapper.insert(category);
        }
        else{
            // 修改
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
