package com.yifei.wiki.util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CopyUtil {
    public static <T> T copy(Object source, Class<T> tClass){
        if(source == null)
            return null;

        T obj = null;
        try {
            obj = tClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BeanUtils.copyProperties(source, obj);
        return obj;
    }

    public static <T> List<T> copyList(List source, Class<T> tClass){
        List<T> target = new ArrayList<>();
        if(!CollectionUtils.isEmpty(source)){
            for(Object o: source){
                T obj = copy(o, tClass);
                target.add(obj);
            }
        }
        return target;
    }
}
