package com.example.applibrary.entity;

import java.util.List;

public class GoodDetail {

    private StoreInfo storeInfo;
    //    private AttrInfo productValue;
    //无法解析
//        private Map<String, Object> productValue;
    private List<ProductAttr> productAttr;
    private String priceName;
    private ReplyInfo reply;
    private int replyCount;
    private String replyChance;
    private int mer_id;
    private String details_url;
    private int notFreight;

    public StoreInfo getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(StoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public List<ProductAttr> getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(List<ProductAttr> productAttr) {
        this.productAttr = productAttr;
    }

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    public ReplyInfo getReply() {
        return reply;
    }

    public void setReply(ReplyInfo reply) {
        this.reply = reply;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getReplyChance() {
        return replyChance;
    }

    public void setReplyChance(String replyChance) {
        this.replyChance = replyChance;
    }

    public int getMer_id() {
        return mer_id;
    }

    public void setMer_id(int mer_id) {
        this.mer_id = mer_id;
    }

    public String getDetails_url() {
        return details_url;
    }

    public void setDetails_url(String details_url) {
        this.details_url = details_url;
    }

    public int getNotFreight() {
        return notFreight;
    }

    public void setNotFreight(int notFreight) {
        this.notFreight = notFreight;
    }
}
