package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by sks on 2016/4/16.
 */
public class GoodAdaptar extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<NewGoodBean> mGoodList;

    FooterViewHolder footerViewHolder;
    GoodItemViewHolder goodItemViewHolder;
    int sortBy;

    String foottext;
    boolean isMore;

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        sort(sortBy);
        notifyDataSetChanged();
    }

    private void sort(final int sortBy) {
        Collections.sort(mGoodList, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean g1, NewGoodBean g2) {
                int result =0;
                switch (sortBy){
                    case I.SORT_BY_ADDTIME_ASC:
                        result = (int) (g1.getAddTime()-g2.getAddTime());
                        break;
                    case I.SORT_BY_ADDTIME_DESC:
                        result = (int) (g2.getAddTime()-g1.getAddTime());
                        break;
                    case I.SORT_BY_PRICE_ASC:
                    {
                        int p1 = convertPrice(g1.getCurrencyPrice());
                        int p2 = convertPrice(g2.getCurrencyPrice());
                        result = p1-p2;
                    }
                    break;
                    case I.SORT_BY_PRICE_DESC:
                    {
                        int p1 = convertPrice(g1.getCurrencyPrice());
                        int p2 = convertPrice(g2.getCurrencyPrice());
                        result = p2-p1;
                    }
                    break;
                }
                return result;
            }
            private int convertPrice(String price){
                price = price.substring(price.indexOf("￥")+1);
                int p1 = Integer.parseInt(price);
                return p1;
            }
        });
    }

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

    public GoodAdaptar(Context context, ArrayList<NewGoodBean> arrayListgoodbean,int sortBy) {
        this.context = context;
        this.mGoodList = arrayListgoodbean;
        this.sortBy = sortBy;
    }

    @Override
    public int getItemCount() {
        return mGoodList == null ? 1 : mGoodList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    public void initItems(ArrayList<NewGoodBean> list) {
        if (mGoodList != null && !mGoodList.isEmpty()) {
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        sort(sortBy);
        notifyDataSetChanged();
    }

    public void additems(ArrayList<NewGoodBean> list) {
        mGoodList.addAll(list);
        sort(sortBy);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
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
            final NewGoodBean goodBean = mGoodList.get(position);
            goodItemViewHolder.tvNewGoodPrice.setText(goodBean.getCurrencyPrice());
            goodItemViewHolder.tvNewGoodName.setText(goodBean.getGoodsName());
            ImageUtils.setNewGoodThumb(goodBean.getGoodsThumb(),goodItemViewHolder.nivNewGoodAvatar);


            goodItemViewHolder.layoutGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, GoodDetailsActivity.class)
                            .putExtra(D.NewGood.KEY_GOODS_ID, goodBean.getGoodsId()));
                }
            });
        }
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
