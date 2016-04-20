package cn.ucai.fulicenter.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.activity.SettingsActivity;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.RequestManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonCenterFragment extends Fragment {
    NetworkImageView mnivAvatar;
    TextView mtvUserName;
    TextView mtvCollectionNum;
    TextView mtvSetUp;
    FuLiCenterMainActivity mContext;
    CollectionNumReceiver mCollectionNumReceiver;

    public PersonCenterFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getActivity();
        View layout = inflater.inflate(R.layout.fragment_person_center, container, false);
        registerCollectionNumReceiver();
        initView(layout);
        initDate();
        setListener();
        return layout;
    }

    private void setListener() {
        mtvSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerCollectionNumReceiver() {
        mCollectionNumReceiver = new CollectionNumReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("collection_num");
        mContext.registerReceiver(mCollectionNumReceiver, filter);
    }

    private void initDate() {
        UserBean user = FuLiCenterApplication.getInstance().getUser();
        if (user != null) {
            mnivAvatar.setErrorImageResId(R.drawable.nopic);
            mnivAvatar.setImageUrl(I.DOWNLOAD_AVATAR_URL+user.getAvatar(), RequestManager.getImageLoader());
            mnivAvatar.setDefaultImageResId(R.drawable.nopic);
            mtvUserName.setText(user.getUserName());
        }
    }



    private void initView(View layout) {
        mnivAvatar = (NetworkImageView) layout.findViewById(R.id.iv_person_avatar);
        mtvUserName = (TextView) layout.findViewById(R.id.tv_username);
        mtvCollectionNum = (TextView) layout.findViewById(R.id.tv_collection_num);
        mtvSetUp = (TextView) layout.findViewById(R.id.tv_set_up);
    }


    class CollectionNumReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String num = intent.getStringExtra("num");
            mtvCollectionNum.setText(num);
        }
    }

}


