package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;

/**
 * Created by sks on 2016/4/21.
 */
public class DownLoadCollectCountTask extends BaseActivity {
    Context mContext;
    String path;

    public DownLoadCollectCountTask(Context mContext) {
        this.mContext = mContext;
        initPath();
    }

    private void initPath() {
        String userName = FuLiCenterApplication.getInstance().getUserName();
        if (userName != null) {
            try {
               path = new ApiParams()
                        .with(I.User.USER_NAME,userName)
                        .getRequestUrl(I.REQUEST_FIND_COLLECT_COUNT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void execute() {
        if (path != null && !path.isEmpty()) {
            executeRequest(new GsonRequest<MessageBean>(path,MessageBean.class,responseCollectionListener(),errorListener()));
        }
    }
    private Response.Listener<MessageBean> responseCollectionListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    String count = messageBean.getMsg();
                    FuLiCenterApplication.getInstance().setCollectionCount(Integer.parseInt(count));
                } else {
                    FuLiCenterApplication.getInstance().setCollectionCount(0);
                }
                Intent intent = new Intent("update_collection_num");
                mContext.sendStickyBroadcast(intent);
            }
        };
    }
}
