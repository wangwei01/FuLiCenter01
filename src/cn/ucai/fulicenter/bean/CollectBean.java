package cn.ucai.fulicenter.bean;

import java.io.Serializable;

/**
 * Created by sks on 2016/4/15.
 */
public class CollectBean  implements Serializable {
    private int id;

    private String userName;

    private int goodsId;

    private String goodsName;

    private String goodsEnglishName;

    private String goodsThumb;

    private String goodsImg;

    private long addTime;

    public CollectBean() {
    }

    public CollectBean(String userName, int goodsId, String goodsName, String goodsEnglishName, String goodsThumb, String goodsImg, long addTime) {
        this.userName = userName;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.goodsEnglishName = goodsEnglishName;
        this.goodsThumb = goodsThumb;
        this.goodsImg = goodsImg;
        this.addTime = addTime;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getUserName(){
        return this.userName;
    }
    public void setGoodsId(int goodsId){
        this.goodsId = goodsId;
    }
    public int getGoodsId(){
        return this.goodsId;
    }
    public void setGoodsName(String goodsName){
        this.goodsName = goodsName;
    }
    public String getGoodsName(){
        return this.goodsName;
    }
    public void setGoodsEnglishName(String goodsEnglishName){
        this.goodsEnglishName = goodsEnglishName;
    }
    public String getGoodsEnglishName(){
        return this.goodsEnglishName;
    }
    public void setGoodsThumb(String goodsThumb){
        this.goodsThumb = goodsThumb;
    }
    public String getGoodsThumb(){
        return this.goodsThumb;
    }
    public void setGoodsImg(String goodsImg){
        this.goodsImg = goodsImg;
    }
    public String getGoodsImg(){
        return this.goodsImg;
    }
    public void setAddTime(long addTime){
        this.addTime = addTime;
    }
    public long getAddTime(){
        return this.addTime;
    }

    @Override
    public String toString() {
        return "CollectBean{" +
                "id=" + id +
                ", userName=" + userName +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName +
                ", goodsEnglishName='" + goodsEnglishName +
                ", goodsThumb='" + goodsThumb +
                ", goodsImg='" + goodsImg +
                ", addTime=" + addTime +
                '}';
    }
}
