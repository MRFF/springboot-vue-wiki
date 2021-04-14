package com.yifei.wiki.req;

public class DocQueryReq extends PageReq{
    private Long ebookId;
    public Long getEbookId() {
        return ebookId;
    }

    public void setEbookId(Long ebookId) {
        this.ebookId = ebookId;
    }



    @Override
    public String toString() {
        return "DocQueryReq{} " + super.toString();
    }
}