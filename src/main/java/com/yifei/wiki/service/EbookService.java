package com.yifei.wiki.service;

import com.yifei.wiki.domain.Ebook;
import com.yifei.wiki.domain.EbookExample;
import com.yifei.wiki.mapper.EbookMapper;
import com.yifei.wiki.req.EbookReq;
import com.yifei.wiki.resp.EbookResp;
import com.yifei.wiki.util.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {
    @Autowired
    private EbookMapper ebookMapper;

    public List<Ebook> list(){
        return ebookMapper.selectByExample(null);
    }
    // 参数名一致时，会自动找到类中的属性映射
    public List<EbookResp> get(EbookReq req){
        EbookExample example = new EbookExample();
        EbookExample.Criteria criteria = example.createCriteria();
        // 动态SQL，如果前端没有传参数，就返回所有的值
        if(!ObjectUtils.isEmpty(req.getName()))
            criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebooks = ebookMapper.selectByExample(example);

//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebooks) {
//            EbookResp ebookResp = new EbookResp();
//            BeanUtils.copyProperties(ebook, ebookResp);
//            respList.add(ebookResp);
//        }
        return CopyUtil.copyList(ebooks, EbookResp.class);
    }
}
