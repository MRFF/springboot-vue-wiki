package com.yifei.wiki.mapper;

import com.yifei.wiki.domain.Test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface TestMapper {
    public List<Test> findAll();
}
