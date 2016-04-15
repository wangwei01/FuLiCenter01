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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.listener.OnSetAvatarListener;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.NetUtil;
import cn.ucai.fulicenter.utils.Utils;

public class NewGroupActivity extends BaseActivity {
    private EditText groupNameEditText;
    private ProgressDialog progressDialog;
    private EditText introductionEditText;
    private CheckBox checkBox;
    private CheckBox memberCheckbox;
    private LinearLayout openInviteContainer;
    private ImageView mivavatar;
    OnSetAvatarListener mOnSetAvatarListener;
    NewGroupActivity mContext;
    public static final int ACTION_CREATE_GROUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_new_group);
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        checkBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
        mivavatar = (ImageView) findViewById(R.id.iv_group_avatar);
        setListener();


    }

    private void setListener() {
        setGroupIconClickListener();
        setSaveGroupClickListener();
        setOnCheckedChangeListener();
    }

    private void setOnCheckedChangeListener() {
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openInviteContainer.setVisibility(View.INVISIBLE);
                } else {
                    openInviteContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setGroupIconClickListener() {
        findViewById(R.id.group_relative_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSetAvatarListener = new OnSetAvatarListener(mContext, R.id.group_linear_layout,
                        groupNameEditText.getText().toString(), "group_icon");
            }
        });
    }


    /**
     */
    public void setSaveGroupClickListener() {
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
                String name = groupNameEditText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Intent intent = new Intent(mContext, AlertDialog.class);
                    intent.putExtra("msg", str6);
                    startActivity(intent);
                } else {
                    // 进通讯录选人
                    startActivityForResult(new Intent(mContext, GroupPickContactsActivity.class).putExtra("groupName", name), ACTION_CREATE_GROUP);
                }
            }
        });

    }

    private void setProgressDialogShow(String st1) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    String st1;
    String st2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        st2 = getResources().getString(R.string.Failed_to_create_groups);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == ACTION_CREATE_GROUP) {
            setProgressDialogShow(st1);
            createGroup(data);
        } else {
            mOnSetAvatarListener.setAvatar(requestCode, data, mivavatar);
        }
    }

    private void createGroup(Intent data) {
        String groupName = groupNameEditText.getText().toString().trim();
        try {
            String path = new ApiParams()
                    .with(I.Group.NAME, groupName)
                    .getRequestUrl(I.REQUEST_FIND_GROUP);
            executeRequest(new GsonRequest<GroupBean>(path, GroupBean.class, responseGroupSuccessListener(data.getStringArrayExtra("newmembers")), errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Response.Listener<GroupBean> responseGroupSuccessListener(final String[] members) {
        return new Response.Listener<GroupBean>() {
            @Override
            public void onResponse(final GroupBean groupBean) {
                if (groupBean != null) {
                    progressDialog.dismiss();
                    groupNameEditText.requestFocus();
                    groupNameEditText.setError("群名已经存在");
                } else {
                    //新建群组
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 调用sdk创建群组方法
                            String groupName = groupNameEditText.getText().toString().trim();
                            String desc = introductionEditText.getText().toString();
                            StringBuffer sbmembers = new StringBuffer();
                            for (String membername : members) {
                                sbmembers.append(membername).append(",");
                            }
                            String userName = SuperWeChatApplication.getInstance().getUserName();
                            sbmembers.append(userName);

                            String groupId = null;

                            try {
                                if (checkBox.isChecked()) {
                                    //创建公开群，此种方式创建的群，可以自由加入
                                    //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                                    EMGroup publicGroup = EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                                    groupId = publicGroup.getGroupId();
                                } else {
                                    //创建不公开群
                                    EMGroup privateGroup = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
                                    groupId = privateGroup.getGroupId();
                                }
                                boolean ischeckes = checkBox.isChecked();
                                boolean ischeckesmember = !memberCheckbox.isChecked();
                                GroupBean groupnean = new GroupBean(groupId, groupName, desc, userName, ischeckes, ischeckesmember, sbmembers.toString());
                                Log.e("main", "groupbean"+groupnean);
                                boolean issuccess = NetUtil.createGroup(groupnean);
                                if (issuccess) {
                                    try {
                                        boolean uploadissuccess = NetUtil.uploadAvatar(mContext, "group_icon", groupName);
                                        if (uploadissuccess) {
                                            groupnean.setAvatar("group_icon/" + groupName + ".jpg");
                                            Intent intent = new Intent("download_groups");
                                            intent.putExtra("group", groupnean);
                                            setResult(RESULT_OK,intent);
                                        } else {
                                            progressDialog.dismiss();
                                            Utils.showToast(mContext, "头像上传失败", Toast.LENGTH_LONG);
                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();
                                                finish();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    progressDialog.dismiss();
                                }

                            } catch (final EaseMobException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }


                        }
                    }).start();
                }
            }
        };
    }


    public void back(View view) {
        finish();
    }
}
