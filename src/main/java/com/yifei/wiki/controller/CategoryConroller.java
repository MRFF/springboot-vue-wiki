package com.yifei.wiki.controller;

import com.yifei.wiki.domain.Category;
import com.yifei.wiki.req.CategoryQueryReq;
import com.yifei.wiki.req.CategorySaveReq;
import com.yifei.wiki.resp.CommonResp;
import com.yifei.wiki.resp.CategoryQueryResp;
import com.yifei.wiki.resp.PageResp;
import com.yifei.wiki.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryConroller {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public CommonResp<List<Category>> list(){
        CommonResp<List<Category>> resp = new CommonResp<>();
        List<Category> categorys = categoryService.list();
        resp.setContent(categorys);
        return resp;
    }

    @GetMapping("/get")
    public CommonResp get(@Valid CategoryQueryReq req){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        System.out.println("----CategoryQueryReq name: " + req.getName());
        PageResp<CategoryQueryResp> pageResp = categoryService.get(req);
        resp.setContent(pageResp);
        return resp;
    }

    // 如果POST请求提交的是json形式，而非form，则必须在方法前添加@RequestBody，否则接收不到json
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
