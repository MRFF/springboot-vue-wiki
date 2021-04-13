package com.yifei.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yifei.wiki.domain.Doc;
import com.yifei.wiki.domain.DocExample;
import com.yifei.wiki.mapper.DocMapper;
import com.yifei.wiki.req.DocQueryReq;
import com.yifei.wiki.req.DocSaveReq;
import com.yifei.wiki.resp.DocQueryResp;
import com.yifei.wiki.resp.PageResp;
import com.yifei.wiki.util.CopyUtil;
import com.yifei.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class DocService {
    @Autowired
    private DocMapper docMapper;
    @Autowired
    private SnowFlake snowFlake;

    public List<DocQueryResp> all(){
        DocExample example = new DocExample();
        example.setOrderByClause("sort asc");
        List<Doc> docs = docMapper.selectByExample(example);
        List<DocQueryResp> docQueryResps = CopyUtil.copyList(docs, DocQueryResp.class);
        return docQueryResps;
    }


    // 参数名一致时，会自动找到类中的属性映射
    public PageResp<DocQueryResp> get(DocQueryReq req){
        DocExample example = new DocExample();
        example.setOrderByClause("sort asc");
        DocExample.Criteria criteria = example.createCriteria();

        // 使用分页插件，实现分页。每次设置分页，都只会生效一次，因此注意要与分页查询语句紧邻，中间不能有其它查询
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docs = docMapper.selectByExample(example);


        // 获取分页后的信息，可以获取结果总条数
        PageInfo<Doc> pageInfo = new PageInfo<>(docs);
        PageResp<DocQueryResp> pageResp = new PageResp<>();

        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setRecords(CopyUtil.copyList(docs, DocQueryResp.class));

        return pageResp;
    }

    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if(ObjectUtils.isEmpty(req.getId())){
            doc.setId(snowFlake.nextId());
            // 新增
            docMapper.insert(doc);
        }
        else{
            // 修改
            docMapper.updateByPrimaryKey(doc);
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }
}
