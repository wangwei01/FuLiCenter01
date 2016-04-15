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
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.util.EMLog;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.adapter.GroupAdapter;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.bean.GroupBean;

public class GroupsActivity extends BaseActivity {
	public static final String TAG = "GroupsActivity";
	private ListView groupListView;
	protected ArrayList<GroupBean> grouplist;
	private GroupAdapter groupAdapter;
	private InputMethodManager inputMethodManager;
	public static GroupsActivity instance;
	private SyncListener syncListener;
	private View progressBar;
	private SwipeRefreshLayout swipeRefreshLayout;
	Handler handler = new Handler();


	public static final int REQUEST_NEW_GROUP=100;
	public static final int REQUEST_NEW_PUBLIC_GROUP=1;
	public static final int REQUEST_ENTITY_CHATACTIVITY=2;
	UpdateGroupListReceiver mUpdateGroupListReceiver;

	class SyncListener implements HXSDKHelper.HXSyncListener {
		@Override
		public void onSyncSucess(final boolean success) {
			EMLog.d(TAG, "onSyncGroupsFinish success:" + success);
			runOnUiThread(new Runnable() {
				public void run() {
					swipeRefreshLayout.setRefreshing(false);
					if (success) {
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								refresh();
								progressBar.setVisibility(View.GONE);
							}
						}, 1000);
					} else {
						if (!GroupsActivity.this.isFinishing()) {
							String s1 = getResources()
									.getString(
											R.string.Failed_to_get_group_chat_information);
							Toast.makeText(GroupsActivity.this, s1, Toast.LENGTH_LONG).show();
							progressBar.setVisibility(View.GONE);
						}
					}
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_groups);
		initBroadCastReceiver();
		instance = this;
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		grouplist = FuLiCenterApplication.getInstance().getGroupList();
		groupListView = (ListView) findViewById(R.id.list);

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
		                android.R.color.holo_orange_light, android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
			    MainActivity.asyncFetchGroupsFromServer();
			}
		});

		groupAdapter = new GroupAdapter(this,grouplist);
		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 1) {
					// 新建群聊
					startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), REQUEST_NEW_GROUP);
				} else if (position == 2) {
					// 添加公开群
					startActivityForResult(new Intent(GroupsActivity.this, PublicGroupsActivity.class), REQUEST_NEW_PUBLIC_GROUP);
				} else {
					// 进入群聊
					Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
					// it is group chat
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					intent.putExtra("groupId", groupAdapter.getItem(position).getGroupId());
					startActivityForResult(intent, REQUEST_ENTITY_CHATACTIVITY);
				}
			}

		});
		groupListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});

		progressBar = (View)findViewById(R.id.progress_bar);

		syncListener = new SyncListener();
		HXSDKHelper.getInstance().addSyncGroupListener(syncListener);

		if (!HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}

		refresh();
	}



	/**
	 * 进入公开群聊列表
	 */
	public void onPublicGroups(View view) {
		startActivity(new Intent(this, PublicGroupsActivity.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_NEW_GROUP) {
			GroupBean groupbean= (GroupBean) data.getSerializableExtra("group");
			groupAdapter.addItem(groupbean);
		}
		if (requestCode == REQUEST_NEW_PUBLIC_GROUP) {

		}
		if (requestCode == REQUEST_ENTITY_CHATACTIVITY) {

		}

	}

	private void initBroadCastReceiver() {
		mUpdateGroupListReceiver = new UpdateGroupListReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("download_groups");
		registerReceiver(mUpdateGroupListReceiver, filter);
	}

	class UpdateGroupListReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (groupAdapter.getCount() == 3 && intent.getAction().equals("download_groups")) {
				ArrayList<GroupBean> groupList = FuLiCenterApplication.getInstance().getGroupList();
				if (!groupList.containsAll(groupList)) {
					groupAdapter.initList(groupList);
				}
			}
		}
	}



	@Override
	public void onResume() {
		super.onResume();
		grouplist = FuLiCenterApplication.getInstance().getGroupList();
		groupAdapter = new GroupAdapter(this,grouplist);
		groupListView.setAdapter(groupAdapter);
		groupAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		if (mUpdateGroupListReceiver != null) {
			unregisterReceiver(mUpdateGroupListReceiver);
		}
		if (syncListener != null) {
			HXSDKHelper.getInstance().removeSyncGroupListener(syncListener);
			syncListener = null;
		}
		super.onDestroy();
		instance = null;
	}

	public void refresh() {
		if (groupListView != null && groupAdapter != null) {
			grouplist = FuLiCenterApplication.getInstance().getGroupList();
			groupAdapter = new GroupAdapter(GroupsActivity.this,grouplist);
			groupListView.setAdapter(groupAdapter);
			groupAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 返回
	 *
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
}
