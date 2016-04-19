package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueChildActivity;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by sks on 2016/4/19.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<BoutiqueBean> mBoutiqueArrList;

    FooterViewHolder mFooterViewholder;
    BoutiqueViewHolder mBoutiqueViewholder;
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

    public BoutiqueAdapter(Context mContext, ArrayList<BoutiqueBean> mBoutiqueArrList) {
        this.mContext = mContext;
        this.mBoutiqueArrList = mBoutiqueArrList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer, parent,false));
                break;
            case I.TYPE_ITEM:
                holder = new BoutiqueViewHolder(inflater.inflate(R.layout.item_boutique, parent,false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            mFooterViewholder = (FooterViewHolder) holder;
            mFooterViewholder.tvfooter.setText(foottext);
            mFooterViewholder.tvfooter.setVisibility(View.VISIBLE);
        }


        if (holder instanceof BoutiqueViewHolder) {
            mBoutiqueViewholder = (BoutiqueViewHolder) holder;
            final BoutiqueBean boutiqueBean = mBoutiqueArrList.get(position);
            mBoutiqueViewholder.mtvDetails.setText(boutiqueBean.getDescription());
            mBoutiqueViewholder.mtvText.setText(boutiqueBean.getName());
            mBoutiqueViewholder.mtvIntroduce.setText(boutiqueBean.getTitle());
            ImageUtils.setBoutiqueThumb(I.DOWNLOAD_BOUTIQUE_IMG_URL+boutiqueBean.getImageurl(),mBoutiqueViewholder.mnivThunb);
            mBoutiqueViewholder.mRelativrLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BoutiqueChildActivity.class);
                    intent.putExtra(I.Boutique.ID, boutiqueBean.getId());
                    intent.putExtra(I.Boutique.NAME, boutiqueBean.getName());
                    mContext.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mBoutiqueArrList == null ? 1 : mBoutiqueArrList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }
    public void initItems(ArrayList<BoutiqueBean> list) {
        if (mBoutiqueArrList != null && !mBoutiqueArrList.isEmpty()) {
            mBoutiqueArrList.clear();
        }
        mBoutiqueArrList.addAll(list);
        notifyDataSetChanged();
    }

    public void additems(ArrayList<BoutiqueBean> list) {
        mBoutiqueArrList.addAll(list);
        notifyDataSetChanged();
    }

    class BoutiqueViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mRelativrLayout;
        NetworkImageView mnivThunb;
        TextView mtvIntroduce;
        TextView mtvText;
        TextView mtvDetails;

        public BoutiqueViewHolder(View itemView) {
            super(itemView);
            mRelativrLayout = (RelativeLayout) itemView.findViewById(R.id.layout_boutique);
            mnivThunb = (NetworkImageView) itemView.findViewById(R.id.niv_boutique);
            mtvIntroduce = (TextView) itemView.findViewById(R.id.tv_introduction);
            mtvText = (TextView) itemView.findViewById(R.id.tv_text);
            mtvDetails = (TextView) itemView.findViewById(R.id.tv_details);
        }
    }
}
