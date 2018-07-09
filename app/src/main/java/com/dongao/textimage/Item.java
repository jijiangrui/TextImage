package com.dongao.textimage;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * @author jjr
 * @date 2018/6/19
 */
public class Item implements MultiItemEntity{

    private int type;
    private String title;
    private String content;
    private List<String> imgs;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    @Override
    public int getItemType() {
        return getType();
    }
}
