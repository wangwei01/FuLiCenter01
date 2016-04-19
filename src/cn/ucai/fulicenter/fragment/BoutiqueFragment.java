package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 */
public class BoutiqueFragment extends Fragment {

    FuLiCenterMainActivity mContext;
    ArrayList<BoutiqueBean> mBoutiqueList = new ArrayList<>();
    private int Pageid = 0;
    private int action = I.ACTION_DOWNLOAD;

    SwipeRefreshLayout mSwipeRefreshlayout;
    RecyclerView mRecylerView;
    TextView mtvHint;
    LinearLayoutManager mLinearLayoutManager;
    BoutiqueAdapter mBoutiqueAdaptar;
    Spinner mSpinner;
    String path;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View inflate = inflater.inflate(R.layout.fragment_boutique, container, false);
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition == mBoutiqueAdaptar.getItemCount() - 1) {
                    if (mBoutiqueAdaptar.isMore()) {
                        mSwipeRefreshlayout.setRefreshing(true);
                        action = I.ACTION_PULL_UP;
                        getPath();
                        mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path, BoutiqueBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshlayout.setEnabled(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
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
                getPath();
                mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path, BoutiqueBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));

            }
        });
    }


    private String getPath() {
        try {
             path = new ApiParams()
                    .getRequestUrl(I.REQUEST_FIND_BOUTIQUES);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initDate() {
        String path = getPath();
        mContext.executeRequest(new GsonRequest<BoutiqueBean[]>(path, BoutiqueBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));
    }

    private Response.Listener<BoutiqueBean[]> responseDownLoadNewGoodListener() {
        return new Response.Listener<BoutiqueBean[]>() {
            @Override
            public void onResponse(BoutiqueBean[] newGoodBeen) {
                if (newGoodBeen != null) {
                    mBoutiqueAdaptar.setMore(true);
                    mSwipeRefreshlayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mBoutiqueAdaptar.setFoottext("刷新数据");
                    ArrayList<BoutiqueBean> list = Utils.array2List(newGoodBeen);
                    Log.i("main", "Response.Listener<BoutiqueBean[]> responseDownLoadNewGoodListener" + list.size());
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mBoutiqueAdaptar.initItems(list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mBoutiqueAdaptar.additems(list);
                    }
                    if (newGoodBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mBoutiqueAdaptar.setMore(false);
                        mBoutiqueAdaptar.setFoottext("没有更多数据了");
                    }
                }
            }
        };
    }

    private void initView(View layout) {
        mSwipeRefreshlayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_boutique);
        mSwipeRefreshlayout.setColorSchemeColors(
                R.color.google_blue, R.color.google_green
                , R.color.google_red, R.color.google_yellow
        );
        mtvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);


        mRecylerView = (RecyclerView) layout.findViewById(R.id.rv_boutique);
        mRecylerView.setHasFixedSize(true);


        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecylerView.setLayoutManager(mLinearLayoutManager);

        mBoutiqueAdaptar = new BoutiqueAdapter(mContext, mBoutiqueList);
        mRecylerView.setAdapter(mBoutiqueAdaptar);
        mBoutiqueAdaptar.notifyDataSetChanged();
    }

}
