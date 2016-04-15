package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.I;

/**
 * Created by sks on 2016/4/7.
 */
public class DownLoadGroups extends BaseActivity {
    Context mContext;
    String userName;
    String path;

    public DownLoadGroups(Context mContext, String userName) {
        this.mContext = mContext;
        this.userName = userName;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, userName)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {

        executeRequest(new GsonRequest<GroupBean[]>(path, GroupBean[].class, responseListener(), errorListener()));
    }

    private Response.Listener<GroupBean[]> responseListener() {
        return new Response.Listener<GroupBean[]>() {
            @Override
            public void onResponse(GroupBean[] groupArray) {
                if(groupArray==null){
                    return;
                }
                List<GroupBean> list = Arrays.asList(groupArray);
                FuLiCenterApplication instance = FuLiCenterApplication.getInstance();
                ArrayList<GroupBean> groups = new ArrayList<GroupBean>(list);
                instance.setGroupList(groups);
                Intent intent = new Intent("download_groups");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }

}
