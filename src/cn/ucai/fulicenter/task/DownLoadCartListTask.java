package cn.ucai.fulicenter.task;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/4/21.
 */
public class DownLoadCartListTask extends BaseActivity {
    Context mContext;
    String username;
    int pageId;
    int pageSize;
    String path;

    public DownLoadCartListTask(Context mContext, String username, int pageId, int pageSize) {
        this.mContext = mContext;
        this.username = username;
        this.pageId = pageId;
        this.pageSize = pageSize;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, username)
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, pageSize + "")
                    .getRequestUrl(I.REQUEST_FIND_CARTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {

        executeRequest(new GsonRequest<CartBean[]>(path,CartBean[].class,responseListener(),errorListener()));
    }

    private Response.Listener<CartBean[]> responseListener() {
        return new Response.Listener<CartBean[]>() {
            @Override
            public void onResponse(CartBean[] cartArray) {
                Log.e("main", "responseListener" + cartArray);
                if(cartArray==null){
                    return;
                }
                ArrayList<CartBean> cartbeanlist= Utils.array2List(cartArray);
                ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();

                cartList.clear();
                cartList.addAll(cartbeanlist);


                for (CartBean cartBean : cartbeanlist) {
                    int goodsId = cartBean.getGoodsId();
                    try {
                        String path=new ApiParams()
                                .with(I.Cart.GOODS_ID,goodsId+"")
                                .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                        executeRequest(new GsonRequest<GoodDetailsBean[]>(path,GoodDetailsBean[].class,responseGoodDetailsBeanListener(),errorListener()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private Response.Listener<GoodDetailsBean[]> responseGoodDetailsBeanListener() {
        return new Response.Listener<GoodDetailsBean[]>() {
            @Override
            public void onResponse(GoodDetailsBean[] goodDetailsBeen) {

            }
        };
    }
}
