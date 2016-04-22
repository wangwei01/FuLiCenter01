package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownLoadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

public class GoodDetailsActivity extends BaseActivity {
    public static final String TAG = GoodDetailsActivity.class.getName();
    GoodDetailsActivity mContext;
    GoodDetailsBean mGoodDetails;
    int mGoodsId;
    /**
     * 用于收藏、支付的商品信息实体
     */
    NewGoodBean mGood;
    /**
     * 封装了显示商品信息的view
     */
//    ViewHolder mHolder;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    /**
     * 显示颜色的容器布局
     */
    LinearLayout mLayoutColors;
    ImageView mivCollect;
    ImageView mivAddCart;
    ImageView mivShare;
    TextView mtvCartCount;

    TextView tvGoodName;
    TextView tvGoodEngishName;
    TextView tvShopPrice;
    TextView tvCurrencyPrice;
    WebView wvGoodBrief;

    CartChangedReceiver mCartChangedReceiver;


    /**
     * 当前的颜色值
     */
    int mCurrentColor;

    //是否被收藏的参数
    boolean isCollect;
    private int actionCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_details);

        mContext = this;
        initView();
        initData();
        setListener();

        registerCartChangedReceiver();
    }

    private void setListener() {
        mivCollect.setOnClickListener(new setCollectOnclickListener());
        setAddcartClickListener();
        mivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });

    }

    private void setAddcartClickListener() {
        mivAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addCart(mContext, mGoodDetails);
            }
        });
    }

    class setCollectOnclickListener implements View.OnClickListener {
        UserBean user = FuLiCenterApplication.getInstance().getUser();
        @Override
        public void onClick(View v) {
            if (user == null) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                try {
                    String path ="";
                    if (isCollect) {
                        path = new ApiParams()
                                .with(I.User.USER_NAME, user.getUserName())
                                .with(I.Collect.GOODS_ID, mGoodDetails.getGoodsId() + "")
                                .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                        actionCollect = I.ACTION_DELETE_COLLECTION;
                    } else {
                        path = new ApiParams()
                                .with(I.User.USER_NAME, user.getUserName())
                                .with(I.Collect.GOODS_ID, mGoodDetails.getGoodsId() + "")
                                .with(I.Collect.GOODS_NAME, mGoodDetails.getGoodsName())
                                .with(I.Collect.GOODS_ENGLISH_NAME, mGoodDetails.getGoodsEnglishName())
                                .with(I.Collect.GOODS_THUMB, mGoodDetails.getGoodsThumb())
                                .with(I.Collect.GOODS_IMG, mGoodDetails.getGoodsImg())
                                .with(I.Collect.ADD_TIME, mGoodDetails.getAddTime() + "")
                                .getRequestUrl(I.REQUEST_ADD_COLLECT);
                        actionCollect = I.ACTION_ADD_COLLECTION;
                    }
                    executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseMessageListener(actionCollect), errorListener()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Response.Listener<MessageBean> responseMessageListener(final int actionCollect) {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    if (actionCollect == I.ACTION_ADD_COLLECTION) {
                        isCollect = true;
                        mivCollect.setImageResource(R.drawable.bg_collect_out);
                    } else if (actionCollect == I.ACTION_DELETE_COLLECTION) {
                        isCollect = false;
                        mivCollect.setImageResource(R.drawable.bg_collect_in);
                    }
                    new DownLoadCollectCountTask(mContext).execute();
                }
                Utils.showToast(mContext, messageBean.getMsg(), Toast.LENGTH_SHORT);
            }
        };
    }

    private void initData() {
        mGoodsId = getIntent().getIntExtra(D.NewGood.KEY_GOODS_ID, 0);
        try {
            String path = new ApiParams().with(I.CategoryGood.GOODS_ID, mGoodsId + "")
                    .getRequestUrl(I.REQUEST_FIND_GOOD_DETAILS);
            executeRequest(new GsonRequest<GoodDetailsBean>(path, GoodDetailsBean.class,
                    responseDownloadGoodDetailsListener(), errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<GoodDetailsBean> responseDownloadGoodDetailsListener() {
        return new Response.Listener<GoodDetailsBean>() {
            @Override
            public void onResponse(GoodDetailsBean goodDetailsBean) {
                if (goodDetailsBean != null) {
                    mGoodDetails = goodDetailsBean;
                    DisplayUtils.initBackWithTitle(mContext, getResources().getString(R.string.title_good_details));
                    tvCurrencyPrice.setText(mGoodDetails.getCurrencyPrice());
                    tvGoodEngishName.setText(mGoodDetails.getGoodsEnglishName());
                    tvGoodName.setText(mGoodDetails.getGoodsName());
                    wvGoodBrief.loadDataWithBaseURL(null, mGoodDetails.getGoodsBrief().trim(), D.TEXT_HTML, D.UTF_8, null);

                    //初始化颜色面板
                    initColorsBanner();
                } else {
                    Utils.showToast(mContext, "商品详情下载失败", Toast.LENGTH_LONG);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        CollectionImageIsSelected();
    }

    private void CollectionImageIsSelected() {
        UserBean user = FuLiCenterApplication.getInstance().getUser();
        if (user!=null) {
            try {
                String path = new ApiParams()
                        .with(I.User.USER_NAME, user.getUserName())
                        .with(I.Collect.GOODS_ID, mGoodsId + "")
                        .getRequestUrl(I.REQUEST_IS_COLLECT);
                executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseDownMessageListener(), errorListener()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isCollect = false;
            mivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }

    private Response.Listener<MessageBean> responseDownMessageListener() {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    mivCollect.setImageResource(R.drawable.bg_collect_out);
                } else {
                    mivCollect.setImageResource(R.drawable.bg_collect_in);
                }
            }
        };
    }

    private void initColorsBanner() {
        //设置第一个颜色的图片轮播
        updateColor(0);
        for (int i = 0; i < mGoodDetails.getProperties().length; i++) {
            mCurrentColor = i;
            View layout = View.inflate(mContext, R.layout.layout_property_color, null);
            final NetworkImageView ivColor = (NetworkImageView) layout.findViewById(R.id.ivColorItem);
            Log.i(TAG, "initColorsBanner.goodDetails=" + mGoodDetails.getProperties()[i].toString());
            String colorImg = mGoodDetails.getProperties()[i].getColorImg();
            if (colorImg.isEmpty()) {
                continue;
            }
            ImageUtils.setNewGoodThumb(colorImg, ivColor);
            mLayoutColors.addView(layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateColor(mCurrentColor);
                }
            });
        }
    }

    /**
     * 设置指定属性的图片轮播
     *
     * @param i
     */
    private void updateColor(int i) {
        AlbumBean[] albums = mGoodDetails.getProperties()[i].getAlbums();
        String[] albumImgUrl = new String[albums.length];
        for (int j = 0; j < albumImgUrl.length; j++) {
            albumImgUrl[j] = albums[j].getImgUrl();
        }
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator, albumImgUrl, albumImgUrl.length);
    }

    private void initView() {
        mivCollect = (ImageView) findViewById(R.id.ivCollect);
        mivAddCart = (ImageView) findViewById(R.id.ivAddCart);
        mivShare = (ImageView) findViewById(R.id.ivShare);
        mtvCartCount = (TextView) findViewById(R.id.tvCartCount);

        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mLayoutColors = (LinearLayout) findViewById(R.id.layoutColorSelector);
        tvCurrencyPrice = (TextView) findViewById(R.id.tvCurrencyPrice);
        tvGoodEngishName = (TextView) findViewById(R.id.tvGoodEnglishName);
        tvGoodName = (TextView) findViewById(R.id.tvGoodName);
        tvShopPrice = (TextView) findViewById(R.id.tvShopPrice);
        wvGoodBrief = (WebView) findViewById(R.id.wvGoodBrief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);

        mtvCartCount = (TextView) findViewById(R.id.tvCartCount);


    }


    class CartChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.sumCartCount();
            if (count > 0) {
                mtvCartCount.setText("" + count);
                mtvCartCount.setVisibility(View.VISIBLE);
            } else {
                mtvCartCount.setVisibility(View.GONE);
            }
        }
    }

    private void registerCartChangedReceiver() {
        mCartChangedReceiver = new CartChangedReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        mContext.registerReceiver(mCartChangedReceiver,filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCartChangedReceiver != null) {
            mContext.unregisterReceiver(mCartChangedReceiver);
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}
