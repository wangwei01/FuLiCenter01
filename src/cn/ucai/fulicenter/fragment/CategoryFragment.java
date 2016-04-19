package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.CategoryAdaptar;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {


    ExpandableListView mexpandableListView;
    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mChildList;
    CategoryAdaptar mcategoryAdapter;
    FuLiCenterMainActivity mContext;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View inflate = inflater.inflate(R.layout.fragment_category, container, false);
        initData();
        setListener();
        initView(inflate);
        return inflate;
    }

    private void setListener() {

    }

    private void initData() {

        try {
            final String path = new ApiParams()
                    .getRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mContext.executeRequest(new GsonRequest<CategoryGroupBean[]>(path, CategoryGroupBean[].class, responDownLoadcateGroupbeanListener(),mContext.errorListener()));
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Response.Listener<CategoryGroupBean[]> responDownLoadcateGroupbeanListener() {
        return new Response.Listener<CategoryGroupBean[]>() {
            @Override
            public void onResponse(CategoryGroupBean[] categoryGroupBeen) {
                if (categoryGroupBeen != null) {
                    ArrayList<CategoryGroupBean> categoryGrouplist = Utils.array2List(categoryGroupBeen);
                    mGroupList = categoryGrouplist;
                        for(int i=0;i<categoryGrouplist.size();i++) {
                            CategoryGroupBean groupBean = categoryGrouplist.get(i);
                            int parentid=groupBean.getId();
                            try {
                                final String path = new ApiParams()
                                        .with(I.PAGE_ID, I.PAGE_ID_DEFAULT + "").with(I.PAGE_SIZE, I.PAGE_SIZE_DEFAULT + "")
                                        .with(I.CategoryChild.PARENT_ID, parentid + "")
                                        .getRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mContext.executeRequest(new GsonRequest<CategoryChildBean[]>(path,CategoryChildBean[].class,responseChilBeanListener(),mContext.errorListener()));
                                    }
                                }).start();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                }
            }
        };
    }

    private Response.Listener<CategoryChildBean[]> responseChilBeanListener() {
        return new Response.Listener<CategoryChildBean[]>() {
            @Override
            public void onResponse(CategoryChildBean[] categoryChildBeen) {
                ArrayList<CategoryChildBean> categoryChildBeen1 = Utils.array2List(categoryChildBeen);
                mChildList.add(categoryChildBeen1);
                if (mGroupList.size() == mChildList.size()) {
                    mcategoryAdapter.addiTems(mGroupList, mChildList);
                }
            }
        };
    }

    private void initView(View layout) {
        mexpandableListView = (ExpandableListView)layout.findViewById(R.id.elv);
        mexpandableListView.setGroupIndicator(null);
        mChildList = new ArrayList<>();
        mGroupList = new ArrayList<>();
        mcategoryAdapter = new CategoryAdaptar(mContext, mGroupList, mChildList);
        mexpandableListView.setAdapter(mcategoryAdapter);
    }


}
