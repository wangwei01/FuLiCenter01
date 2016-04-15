package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
 * Created by sks on 2016/4/13.
 */
public class DownLoadPublicGroupTask extends BaseActivity {
    Context mContext;
    String userName;
    int pageid;
    int pagesize;
    String path;

    public DownLoadPublicGroupTask(Context mContext, String userName, int pageid, int pagesize) {
        this.mContext = mContext;
        this.userName = userName;
        this.pageid = pageid;
        this.pagesize = pagesize;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, userName)
                    .with(I.PAGE_ID, ""+pageid)
                    .with(I.PAGE_SIZE, ""+pagesize)
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS);
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
                Log.i("main","下载用户为"+Arrays.toString(groupArray));
                List<GroupBean> list = Arrays.asList(groupArray);
                ArrayList<GroupBean> publicGroupList = FuLiCenterApplication.getInstance().getPublicGroupList();
                ArrayList<GroupBean> groups = new ArrayList<GroupBean>(list);

                for (GroupBean groupBean : groups) {
                    if (!publicGroupList.contains(groupBean)) {
                        publicGroupList.add(groupBean);
                    }
                }

                Intent intent = new Intent("download_public_group");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
