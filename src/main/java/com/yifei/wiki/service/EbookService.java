package com.yifei.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yifei.wiki.domain.Ebook;
import com.yifei.wiki.domain.EbookExample;
import com.yifei.wiki.mapper.EbookMapper;
import com.yifei.wiki.req.EbookQueryReq;
import com.yifei.wiki.req.EbookSaveReq;
import com.yifei.wiki.resp.EbookQueryResp;
import com.yifei.wiki.resp.PageResp;
import com.yifei.wiki.util.CopyUtil;
import com.yifei.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class EbookService {
    @Autowired
    private EbookMapper ebookMapper;
    @Autowired
    private SnowFlake snowFlake;

    public List<Ebook> list(){
        return ebookMapper.selectByExample(null);
    }

    // 参数名一致时，会自动找到类中的属性映射
    public PageResp<EbookQueryResp> get(EbookQueryReq req){
        EbookExample example = new EbookExample();
        EbookExample.Criteria criteria = example.createCriteria();
        // 动态SQL，如果前端没有传参数，就返回所有的值
        if(!ObjectUtils.isEmpty(req.getName()))
            criteria.andNameLike("%" + req.getName() + "%");

        // 使用分页插件，实现分页。每次设置分页，都只会生效一次，因此注意要与分页查询语句紧邻，中间不能有其它查询
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebooks = ebookMapper.selectByExample(example);

        // 获取分页后的信息，可以获取结果总条数
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooks);
        PageResp<EbookQueryResp> pageResp = new PageResp<>();

        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setRecords(CopyUtil.copyList(ebooks, EbookQueryResp.class));
//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebooks) {
//            EbookResp ebookResp = new EbookResp();
//            BeanUtils.copyProperties(ebook, ebookResp);
//            respList.add(ebookResp);
//        }

        return pageResp;
    }

    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if(ObjectUtils.isEmpty(req.getId())){
            ebook.setId(snowFlake.nextId());
            // 新增
            ebookMapper.insert(ebook);
        }
        else{
            // 修改
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }
}
