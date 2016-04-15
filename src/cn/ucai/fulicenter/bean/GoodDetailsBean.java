package cn.ucai.fulicenter.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by sks on 2016/4/15.
 */
public class GoodDetailsBean  implements Serializable {

    /**
     * id : 280
     * goodsId : 7677
     * catId : 291
     * goodsName : 双层分格饭盒绿色
     * goodsEnglishName : Monbento
     * goodsBrief : PP食品级材质，轻巧、易清洗、蠕变性小，不易变形，可置于微波炉加热，可方巾洗碗机清洗。双层色彩可以随意组合，轻巧方便。
     * shopPrice : ￥253
     * currencyPrice : ￥293
     * promotePrice : ￥0
     * rankPrice : ￥293
     * goodsThumb : 201509/thumb_img/7677_thumb_G_1442391216339.png
     * goodsImg : 201509/thumb_img/7677_thumb_G_1442391216339.png
     * addTime : 1442419200000
     * shareUrl : http://m.fulishe.com/item/7677
     */

    private int id;
    private int goodsId;
    private int catId;
    private String goodsName;
    private String goodsEnglishName;
    private String goodsBrief;
    private String shopPrice;
    private String currencyPrice;
    private String promotePrice;
    private String rankPrice;
    private String goodsThumb;
    private String goodsImg;
    private long addTime;
    private String shareUrl;
    @JsonProperty("isPromote")
    private Boolean isPromote;
    private PropertyBean[] properties;


    @JsonIgnore
    public Boolean getPromote() {
        return isPromote;
    }

    public void setIsPromote(Boolean isPromote) {
        this.isPromote = isPromote;
    }

    public PropertyBean[] getProperties() {
        return properties;
    }

    public void setProperties(PropertyBean[] properties) {
        this.properties = properties;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsEnglishName() {
        return goodsEnglishName;
    }

    public void setGoodsEnglishName(String goodsEnglishName) {
        this.goodsEnglishName = goodsEnglishName;
    }

    public String getGoodsBrief() {
        return goodsBrief;
    }

    public void setGoodsBrief(String goodsBrief) {
        this.goodsBrief = goodsBrief;
    }

    public String getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(String shopPrice) {
        this.shopPrice = shopPrice;
    }

    public String getCurrencyPrice() {
        return currencyPrice;
    }

    public void setCurrencyPrice(String currencyPrice) {
        this.currencyPrice = currencyPrice;
    }

    public String getPromotePrice() {
        return promotePrice;
    }

    public void setPromotePrice(String promotePrice) {
        this.promotePrice = promotePrice;
    }

    public String getRankPrice() {
        return rankPrice;
    }

    public void setRankPrice(String rankPrice) {
        this.rankPrice = rankPrice;
    }

    public String getGoodsThumb() {
        return goodsThumb;
    }

    public void setGoodsThumb(String goodsThumb) {
        this.goodsThumb = goodsThumb;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    @Override
    public String toString() {
        return "GoodDetailsBean{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", catId=" + catId +
                ", goodsName='" + goodsName +
                ", goodsEnglishName='" + goodsEnglishName +
                ", goodsBrief='" + goodsBrief +
                ", shopPrice='" + shopPrice +
                ", currencyPrice='" + currencyPrice +
                ", promotePrice='" + promotePrice +
                ", rankPrice='" + rankPrice +
                ", goodsThumb='" + goodsThumb +
                ", goodsImg='" + goodsImg +
                ", addTime=" + addTime +
                ", shareUrl='" + shareUrl +
                ", properties=" + properties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodDetailsBean that = (GoodDetailsBean) o;

        return goodsId == that.goodsId;

    }

    @Override
    public int hashCode() {
        return goodsId;
    }
}
