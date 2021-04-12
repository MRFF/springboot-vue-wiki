package com.yifei.wiki.controller;

import com.yifei.wiki.domain.Ebook;
import com.yifei.wiki.req.EbookQueryReq;
import com.yifei.wiki.req.EbookSaveReq;
import com.yifei.wiki.resp.CommonResp;
import com.yifei.wiki.resp.EbookQueryResp;
import com.yifei.wiki.resp.PageResp;
import com.yifei.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookConroller {
    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp<List<Ebook>> list(){
        CommonResp<List<Ebook>> resp = new CommonResp<>();
        List<Ebook> ebooks = ebookService.list();
        resp.setContent(ebooks);
        return resp;
    }

    @GetMapping("/get")
    public CommonResp get(EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> pageResp = ebookService.get(req);
        resp.setContent(pageResp);
        return resp;
    }

    // 如果POST请求提交的是json形式，而非form，则必须在方法前添加@RequestBody，否则接收不到json
    public CommonResp save(@RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp();
        ebookService.save(req);
        resp.setSuccess(true);
        return resp;
    }
}
