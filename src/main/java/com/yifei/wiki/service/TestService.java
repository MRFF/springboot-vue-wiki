package com.yifei.wiki.service;

import com.yifei.wiki.domain.Test;
import com.yifei.wiki.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {
    @Autowired
    private TestMapper testMapper;

    public List<Test> findAll(){
        return testMapper.findAll();
    }
}
