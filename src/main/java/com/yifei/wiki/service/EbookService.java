package com.yifei.wiki.service;

import com.yifei.wiki.domain.Ebook;
import com.yifei.wiki.domain.EbookExample;
import com.yifei.wiki.mapper.EbookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbookService {
    @Autowired
    private EbookMapper ebookMapper;

    public List<Ebook> list(){
        return ebookMapper.selectByExample(null);
    }

    public List<Ebook> get(String name){
        EbookExample example = new EbookExample();
        EbookExample.Criteria criteria = example.createCriteria();
        criteria.andNameLike("%" + name + "%");
        List<Ebook> ebooks = ebookMapper.selectByExample(example);
        return ebooks;
    }
}
