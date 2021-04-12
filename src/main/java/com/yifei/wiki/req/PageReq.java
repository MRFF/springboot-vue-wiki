package com.yifei.wiki.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * 分页请求需要前端提供页码和每页条目数
 * 之后所有涉及到分页的请求都需要这两个参数，因此我们可以将PageReq当作父类
 * 凡是涉及分页的请求，都可以再去继承它
 */
public class PageReq {
    @NotNull(message = "【页码】不能为空")
    private int page;

    @Max(value = 1000, message = "【每页条数】不能超过1000")
    @NotNull(message = "【每页条数】不能为空")
    private int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageReq{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}