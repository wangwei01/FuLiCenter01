package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * Created by sks on 2016/4/19.
 */
public class CollectActivity extends BaseActivity {

    CollectActivity mContext;
    ArrayList<CollectBean> mGoodList = new ArrayList<>();
    private int Pageid = 0;
    private int action = I.ACTION_DOWNLOAD;

    SwipeRefreshLayout mSwipeRefreshlayout;
    RecyclerView mRecylerView;
    TextView mtvHint;
    GridLayoutManager mGridLayoutManager;
    CollectAdapter mCollectAdapter;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_collect_child);
        mContext = this;
        initView();
        setListener();
        initDate();
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition == mCollectAdapter.getItemCount() - 1) {
                    if (mCollectAdapter.isMore()) {
                        mSwipeRefreshlayout.setRefreshing(true);
                        action = I.ACTION_PULL_UP;
                        Pageid += I.PAGE_SIZE_DEFAULT;
                        String path = getPath();
                        mContext.executeRequest(new GsonRequest<CollectBean[]>(path, CollectBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));
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
                mContext.executeRequest(new GsonRequest<CollectBean[]>(path, CollectBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));

            }
        });
    }


    private String getPath() {
        UserBean user = FuLiCenterApplication.getInstance().getUser();
        Log.i("main", "CollectActivity+UserBean" + user.getUserName());
        try {
            String path = new ApiParams()
                    .with(I.User.USER_NAME, user.getUserName())
                    .with(I.PAGE_ID, Pageid + "")
                    .with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                    .getRequestUrl(I.REQUEST_FIND_COLLECTS);
            Log.i("main", "CollectActivity+UserBean+path=" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initDate() {
        String path = getPath();
        mContext.executeRequest(new GsonRequest<CollectBean[]>(path, CollectBean[].class, responseDownLoadNewGoodListener(), mContext.errorListener()));
    }

    private Response.Listener<CollectBean[]> responseDownLoadNewGoodListener() {
        return new Response.Listener<CollectBean[]>() {
            @Override
            public void onResponse(CollectBean[] newGoodBeen) {

                if (newGoodBeen != null) {
                    mCollectAdapter.setMore(true);
                    mSwipeRefreshlayout.setRefreshing(false);
                    mtvHint.setVisibility(View.GONE);
                    mCollectAdapter.setFoottext("刷新数据");
                    ArrayList<CollectBean> list = Utils.array2List(newGoodBeen);

                    for(int i=0;i<list.size();i++) {
                        Log.i("main", "responseDownLoadNewGoodListener" + list.get(i).toString());
                    }

                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mCollectAdapter.initItems(list);
                    } else if (action == I.ACTION_PULL_UP) {
                        mCollectAdapter.additems(list);
                    }
                    if (newGoodBeen.length < I.PAGE_SIZE_DEFAULT) {
                        mCollectAdapter.setMore(false);
                        mCollectAdapter.setFoottext("没有更多数据了");
                    }
                }
            }
        };
    }

    private void initView() {

        mSwipeRefreshlayout = (SwipeRefreshLayout) findViewById(R.id.srl_collect_child);
        mSwipeRefreshlayout.setColorSchemeColors(
                R.color.google_blue, R.color.google_green
                , R.color.google_red, R.color.google_yellow
        );
        mtvHint = (TextView) findViewById(R.id.tv_refresh_hint);

       // String boutiqueChildName = getIntent().getStringExtra(I.Boutique.NAME);
        DisplayUtils.initBackWithTitle(mContext,"收藏商品");


        mRecylerView = (RecyclerView)findViewById(R.id.rv_collect_child);
        mRecylerView.setHasFixedSize(true);


        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecylerView.setLayoutManager(mGridLayoutManager);

        mCollectAdapter = new CollectAdapter(mContext, mGoodList);
        mRecylerView.setAdapter(mCollectAdapter);

        mCollectAdapter.notifyDataSetChanged();
    }
}


