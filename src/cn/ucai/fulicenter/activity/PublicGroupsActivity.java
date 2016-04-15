/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.task.DownLoadPublicGroupTask;
import cn.ucai.fulicenter.utils.UserUtils;

public class PublicGroupsActivity extends BaseActivity {
	private ProgressBar pb;
	private ListView listView;
	private GroupsAdapter adapter;
	
	private ArrayList<GroupBean> groupsList;
	private boolean isLoading;
	private boolean isFirstLoading = true;
	private boolean hasMoreData = true;
	private String cursor;
	private int pageId = 0;
	private final int pagesize = 15;
    private LinearLayout footLoadingLayout;
    private ProgressBar footLoadingPB;
    private TextView footLoadingText;
    private Button searchBtn;

    PublicGroupsActivity mContext;
    DownLoadPublicGroupsReceiver  mDownLoadPublicGroupsReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        resgisterDownLoadPublicGroupsReceiver();
		setContentView(R.layout.activity_public_groups);
        mContext = this;
        initView();
		groupsList = new ArrayList<GroupBean>();
        //获取及显示数据
        setListener();
	}

    private void resgisterDownLoadPublicGroupsReceiver() {
        mDownLoadPublicGroupsReceiver = new DownLoadPublicGroupsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("download_public_group");
        registerReceiver(mDownLoadPublicGroupsReceiver, filter);
    }

    class DownLoadPublicGroupsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAndShowData();
        }
    }


    private void setListener() {
        //设置item点击事件
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(PublicGroupsActivity.this, GroupSimpleDetailActivity.class).
                        putExtra("groupinfo", adapter.getItem(position)));
            }
        });
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    if(listView.getCount() != 0){
                        int lasPos = view.getLastVisiblePosition();
                        if(hasMoreData && !isLoading && lasPos == listView.getCount()-1){
                            pageId++;
                            UserBean user = FuLiCenterApplication.getInstance().getUser();
                            new DownLoadPublicGroupTask(mContext, user.getUserName(), pageId, pagesize).execute();
                            loadAndShowData();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initView() {
        pb = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.list);
        searchBtn = (Button) findViewById(R.id.btn_search);

        View footView = getLayoutInflater().inflate(R.layout.listview_footer_view, null);
        footLoadingLayout = (LinearLayout) footView.findViewById(R.id.loading_layout);
        footLoadingPB = (ProgressBar)footView.findViewById(R.id.loading_bar);
        footLoadingText = (TextView) footView.findViewById(R.id.loading_text);
        listView.addFooterView(footView, null, false);
        footLoadingLayout.setVisibility(View.GONE);
    }

    /**
	 * 搜索
	 * @param view
	 */
	public void search(View view){
	    startActivity(new Intent(this, PublicGroupsSeachActivity.class));
	}
	
	private void loadAndShowData(){
	    new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    final ArrayList<GroupBean> publicGroupList = FuLiCenterApplication.getInstance().getPublicGroupList();
                    //final EMCursorResult<EMGroupInfo> result = EMGroupManager.getInstance().getPublicGroupsFromServer(pagesize, cursor);
                    //获取group list
                   // final List<EMGroupInfo> returnGroups = result.getData();

                    runOnUiThread(new Runnable() {

                        public void run() {
                            searchBtn.setVisibility(View.VISIBLE);
                            for (GroupBean groupBean : publicGroupList) {
                                if (!groupsList.contains(groupBean)) {
                                    groupsList.add(groupBean);
                                }
                            }

                            for (GroupBean groupBean : groupsList) {
                                Log.i("main", "groupbean" + groupBean.toString());
                            }
                        //    groupsList.addAll(publicGroupList);
                            if(publicGroupList.size() != 0){
                                //获取cursor
                               // cursor = result.getCursor();
                                if(publicGroupList.size() == pagesize)
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                            }
                            if(isFirstLoading){
                                pb.setVisibility(View.INVISIBLE);
                                isFirstLoading = false;
                                //设置adapter
                                adapter = new GroupsAdapter(PublicGroupsActivity.this, 1, groupsList);
                                listView.setAdapter(adapter);
                            }else{
                                if(publicGroupList.size() < (pageId+1)*pagesize){
                                    hasMoreData = false;
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                                    footLoadingPB.setVisibility(View.GONE);
                                    footLoadingText.setText("No more data");
                                }
                                adapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            isLoading = false;
                            pb.setVisibility(View.INVISIBLE);
                            footLoadingLayout.setVisibility(View.GONE);
                            Toast.makeText(PublicGroupsActivity.this, "加载数据失败，请检查网络或稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownLoadPublicGroupsReceiver != null) {
            unregisterReceiver(mDownLoadPublicGroupsReceiver);
        }
    }

    /**
	 * adapter
	 *
	 */
	private class GroupsAdapter extends BaseAdapter {

		private LayoutInflater inflater;
        ArrayList<GroupBean> mGroupsList;
        Context mContext;

		public GroupsAdapter(Context context, int res,  ArrayList<GroupBean> list) {
			this.inflater = LayoutInflater.from(context);
            this.mContext = context;
            mGroupsList = list;
        }

        @Override
        public int getCount() {
            return mGroupsList == null ? 0 : mGroupsList.size();
        }

        @Override
        public GroupBean getItem(int position) {
            return mGroupsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_group, null);
			}


            ((TextView) convertView.findViewById(R.id.name)).setText(getItem(position).getName());
            UserUtils.setGroupBeanAvatar(getItem(position),(NetworkImageView) convertView.findViewById(R.id.avatar));
			return convertView;
		}
	}
	
	public void back(View view){
		finish();
	}
}
