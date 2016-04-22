package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by sks on 2016/4/22.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    ArrayList<CartBean> mCartArrList;

    FooterViewHolder mFooterViewholder;
    CartViewHolder mCartViewholder;

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

    public CartAdapter(Context mContext, ArrayList<CartBean> mBoutiqueArrList) {
        this.mContext = mContext;
        this.mCartArrList = mBoutiqueArrList;
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
                holder = new CartViewHolder(inflater.inflate(R.layout.item_cart, parent,false));
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


        if (holder instanceof CartViewHolder) {
            mCartViewholder = (CartViewHolder) holder;
            final CartBean cartBean = mCartArrList.get(position);
            GoodDetailsBean goods = cartBean.getGoods();
            if (goods == null) {
                return;
            }
            mCartViewholder.mtvCartPrice.setText(goods.getCurrencyPrice());
            mCartViewholder.mtvCartName.setText(goods.getGoodsName());
            mCartViewholder.mtvCartNum.setText("("+cartBean.getCount()+")");
            ImageUtils.setBoutiqueThumb(I.DOWNLOAD_GOODS_THUMB_URL+goods.getGoodsThumb(),mCartViewholder.mnivCartThunb);

            AddDeleteCartListener listener = new AddDeleteCartListener(cartBean);
            mCartViewholder.mivAddCart.setOnClickListener(listener);
            mCartViewholder.mivDeleteCart.setOnClickListener(listener);
            mCartViewholder.mckCheck.setChecked(cartBean.isChecked());
            mCartViewholder.mckCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cartBean.setChecked(isChecked);
                    new UpdateCartTask(mContext, cartBean).execute();
                }
            });
        }
    }


    class AddDeleteCartListener implements View.OnClickListener {
        CartBean cartBean;

        public AddDeleteCartListener(CartBean cartBean) {
            this.cartBean = cartBean;
        }

        @Override
        public void onClick(View v) {
            cartBean.setChecked(true);
            switch (v.getId()) {
                case R.id.iv_add_cart:
                    Log.i("main", "AddDeleteCartListener"+"add已经执行");
                    Utils.addCart(mContext, cartBean.getGoods());
                    break;
                case R.id.iv_delete_cart:
                    Log.i("main", "AddDeleteCartListener"+"delete已经执行");
                    Utils.DeleteCaet(mContext, cartBean.getGoods());
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return mCartArrList == null ? 1 : mCartArrList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }
    public void initItems(ArrayList<CartBean> list) {
        if (mCartArrList != null && !mCartArrList.isEmpty()) {
            mCartArrList.clear();
        }
        mCartArrList.addAll(list);
        notifyDataSetChanged();
    }

    public void additems(ArrayList<CartBean> list) {
        mCartArrList.addAll(list);
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mCartRelativrLayout;
        NetworkImageView mnivCartThunb;
        TextView mtvCartName;
        ImageView mivAddCart;
        TextView mtvCartNum;
        ImageView mivDeleteCart;
        TextView  mtvCartPrice;
        CheckBox   mckCheck;

        public CartViewHolder(View itemView) {
            super(itemView);
            mCartRelativrLayout = (RelativeLayout) itemView.findViewById(R.id.relative_cart_layout);
            mnivCartThunb = (NetworkImageView) itemView.findViewById(R.id.iv_cart_avatar);
            mtvCartName = (TextView) itemView.findViewById(R.id.tv_cart_name);
            mivAddCart = (ImageView) itemView.findViewById(R.id.iv_add_cart);
            mtvCartNum = (TextView) itemView.findViewById(R.id.tv_cart_num);
            mivDeleteCart = (ImageView) itemView.findViewById(R.id.iv_delete_cart);
            mtvCartPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
            mckCheck = (CheckBox) itemView.findViewById(R.id.cb_check);
        }
    }
}
