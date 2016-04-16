package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by sks on 2016/4/16.
 */
public class GoodAdaptar extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<NewGoodBean> mGoodList;

    FooterViewHolder footerViewHolder;
    GoodItemViewHolder goodItemViewHolder;


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

    public GoodAdaptar(Context context, ArrayList<NewGoodBean> arrayListgoodbean) {
        this.context = context;
        this.mGoodList = arrayListgoodbean;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer, parent,false));
                break;
            case I.TYPE_ITEM:
                holder = new GoodItemViewHolder(inflater.inflate(R.layout.item_new_good, parent,false));
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
        if (holder instanceof GoodItemViewHolder) {
            goodItemViewHolder = (GoodItemViewHolder) holder;
            NewGoodBean goodBean = mGoodList.get(position);
            goodItemViewHolder.tvNewGoodPrice.setText(goodBean.getCurrencyPrice());
            goodItemViewHolder.tvNewGoodName.setText(goodBean.getGoodsName());
            ImageUtils.setNewGoodThumb(goodBean.getGoodsThumb(),goodItemViewHolder.nivNewGoodAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return mGoodList == null ? 0 : mGoodList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void initItems(ArrayList<NewGoodBean> list) {
        if (list != null && !list.isEmpty()) {
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        notifyDataSetChanged();
    }

    public void additems(ArrayList<NewGoodBean> list) {
        mGoodList.addAll(list);
        notifyDataSetChanged();
    }

    class GoodItemViewHolder extends RecyclerView.ViewHolder {
         LinearLayout layoutGood;
         NetworkImageView nivNewGoodAvatar;
         TextView tvNewGoodName;
         TextView tvNewGoodPrice;

        public GoodItemViewHolder(View itemView) {
            super(itemView);
            layoutGood = (LinearLayout) itemView.findViewById(R.id.layout_new_good);
            nivNewGoodAvatar = (NetworkImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvNewGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvNewGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
        }

    }
}
