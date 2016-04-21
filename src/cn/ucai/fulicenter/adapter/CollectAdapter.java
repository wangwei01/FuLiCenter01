package cn.ucai.fulicenter.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.task.DownLoadCollectCountTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by sks on 2016/4/21.
 */
public class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    CollectActivity context;
    ArrayList<CollectBean> mGoodList;

    FooterViewHolder footerViewHolder;
    GoodCollectViewHolder collectViewHolder;
    int sortBy;

    String foottext;
    boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }


    public void setFoottext(String foottext) {
        this.foottext = foottext;
        notifyDataSetChanged();
    }

    public CollectAdapter(CollectActivity context, ArrayList<CollectBean> arrayListgoodbean) {
        this.context = context;
        this.mGoodList = arrayListgoodbean;
    }

    @Override
    public int getItemCount() {
        return mGoodList == null ? 1 : mGoodList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void initItems(ArrayList<CollectBean> list) {
        if (mGoodList != null && !mGoodList.isEmpty()) {
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        notifyDataSetChanged();
    }

    public void additems(ArrayList<CollectBean> list) {
        mGoodList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer, parent, false));
                break;
            case I.TYPE_ITEM:
                holder = new GoodCollectViewHolder(inflater.inflate(R.layout.item_collect_good, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.tvfooter.setText(foottext);
            footerViewHolder.tvfooter.setVisibility(View.VISIBLE);
        }
        if (position == mGoodList.size()) {
            return;
        }

        if (holder instanceof GoodCollectViewHolder) {
            collectViewHolder = (GoodCollectViewHolder) holder;
            final CollectBean goodBean = mGoodList.get(position);
            collectViewHolder.tvcollectName.setText(goodBean.getGoodsName());
            ImageUtils.setNewGoodThumb(goodBean.getGoodsThumb(), collectViewHolder.nivCollectGoodAvatar);


            collectViewHolder.layoutCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, GoodDetailsActivity.class)
                            .putExtra(D.NewGood.KEY_GOODS_ID, goodBean.getGoodsId()));
                }
            });

            collectViewHolder.ivdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserBean user = FuLiCenterApplication.getInstance().getUser();
                    try {
                        String path = new ApiParams()
                                .with(I.Collect.GOODS_ID, goodBean.getGoodsId() + "")
                                .with(I.User.USER_NAME, user.getUserName())
                                .getRequestUrl(I.REQUEST_DELETE_COLLECT);
                        context.executeRequest(new GsonRequest<MessageBean>(path, MessageBean.class, responseDeleteListener(goodBean), context.errorListener()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private Response.Listener<MessageBean> responseDeleteListener(final CollectBean collectBean) {
        return new Response.Listener<MessageBean>() {
            @Override
            public void onResponse(MessageBean messageBean) {
                if (messageBean.isSuccess()) {
                    mGoodList.remove(collectBean);
                    notifyDataSetChanged();
                    new DownLoadCollectCountTask(context).execute();
                }
                Utils.showToast(context, messageBean.getMsg(), Toast.LENGTH_SHORT);
            }
        };
    }


    class GoodCollectViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutCollect;
        ImageView ivdelete;
        NetworkImageView nivCollectGoodAvatar;
        TextView tvcollectName;

        public GoodCollectViewHolder(View itemView) {
            super(itemView);
            layoutCollect = (LinearLayout) itemView.findViewById(R.id.layout_collect_good);
            ivdelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            nivCollectGoodAvatar = (NetworkImageView) itemView.findViewById(R.id.niv_collect_thumb);
            tvcollectName = (TextView) itemView.findViewById(R.id.tv_collect_name);
        }

    }
}
