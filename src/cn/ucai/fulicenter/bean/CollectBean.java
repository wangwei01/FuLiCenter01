package cn.ucai.fulicenter.bean;

import java.io.Serializable;

/**
 * Created by sks on 2016/4/15.
 */
public class CollectBean  implements Serializable {
    private int id;

    private int userName;

    private int goodsId;

    private String goodsName;

    private String goodsEnglishName;

    private String goodsThumb;

    private String goodsImg;

    private int addTime;

    public CollectBean() {
    }

    public CollectBean(int userName, int goodsId, String goodsName, String goodsEnglishName, String goodsThumb, String goodsImg, int addTime) {
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
    public void setUserName(int userName){
        this.userName = userName;
    }
    public int getUserName(){
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
    public void setAddTime(int addTime){
        this.addTime = addTime;
    }
    public int getAddTime(){
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
