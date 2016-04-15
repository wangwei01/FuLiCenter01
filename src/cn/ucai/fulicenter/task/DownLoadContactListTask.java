package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/4/7.
 */
public class DownLoadContactListTask extends BaseActivity {
    Context mContext;
    String username;
    int pageId;
    int pageSize;
    String path;

    public DownLoadContactListTask(Context mContext, String username, int pageId, int pageSize) {
        this.mContext = mContext;
        this.username = username;
        this.pageId = pageId;
        this.pageSize = pageSize;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, username)
                    .with(I.PAGE_ID, pageId + "")
                    .with(I.PAGE_SIZE, pageSize + "")
                    .getRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {

        executeRequest(new GsonRequest<UserBean[]>(path,UserBean[].class,responseListener(),errorListener()));
    }

    private Response.Listener<UserBean[]> responseListener() {
        return new Response.Listener<UserBean[]>() {
            @Override
            public void onResponse(UserBean[] userArray) {
                Log.e("main", "responseListener" + userArray);
                if(userArray==null){
                    return;
                }
                //将数组转换为集合
                ArrayList<UserBean> userList= Utils.array2List(userArray);
                //获取已添加的所有联系人的集合
                ArrayList<UserBean> contactList = FuLiCenterApplication.getInstance().getContactList();
                //将新下载的数据添加到原联系人集合中
                contactList.clear();
                contactList.addAll(userList);


                HashMap<String, UserBean> userList1 = FuLiCenterApplication.getInstance().getUserList();
                HashMap<String, UserBean> userMap = new HashMap<>();
                for (UserBean userBean : userArray) {
                    userMap.put(userBean.getUserName(), userBean);
                }
                userList1.clear();
                userList1.putAll(userMap);
                Log.e("main", "responseListener" + userArray.length);
                Intent intent = new Intent("update_contact_list");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
