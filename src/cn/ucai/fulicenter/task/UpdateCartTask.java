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
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by sks on 2016/4/22.
 */
public class UpdateCartTask extends BaseActivity {
    Context mContext;
    CartBean mcart;
    String path;

    public UpdateCartTask(Context mContext, CartBean mcart) {
        this.mContext = mContext;
        this.mcart = mcart;
        initPath();
    }
    private void initPath() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        try {
            if (mcart != null && cartList.contains(mcart)) {
                if (mcart.getCount() <= 0) {
                    path = new ApiParams()
                            .with(I.Cart.ID, mcart.getId() + "")
                            .getRequestUrl(I.REQUEST_DELETE_CART);
                } else {
                    path = new ApiParams().with(I.Cart.IS_CHECKED, mcart.isChecked() + "")
                            .with(I.Cart.ID, mcart.getId() + "")
                            .with(I.Cart.COUNT, mcart.getCount() + "")
                            .getRequestUrl(I.REQUEST_UPDATE_CART);
                }
            } else {
                path = new ApiParams().with(I.Cart.COUNT, mcart.getCount() + "")
                        .with(I.Cart.GOODS_ID, mcart.getGoodsId() + "")
                        .with(I.Cart.IS_CHECKED, mcart.isChecked() + "")
                        .with(I.Cart.USER_NAME, mcart.getUserName() + "")
                        .getRequestUrl(I.REQUEST_ADD_CART);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        if (path == null && path.isEmpty()) {
            return;
        }
        executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseListener(), errorListener()));
    }

    private Response.Listener<MessageBean> responseListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(final MessageBean messageBean) {
                Log.i("main", "Response.Listener<MessageBean> responseListener" + messageBean.toString());
                final ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
                if (messageBean.isSuccess()) {
                    if (mcart.getCount() <= 0) {
                        cartList.remove(mcart);

                        Intent intent = new Intent("update_cart");
                        mContext.sendStickyBroadcast(intent);
                    } else {
                        if (cartList.contains(mcart)) {
                            cartList.set(cartList.indexOf(mcart), mcart);

                            Intent intent = new Intent("update_cart");
                            mContext.sendStickyBroadcast(intent);
                        } else {
                            try {
                                path = new ApiParams().with(I.CategoryGood.GOODS_ID, mcart.getGoodsId() + "")
                                        .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
                                executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class, new Response.Listener<GoodDetailsBean>() {
                                    @Override
                                    public void onResponse(GoodDetailsBean goodDetailsBean) {
                                        if (goodDetailsBean != null) {
                                            mcart.setGoods(goodDetailsBean);
                                            mcart.setId(Integer.parseInt(messageBean.getMsg()));

                                            Intent intent = new Intent("update_cart");
                                            mContext.sendStickyBroadcast(intent);
                                        }
                                    }
                                }, errorListener()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            cartList.add(mcart);
                            Intent intent = new Intent("update_cart");
                            mContext.sendStickyBroadcast(intent);
                        }
                    }

                }
            }
        };
    }
}
