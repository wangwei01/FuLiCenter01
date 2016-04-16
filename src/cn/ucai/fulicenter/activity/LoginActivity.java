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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.Constant;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.db.EMUserDao;
import cn.ucai.fulicenter.db.UserDao;
import cn.ucai.fulicenter.domain.User;
import cn.ucai.fulicenter.listener.OnSetAvatarListener;
import cn.ucai.fulicenter.task.DownLoadContactListTask;
import cn.ucai.fulicenter.task.DownLoadContactTask;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.MD5;
import cn.ucai.fulicenter.utils.NetUtil;
import cn.ucai.fulicenter.utils.Utils;

/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private boolean progressShow;
    ProgressDialog pd;

    private boolean autoLogin = false;

    private String currentUsername;
    private String currentPassword;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 如果用户名密码都有，直接进入主页面
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

            return;
        }
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);


        mContext = this;
        setListener();

        if (FuLiCenterApplication.getInstance().getUserName() != null) {
            usernameEditText.setText(FuLiCenterApplication.getInstance().getUserName());
        }
    }

    private void setListener() {
        setLoginListener();
        setRegisterListener();
        setUserNameTextChangedListener();
        setServerUrlClickListener();
    }


    String serverUrl;

    private void setServerUrlClickListener() {
        findViewById(R.id.btn_Url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sp = getSharedPreferences("server_url", MODE_PRIVATE);
                serverUrl = sp.getString("url", "");
                View layout = View.inflate(mContext, R.layout.dialog_serverurl, null);
                final EditText etServerUrl = (EditText) layout.findViewById(R.id.et_serverurl);
                String url = etServerUrl.getText().toString();
                if (serverUrl != null) {
                    etServerUrl.setText(serverUrl);
                }

                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("设置服务器IP地址")
                        .setView(layout)
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                serverUrl = etServerUrl.getText().toString();
                                sp.edit().putString("url", serverUrl).commit();
                                FuLiCenterApplication.SERVER_ROOT = serverUrl + ":8080/SuperQQ4Server/Server";
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
            }
        });
    }

    private void setUserNameTextChangedListener() {
        // 如果用户名改变，清空密码
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 注册
     */
    private void setRegisterListener() {
        findViewById(R.id.btn_Register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext, RegisterActivity.class), 0);
            }
        });
    }

    /**
     *
     * 启动登陆的Diolog
     */

    private void onProgressShow() {
        progressShow = true;
        pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd.show();
            }
        });
    }


    private void loginSuccess() {
        Log.i("main", "5");
        // 登陆成功，保存用户名密码
        FuLiCenterApplication.getInstance().setUserName(currentUsername);
        FuLiCenterApplication.getInstance().setPassword(currentPassword);

        try {
            // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
            // ** manually load all local groups and
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();

            //下载当前用户的头像
         /*   UserDao dao = new UserDao(mContext);
            String avatarName=dao.findUserByUserName(currentUsername).getAvatar();
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file=new File(dir,avatarName);
            NetUtil.downloadAvatar(file, null, avatarName );*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String avatar = FuLiCenterApplication.getInstance().getUser().getAvatar();
                    File file = OnSetAvatarListener.getAvatarFile(mContext, avatar);
                    NetUtil.downloadAvatar(file, "user_avatar", avatar);
                }
            }).start();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //下载联系人列表
                    new DownLoadContactTask(mContext, currentUsername, 0, 20).execute();
                    //下载好友列表
                    new DownLoadContactListTask(mContext,currentUsername,0,20).execute();


                }
            });
            // 处理好友和群组
            initializeContacts();
        } catch (Exception e) {
            e.printStackTrace();
            // 取好友或者群聊失败，不让进入主页面
            runOnUiThread(new Runnable() {
                public void run() {
                    pd.dismiss();
                    DemoHXSDKHelper.getInstance().logout(true,null);
                    Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
        boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                FuLiCenterApplication.currentUserNick.trim());
        if (!updatenick) {
            Log.e("LoginActivity", "update current user nick fail");
        }
        if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
            pd.dismiss();
        }
        // 进入主页面
        Intent intent = new Intent(LoginActivity.this,
                MainActivity.class);
        startActivity(intent);

        finish();
    }


    private void loginAppServer() {
        Log.i("main", "1");
        UserDao dao = new UserDao(mContext);
        UserBean user = dao.findUserByUserName(currentUsername);
        if (user != null) {
            if (user.getPassword().equals(MD5.getData(currentPassword))) {
                saveUser(user);
                loginSuccess();
            } else {
                Log.i("main", "6");
                pd.dismiss();
            }
        }else{
            Log.i("main", "7");
            //使用Volley登录服务器
            String path = null;
            try {
                path = new ApiParams()
                        .with(I.User.USER_NAME, currentUsername)
                        .with(I.User.PASSWORD, currentPassword)
                        .getRequestUrl(I.REQUEST_LOGIN);
                Log.i("main", "14");
                executeRequest(new GsonRequest<UserBean>(path,UserBean.class,responseListener(),errorListener()));
                Log.i("main", "12");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Response.Listener<UserBean> responseListener() {
        return new Response.Listener<UserBean>() {
            @Override
            public void onResponse(UserBean userBean) {
                if (userBean.getResult().equals("ok")) {
                    saveUser(userBean);
                    userBean.setPassword(MD5.getData(userBean.getPassword()));
                    UserDao dao = new UserDao(mContext);
                    //根据User。getREsult的值来判断用户的登录状态
                    dao.addUser(userBean);
                    loginSuccess();
                } else {
                    pd.dismiss();
                    Utils.showToast(mContext, "登录失败，请重新登录", Toast.LENGTH_LONG);
                }
            }
        };
    }

    private void saveUser(UserBean user) {
        FuLiCenterApplication instance = FuLiCenterApplication.getInstance();
        instance.setUser(user);
        instance.setUserName(currentUsername);
        instance.setPassword(currentPassword);
        instance.currentUserNick = user.getNick();
    }


    /**
     * 登录
     */
    public void setLoginListener() {
        findViewById(R.id.btn_Login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtils.isNetWorkConnected(mContext)) {
                    Toast.makeText(mContext, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                currentUsername = usernameEditText.getText().toString().trim();
                currentPassword = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(currentUsername)) {
                    Toast.makeText(mContext, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(mContext, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                onProgressShow();

                final long start = System.currentTimeMillis();
                // 调用sdk登陆方法登陆聊天服务器
                EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        Log.i("main", "sucess");
                        if (!progressShow) {
                            Log.i("main", "2");
                            return;
                        }
                        loginAppServer();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(final int code, final String message) {
                        if (!progressShow) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        User robotUser = new User();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        EMUserDao dao = new EMUserDao(LoginActivity.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
}
