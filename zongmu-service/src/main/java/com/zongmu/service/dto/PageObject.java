package com.zongmu.service.dto;

import java.util.ArrayList;
import java.util.List;

public class PageObject<T> extends DTOObject {

    private static final long serialVersionUID = -4689300489061839932L;

    private int totalPage;
    private int pageIndex;
    private int pageCount;
    private List<T> items = new ArrayList<>();

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
