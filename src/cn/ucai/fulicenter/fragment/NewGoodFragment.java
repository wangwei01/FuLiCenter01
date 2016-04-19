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
import android.widget.Spinner;
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
    ArrayList<NewGoodBean> mGoodList = new ArrayList<>();
    private int Pageid = 0;
    private int action = I.ACTION_DOWNLOAD;

    SwipeRefreshLayout mSwipeRefreshlayout;
    RecyclerView mRecylerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;
    GoodAdaptar mGoodAdaptar;
    Spinner mSpinner;

    public NewGoodFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View inflate = inflater.inflate(R.layout.fragment_new_good, container, false);
        initView(inflate);
        setListener();
        initDate();
        return inflate;
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpDownRefreshListener();
    }

    private void setPullUpDownRefreshListener() {
        mRecylerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition == mGoodAdaptar.getItemCount() - 1) {
                    if (mGoodAdaptar.isMore()) {
                        mSwipeRefreshlayout.setRefreshing(true);
                        action = I.ACTION_PULL_UP;
                        Pageid += I.PAGE_SIZE_DEFAULT;
                        String path = getPath();
                        mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path, NewGoodBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshlayout.setEnabled(mGridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mtvHint.setVisibility(View.VISIBLE);
                Pageid = 0;
                action = I.ACTION_PULL_DOWN;
                String path = getPath();
                mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path, NewGoodBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));

            }
        });
    }


    private String getPath() {
        try {
            String path = new ApiParams()
                    .with(I.NewAndBoutiqueGood.CAT_ID, I.CAT_ID + "")
                    .with(I.PAGE_ID, Pageid + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initDate() {
        String path = getPath();
        mContext.executeRequest(new GsonRequest<NewGoodBean[]>(path, NewGoodBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));
    }

    private Response.Listener<NewGoodBean[]> responseDownLoadNewGoodListener() {
        return new Response.Listener<NewGoodBean[]>() {
            @Override
            public void onResponse(NewGoodBean[] newGoodBeen) {
                if (newGoodBeen != null) {
                    mGoodAdaptar.setMore(true);
                    mSwipeRefreshlayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mGoodAdaptar.setFoottext("刷新数据");
                    ArrayList<NewGoodBean> list = Utils.array2List(newGoodBeen);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mGoodAdaptar.initItems(list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mGoodAdaptar.additems(list);
                    }
                    if (newGoodBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mGoodAdaptar.setMore(false);
                        mGoodAdaptar.setFoottext("没有更多数据了");
                    }
                }
            }
        };
    }

    private void initView(View layout) {
        mSwipeRefreshlayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_new_good);
        mSwipeRefreshlayout.setColorSchemeColors(
                R.color.google_blue, R.color.google_green
                , R.color.google_red, R.color.google_yellow
        );
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        mSpinner = (Spinner) layout.findViewById(R.id.spinner);


        mRecylerView = (RecyclerView) layout.findViewById(R.id.rv_nowgood);
        mRecylerView.setHasFixedSize(true);


        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecylerView.setLayoutManager(mGridLayoutManager);

        mGoodAdaptar = new GoodAdaptar(mContext, mGoodList,I.SORT_BY_PRICE_ASC);
        mRecylerView.setAdapter(mGoodAdaptar);
        mGoodAdaptar.notifyDataSetChanged();
    }
}
