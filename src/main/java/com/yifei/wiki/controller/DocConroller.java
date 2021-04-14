package com.yifei.wiki.controller;

import com.yifei.wiki.req.DocQueryReq;
import com.yifei.wiki.req.DocSaveReq;
import com.yifei.wiki.resp.DocQueryResp;
import com.yifei.wiki.resp.CommonResp;
import com.yifei.wiki.resp.PageResp;
import com.yifei.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocConroller {
    @Autowired
    private DocService docService;

    @GetMapping("/all")
    public CommonResp<List<DocQueryResp>> all(){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> docs = docService.all();
        resp.setContent(docs);
        return resp;
    }

    @GetMapping("/get")
    public CommonResp get(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> pageResp = docService.get(req);
        resp.setContent(pageResp);
        return resp;
    }

    // 如果POST请求提交的是json形式，而非form，则必须在方法前添加@RequestBody，否则接收不到json
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idsString}")
    public CommonResp delete(@PathVariable String idsString){
        CommonResp resp = new CommonResp<>();
        List<String> ids = Arrays.asList(idsString.split(","));
        docService.delete(ids);
        return resp;
    }
}
