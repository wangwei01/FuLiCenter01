/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.data.RequestManager;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.UserUtils;
import cn.ucai.fulicenter.widget.ExpandGridView;

public class GroupDetailsActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;
    private static final int REQUEST_CODE_ADD_TO_BALCKLIST = 4;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;

    String longClickUsername = null;

    private ExpandGridView userGridview;
    private String groupId;
    private ProgressBar loadingPB;
    private Button exitBtn;
    private Button deleteBtn;
    private EMGroup group;
    GroupBean mGroup;
    private GridAdapter adapter;
    private int referenceWidth;
    private int referenceHeight;
    private ProgressDialog progressDialog;

    private RelativeLayout rl_switch_block_groupmsg;
    /**
     * 屏蔽群消息imageView
     */
    private ImageView iv_switch_block_groupmsg;
    /**
     * 关闭屏蔽群消息imageview
     */
    private ImageView iv_switch_unblock_groupmsg;

    public static GroupDetailsActivity instance;

    String st = "";
    // 清空所有聊天记录
    private RelativeLayout clearAllHistory;
    private RelativeLayout blacklistLayout;
    private RelativeLayout changeGroupNameLayout;
    private RelativeLayout idLayout;
    private TextView idText;

    ArrayList<UserBean> mMemberList;
    String[] newmembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取传过来的groupid
        groupId = getIntent().getStringExtra("groupId");
        group = EMGroupManager.getInstance().getGroup(groupId);

        mMemberList = SuperWeChatApplication.getInstance().getGroupMembers().get(groupId);

        ArrayList<GroupBean> groupList = SuperWeChatApplication.getInstance().getGroupList();
        for (GroupBean groupBean : groupList) {
            if (groupId.equals(groupBean.getGroupId())) {
                mGroup = groupBean;
            }
        }

        Log.i("main", "mMemberList.size" + mMemberList.size());

        // we are not supposed to show the group if we don't find the group
        if (group == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_group_details);
        instance = this;
        st = getResources().getString(R.string.people);
        clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
        userGridview = (ExpandGridView) findViewById(R.id.gridview);
        loadingPB = (ProgressBar) findViewById(R.id.progressBar);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
        blacklistLayout = (RelativeLayout) findViewById(R.id.rl_blacklist);
        changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
        idLayout = (RelativeLayout) findViewById(R.id.rl_group_id);
        idLayout.setVisibility(View.VISIBLE);
        idText = (TextView) findViewById(R.id.tv_group_id_value);

        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);

        iv_switch_block_groupmsg = (ImageView) findViewById(R.id.iv_switch_block_groupmsg);
        iv_switch_unblock_groupmsg = (ImageView) findViewById(R.id.iv_switch_unblock_groupmsg);

        rl_switch_block_groupmsg.setOnClickListener(this);

        Drawable referenceDrawable = getResources().getDrawable(R.drawable.smiley_add_btn);
        referenceWidth = referenceDrawable.getIntrinsicWidth();
        referenceHeight = referenceDrawable.getIntrinsicHeight();


        idText.setText(groupId);
        if (group.getOwner() == null || "".equals(group.getOwner())
                || !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            blacklistLayout.setVisibility(View.GONE);
            changeGroupNameLayout.setVisibility(View.GONE);
        }
        // 如果自己是群主，显示解散按钮
        if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
        }

        ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount() + st);

        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());

        adapter = new GridAdapter(this, R.layout.grid, mMemberList);
        userGridview.setAdapter(adapter);

        // 保证每次进详情看到的都是最新的group
        updateGroup();

        // 设置OnTouchListener
        userGridview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (adapter.isInDeleteMode) {
                            adapter.isInDeleteMode = false;
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        clearAllHistory.setOnClickListener(this);
        blacklistLayout.setOnClickListener(this);
        changeGroupNameLayout.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.being_added);
        String st2 = getResources().getString(R.string.is_quit_the_group_chat);
        String st3 = getResources().getString(R.string.chatting_is_dissolution);
        String st4 = getResources().getString(R.string.are_empty_group_of_news);
        String st5 = getResources().getString(R.string.is_modify_the_group_name);
        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);
        String st8 = getResources().getString(R.string.Are_moving_to_blacklist);
        final String st9 = getResources().getString(R.string.failed_to_move_into);

        final String stsuccess = getResources().getString(R.string.Move_into_blacklist_success);
        if (resultCode == RESULT_OK) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setMessage(st1);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    newmembers = data.getStringArrayExtra("newmembers");
                    progressDialog.setMessage(st1);
                    progressDialog.show();
                    addMembersToGroup(newmembers);
                    break;
                case REQUEST_CODE_EXIT: // 退出群
                    progressDialog.setMessage(st2);
                    progressDialog.show();
                    exitGrop();
                    break;
                case REQUEST_CODE_EXIT_DELETE: // 解散群
                    progressDialog.setMessage(st3);
                    progressDialog.show();
                    deleteGrop();
                    break;
                case REQUEST_CODE_CLEAR_ALL_HISTORY:
                    // 清空此群聊的聊天记录
                    progressDialog.setMessage(st4);
                    progressDialog.show();
                    clearGroupHistory();
                    break;

                case REQUEST_CODE_EDIT_GROUPNAME: //修改群名称
                    final String returnData = data.getStringExtra("data");
                    if (!TextUtils.isEmpty(returnData)) {
                        progressDialog.setMessage(st5);
                        progressDialog.show();

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    EMGroupManager.getInstance().changeGroupName(groupId, returnData);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            ((TextView) findViewById(R.id.group_name)).setText(returnData + "(" + group.getAffiliationsCount()
                                                    + st);
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } catch (EaseMobException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    break;
                case REQUEST_CODE_ADD_TO_BALCKLIST:
                    progressDialog.setMessage(st8);
                    progressDialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                EMGroupManager.getInstance().blockUser(groupId, longClickUsername);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        refreshMembers();
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), stsuccess, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (EaseMobException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();

                    break;
                default:
                    break;
            }
        }
    }

    private void refreshMembers() {
        // adapter.clear();

        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());
        //  adapter.addAll(members);

        adapter.notifyDataSetChanged();
    }

    /**
     * 点击退出群组按钮
     *
     * @param view
     */
    public void exitGroup(View view) {
        startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

    }

    /**
     * 点击解散群组按钮
     *
     * @param view
     */
    public void exitDeleteGroup(View view) {
        startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
                REQUEST_CODE_EXIT_DELETE);

    }

    /**
     * 清空群聊天记录
     */
    public void clearGroupHistory() {

        EMChatManager.getInstance().clearConversation(group.getGroupId());
        progressDialog.dismiss();
        // adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));

    }

    /**
     * 退出群组
     *
     */
    private void exitGrop() {
        String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
        new Thread(new Runnable() {
            public void run() {
                try {
                    String username = SuperWeChatApplication.getInstance().getUserName();
                    String path = new ApiParams().with(I.Group.GROUP_NAME, group.getGroupName())
                            .with(I.Group.MEMBERS, username)
                            .getRequestUrl(I.REQUEST_DELETE_GROUP_MEMBER);
                    executeRequest(new GsonRequest<Boolean>(path, Boolean.class, new Response.Listener<Boolean>() {
                        @Override
                        public void onResponse(Boolean messageBean) {
                            if (messageBean) {
                                try {
                                    EMGroupManager.getInstance().exitFromGroup(groupId);
                                } catch (EaseMobException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                                setResult(RESULT_OK);
                                Intent intent = new Intent("exit_group");
                                intent.putExtra("group",mGroup);
                                sendStickyBroadcast(intent);
                                finish();
                                if (ChatActivity.activityInstance != null) {
                                    ChatActivity.activityInstance.finish();
                                }
                            }
                        }
                    },errorListener()));
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Exit_the_group_chat_failure) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 解散群组
     *
     */
    private void deleteGrop() {
        final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st5 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 增加群成员
     *
     * @param newmembers
     */
    private void addMembersToGroup(final String[] newmembers) {
        final String st6 = getResources().getString(R.string.Add_group_members_fail);
        new Thread(new Runnable() {

            public void run() {
                try {

                    StringBuilder newGroupMembers = new StringBuilder();
                    for (String str : newmembers) {
                        newGroupMembers.append(str).append(",");
                    }
                    newGroupMembers.deleteCharAt(newGroupMembers.length() - 1);
                    String path = new ApiParams()
                            .with(I.Group.GROUP_ID, groupId)
                            .with(I.Group.MEMBERS, newGroupMembers.toString()).getRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS);
                    executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, new Response.Listener<MessageBean>() {
                        @Override
                        public void onResponse(MessageBean messageBean) {
                            if (messageBean.isSuccess()) {
                              //  ArrayList<UserBean> contactlist = SuperWeChatApplication.getInstance().getContactList();
                                for (int i = 0; i < newmembers.length; i++) {
                                    UserBean user = new UserBean(newmembers[i]);
                                    mMemberList.add(user);
                                }
                            }
                            try {
                            // 创建者调用add方法
                            if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
                                    EMGroupManager.getInstance().addUsersToGroup(groupId, newmembers);
                            } else {
                                // 一般成员调用invite方法
                                EMGroupManager.getInstance().inviteUser(groupId, newmembers, null);
                            }
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount()
                                    + st);
                            progressDialog.dismiss();
                        }
                    }, errorListener()));


                   /* runOnUiThread(new Runnable() {
                        public void run() {
                            refreshMembers();
                        }
                    });*/
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st6 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        String st6 = getResources().getString(R.string.Is_unblock);
        final String st7 = getResources().getString(R.string.remove_group_of);
        switch (v.getId()) {
            case R.id.rl_switch_block_groupmsg: // 屏蔽群组
                if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
                    EMLog.d(TAG, "change to unblock group msg");
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                        progressDialog.setCanceledOnTouchOutside(false);
                    }
                    progressDialog.setMessage(st6);
                    progressDialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                EMGroupManager.getInstance().unblockGroupMessage(groupId);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
                                        iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
                                        progressDialog.dismiss();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }
                    }).start();

                } else {
                    String st8 = getResources().getString(R.string.group_is_blocked);
                    final String st9 = getResources().getString(R.string.group_of_shielding);
                    EMLog.d(TAG, "change to block group msg");
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                        progressDialog.setCanceledOnTouchOutside(false);
                    }
                    progressDialog.setMessage(st8);
                    progressDialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                EMGroupManager.getInstance().blockGroupMessage(groupId);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
                                        iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
                                        progressDialog.dismiss();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }
                    }).start();
                }
                break;

            case R.id.clear_all_history: // 清空聊天记录
                String st9 = getResources().getString(R.string.sure_to_empty_this);
                Intent intent = new Intent(GroupDetailsActivity.this, AlertDialog.class);
                intent.putExtra("cancel", true);
                intent.putExtra("titleIsCancel", true);
                intent.putExtra("msg", st9);
                startActivityForResult(intent, REQUEST_CODE_CLEAR_ALL_HISTORY);
                break;

            case R.id.rl_blacklist: // 黑名单列表
                startActivity(new Intent(GroupDetailsActivity.this, GroupBlacklistActivity.class).putExtra("groupId", groupId));
                break;

            case R.id.rl_change_group_name:
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", group.getGroupName()), REQUEST_CODE_EDIT_GROUPNAME);
                break;

            default:
                break;
        }

    }

    /**
     * 群组成员gridadapter
     *
     * @author admin_new
     *
     */
    private class GridAdapter extends BaseAdapter {

        private int res;
        public boolean isInDeleteMode;
        private List<String> objects;
        ArrayList<UserBean> mmemberArrayList;
        Context context;

        public GridAdapter(Context context, int textViewResourceId, ArrayList<UserBean> mmemberArrayList) {
            res = textViewResourceId;
            isInDeleteMode = false;
            this.context = context;
            this.mmemberArrayList = mmemberArrayList;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(res, null);
                holder.imageView = (NetworkImageView) convertView.findViewById(R.id.iv_avatar);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
            // 最后一个item，减人按钮
            if (position == getCount() - 1) {
                holder.textView.setText("");
                // 设置成删除按钮
                holder.imageView.setDefaultImageResId(R.drawable.smiley_minus_btn);
                holder.imageView.setImageUrl("", RequestManager.getImageLoader());
                holder.imageView.setErrorImageResId(R.drawable.smiley_minus_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_minus_btn, 0, 0);
                // 如果不是创建者或者没有相应权限，不提供加减人按钮
                if (!group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    convertView.setVisibility(View.INVISIBLE);
                } else { // 显示删除按钮
                    if (isInDeleteMode) {
                        // 正处于删除模式下，隐藏删除按钮
                        convertView.setVisibility(View.INVISIBLE);
                    } else {
                        // 正常模式
                        convertView.setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                    }
                    final String st10 = getResources().getString(R.string.The_delete_button_is_clicked);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, st10);
                            isInDeleteMode = true;
                            notifyDataSetChanged();
                        }
                    });
                }
            } else if (position == getCount() - 2) { // 添加群组成员按钮
                holder.textView.setText("");
                holder.imageView.setDefaultImageResId(R.drawable.smiley_add_btn);
                holder.imageView.setImageUrl("", RequestManager.getImageLoader());
                holder.imageView.setErrorImageResId(R.drawable.smiley_add_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_add_btn, 0, 0);
                // 如果不是创建者或者没有相应权限
                if (!group.isAllowInvites() && !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                    // if current user is not group admin, hide add/remove btn
                    convertView.setVisibility(View.INVISIBLE);
                } else {
                    // 正处于删除模式下,隐藏添加按钮
                    if (isInDeleteMode) {
                        convertView.setVisibility(View.INVISIBLE);
                    } else {
                        convertView.setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                    }
                    final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, st11);
                            // 进入选人页面
                            startActivityForResult(
                                    (new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class).putExtra("groupId", groupId)),
                                    REQUEST_CODE_ADD_USER);
                        }
                    });
                }
            } else { // 普通item，显示群组成员
                final UserBean userBean = getItem(position);
                //final String username = getItem(position);
                convertView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
//				Drawable avatar = getResources().getDrawable(R.drawable.default_avatar);
//				avatar.setBounds(0, 0, referenceWidth, referenceHeight);
//				button.setCompoundDrawables(null, avatar, null, null);
                //holder.textView.setText(userBean.getUserName());
              // UserUtils.setGroupMemberNick(userBean.getUserName(), holder.textView);
               UserUtils.setUserBeanNickNF(userBean, holder.textView);
                //UserUtils.setUserAvatar(getContext(), username, holder.imageView);
                UserUtils.setUserBeanAvatar(userBean, holder.imageView);

                // demo群组成员的头像都用默认头像，需由开发者自己去设置头像
                if (isInDeleteMode) {
                    // 如果是删除模式下，显示减人图标
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
                } else {
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
                }
                final String st12 = getResources().getString(R.string.not_delete_myself);
                final String st13 = getResources().getString(R.string.Are_removed);
                final String st14 = getResources().getString(R.string.Delete_failed);
                final String st15 = getResources().getString(R.string.confirm_the_members);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isInDeleteMode) {
                            // 如果是删除自己，return
                            if (EMChatManager.getInstance().getCurrentUser().equals(userBean.getUserName())) {
                                startActivity(new Intent(GroupDetailsActivity.this, AlertDialog.class).putExtra("msg", st12));
                                return;
                            }
                            if (!NetUtils.hasNetwork(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            EMLog.d("group", "remove user from group:" + userBean.getUserName());
                            deleteMembersFromGroup(userBean.getUserName());
                        } else {
                            // 正常情况下点击user，可以进入用户详情或者聊天页面等等
                            // startActivity(new
                            // Intent(GroupDetailsActivity.this,
                            // ChatActivity.class).putExtra("userId",
                            // user.getUsername()));

                        }
                    }

                    /**
                     * 删除群成员
                     *
                     * @param username
                     */
                    protected void deleteMembersFromGroup(final String username) {
                        final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailsActivity.this);
                        deleteDialog.setMessage(st13);
                        deleteDialog.setCanceledOnTouchOutside(false);
                        deleteDialog.show();
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    // 删除被选中的成员
                                    EMGroupManager.getInstance().removeUserFromGroup(groupId, username);
                                    isInDeleteMode = false;
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            deleteDialog.dismiss();
                                            refreshMembers();
                                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "("
                                                    + group.getAffiliationsCount() + st);
                                        }
                                    });
                                } catch (final Exception e) {
                                    deleteDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), st14 + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                });

                button.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        if (EMChatManager.getInstance().getCurrentUser().equals(userBean.getUserName()))
                            return true;
                        if (group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                            Intent intent = new Intent(GroupDetailsActivity.this, AlertDialog.class);
                            intent.putExtra("msg", st15);
                            intent.putExtra("cancel", true);
                            startActivityForResult(intent, REQUEST_CODE_ADD_TO_BALCKLIST);
                            longClickUsername = userBean.getUserName();
                        }
                        return false;
                    }
                });
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return mmemberArrayList.size() + 2;
        }

        @Override
        public UserBean getItem(int position) {
            return mmemberArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    protected void updateGroup() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final EMGroup returnGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
                    // 更新本地数据
                    EMGroupManager.getInstance().createOrUpdateLocalGroup(returnGroup);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group.getAffiliationsCount()
                                    + ")");
                            loadingPB.setVisibility(View.INVISIBLE);
                            refreshMembers();
                            if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
                                // 显示解散按钮
                                exitBtn.setVisibility(View.GONE);
                                deleteBtn.setVisibility(View.VISIBLE);
                            } else {
                                // 显示退出按钮
                                exitBtn.setVisibility(View.VISIBLE);
                                deleteBtn.setVisibility(View.GONE);
                            }

                            // update block
                            EMLog.d(TAG, "group msg is blocked:" + group.getMsgBlocked());
                            if (group.isMsgBlocked()) {
                                iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
                                iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
                            } else {
                                iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
                                iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            loadingPB.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private static class ViewHolder {
        NetworkImageView imageView;
        TextView textView;
        ImageView badgeDeleteView;
    }

}
