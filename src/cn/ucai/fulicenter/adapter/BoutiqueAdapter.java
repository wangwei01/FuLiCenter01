package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by sks on 2016/4/19.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<BoutiqueBean> mBoutiqueArrList;

    FooterViewHolder mFooterViewholder;



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class BoutiqueViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mRelativrLayout;
        NetworkImageView mnivThunb;
        TextView mtvIntroduce;
        TextView mtvText;
        TextView mtvDetails;

        public BoutiqueViewHolder(View itemView) {
            super(itemView);
            mRelativrLayout = (RelativeLayout) itemView.findViewById(R.id.layout_Boutique);
            mnivThunb = (NetworkImageView) itemView.findViewById(R.id.niv_boutique);

        }
    }
}
