package cn.ucai.fulicenter.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by sks on 2016/4/16.
 */
public class FooterViewHolder extends RecyclerView.ViewHolder {

    public TextView tvfooter;

    public FooterViewHolder(View itemView) {
        super(itemView);
        tvfooter = (TextView) itemView.findViewById(R.id.tv_footer);
    }
}
