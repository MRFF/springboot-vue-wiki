package com.yifei.wiki.controller;

import com.yifei.wiki.domain.Ebook;
import com.yifei.wiki.resp.CommonResp;
import com.yifei.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}
