package com.ywanhzy.demo.entity;

import java.io.Serializable;
import java.util.List;

public class MenuEntity implements Serializable {
    private String id;
    private String title;
    private String ico;
    private String sort;
    private String num = "0";
    private boolean select = false;
    private List<MenuEntity> childs;

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public List<MenuEntity> getChilds() {
        return childs;
    }

    public void setChilds(List<MenuEntity> childs) {
        this.childs = childs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }


}
