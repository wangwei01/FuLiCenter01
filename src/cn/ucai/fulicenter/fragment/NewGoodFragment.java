package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.GoodAdaptar;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 */
public class NewGoodFragment extends Fragment {

    FuLiCenterMainActivity mContext;
    ArrayList<NewGoodBean> mGoodList;
    private int Pageid=0;
    private int action = I.ACTION_DOWNLOAD;

    SwipeRefreshLayout mSwipeRefreshlayout;
    RecyclerView mRecylerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;
    GoodAdaptar mGoodAdaptar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View inflate = inflater.inflate(R.layout.fragment_new_good, container, false);
        mGoodList = new ArrayList<>();
        initView(inflate);
        initDate();
        return inflate;
    }

    private void initDate() {
        try {
            String path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID,I.CAT_ID+"")
                    .with(I.PAGE_ID, Pageid + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path,NewGoodBean[].class,responseDownLoadNewGoodListener(),mContext.errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<NewGoodBean[]> responseDownLoadNewGoodListener() {
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                mSwipeRefreshlayout.setRefreshing(false);
                mtvHint.setVisibility(View.GONE);
                ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                    mGoodAdaptar.initItems(list);
                } else if(action==I.ACTION_PULL_UP){
                    mGoodAdaptar.additems(list);
                }
            }
        };
    }

    private void initView(View layout) {
        mSwipeRefreshlayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_new_good);
        mSwipeRefreshlayout.setColorSchemeColors(
                R.color.google_blue,R.color.google_green
                ,R.color.google_red,R.color.google_yellow
                );
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecylerView = (RecyclerView) layout.findViewById(R.id.rv_nowgood);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(mGridLayoutManager);
        mGoodAdaptar = new GoodAdaptar(mContext, mGoodList);
        mRecylerView.setAdapter(mGoodAdaptar);
    }

}
