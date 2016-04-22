package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
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

        executeRequest(new GsonRequest<CartBean[]>(path, CartBean[].class, responseListener(), errorListener()));
    }

    private Response.Listener<CartBean[]> responseListener() {
        return new Response.Listener<CartBean[]>() {
            @Override
            public void onResponse(CartBean[] cartArray) {
                Log.e("main", "responseListener" + cartArray);
                if (cartArray == null) {
                    return;
                }
                final ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                ArrayList<CartBean> cartbeanlist = Utils.array2List(cartArray);

                for (int i = 0; i < cartbeanlist.size(); i++) {
                    CartBean cartBean = cartbeanlist.get(i);
                    if (!cartList.contains(cartBean)) {
                        cartList.add(cartBean);
                        try {
                            String path = new ApiParams()
                                    .with(I.Cart.GOODS_ID, cartBean.getGoodsId() + "")
                                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class, new Response.Listener<GoodDetailsBean>() {
                                @Override
                                public void onResponse(GoodDetailsBean goodDetailsBean) {
                                    if (goodDetailsBean != null) {
                                        for (int i = 0; i < cartList.size(); i++) {
                                            if (cartList.get(i).getGoodsId() == goodDetailsBean.getGoodsId()) {
                                                cartList.get(i).setGoods(goodDetailsBean);
                                                Intent intent = new Intent("update_cart");
                                                mContext.sendStickyBroadcast(intent);
                                            }
                                        }
                                    }
                                }
                            }, errorListener()));

                            Intent intent = new Intent("update_cart");
                            mContext.sendStickyBroadcast(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }
}


