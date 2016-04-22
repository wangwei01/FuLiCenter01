package cn.ucai.fulicenter.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    FuLiCenterMainActivity mContext;
    ArrayList<CartBean> mCartList = new ArrayList<>();

    RecyclerView mRecylerView;
    LinearLayoutManager mLinearLayoutManager;
    CartAdapter mCartAdaptar;

    TextView mtvSumPrice;
    TextView mtvSavePrice;
    TextView mtvNothing;

    CartChangedReceiver mCartChangedReceiver;
    public CartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View inflate = inflater.inflate(R.layout.fragment_cart, container, false);
        initView(inflate);
        registerCartChangedReceiver();
        refresh();
        return inflate;
    }

    private void registerCartChangedReceiver() {
        mCartChangedReceiver = new CartChangedReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        mContext.registerReceiver(mCartChangedReceiver,filter);
    }

    private void refresh() {
        ArrayList<CartBean> list = FuLiCenterApplication.getInstance().getCartList();
        for (CartBean cartBean : list) {
            Log.i("main", "CartFragment" + cartBean.toString());
        }
        mCartList.clear();
        mCartList.addAll(list);
        mCartAdaptar.notifyDataSetChanged();
        sumPrice();

        if (mCartList != null && mCartList.size() > 0) {
            mtvNothing.setVisibility(View.GONE);
        } else {
            mtvNothing.setVisibility(View.VISIBLE);
        }
    }


    private void initView(View layout) {
        mtvSumPrice = (TextView) layout.findViewById(R.id.tv_cart_pricrsum);
        mtvSavePrice = (TextView) layout.findViewById(R.id.tv_save);
        mtvNothing = (TextView) layout.findViewById(R.id.tv_noting);


        mRecylerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecylerView.setLayoutManager(mLinearLayoutManager);

        mCartAdaptar = new CartAdapter(mContext, mCartList);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setAdapter(mCartAdaptar);
        mtvNothing.setVisibility(View.VISIBLE);

    }



    protected void sumPrice() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        int sumRankPrice=0;
        int sunCurrentPrice = 0;
        for(int i=0;i<cartList.size();i++) {
            CartBean cart = cartList.get(i);
            GoodDetailsBean goods = cart.getGoods();
            if (cart.isChecked()) {
                for(int k=0;k<cart.getCount();k++) {
                    if(goods!=null){
                        int rankPrice = concertPrice(goods.getRankPrice());
                        int currentPrice = concertPrice(goods.getCurrencyPrice());
                        sumRankPrice += rankPrice;
                        sunCurrentPrice += currentPrice;
                    }
                }

            }
        }
        int sumSacePrice = sunCurrentPrice - sumRankPrice;
        mtvSavePrice.setText("节省:￥"+sumSacePrice);
        mtvSumPrice.setText("合计:￥"+sumRankPrice);
    }


    private int concertPrice(String strprice) {
        strprice = strprice.substring(strprice.indexOf("￥") + 1);
        int price = Integer.parseInt(strprice);
        return price;
    }

    class CartChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCartChangedReceiver != null) {
            mContext.unregisterReceiver(mCartChangedReceiver);
        }
    }
}
