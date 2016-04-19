package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by sks on 2016/4/19.
 */
public class CategoryAdaptar extends BaseExpandableListAdapter {

    Context mContext;
    ArrayList<CategoryGroupBean> groupList;
    ArrayList<ArrayList<CategoryChildBean>> childList;

    public CategoryAdaptar(Context context, ArrayList<CategoryGroupBean> groupList, ArrayList<ArrayList<CategoryChildBean>> childList) {
        this.mContext = context;
        this.groupList = groupList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return groupList == null ? 0 : groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition) == null || childList == null ? 0 : childList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = View.inflate(mContext, R.layout.item_category_group, null);
            holder.mivExpand = (ImageView) convertView.findViewById(R.id.ivExpand);
            holder.mtvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mivGroup = (NetworkImageView) convertView.findViewById(R.id.ivGroup);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        CategoryGroupBean groupBean = groupList.get(groupPosition);
        holder.mtvName.setText(groupBean.getName());
        String imgurl = groupBean.getImageUrl();
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_GROUP_IMAGE_URL;
        ImageUtils.setBoutiqueThumb(url + imgurl, holder.mivGroup);

        if (isExpanded) {
            holder.mivExpand.setImageResource(R.drawable.expand_off);
        } else {
            holder.mivExpand.setImageResource(R.drawable.expand_on);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_category_child, null);
            holder = new ChildViewHolder();
            holder.mivChild = (NetworkImageView) convertView.findViewById(R.id.ivChild);
            holder.mRelativelayout = (RelativeLayout) convertView.findViewById(R.id.layout_child);
            holder.mtvchildname = (TextView) convertView.findViewById(R.id.tvChildName);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        final CategoryChildBean childBean = childList.get(groupPosition).get(childPosition);
        holder.mtvchildname.setText(childBean.getName());
        String imgurl = childBean.getImageUrl();
        String url = I.DOWNLOAD_DOWNLOAD_CATEGORY_CHILD_IMAGE_URL;
        ImageUtils.setBoutiqueThumb(url + imgurl, holder.mivChild);
        holder.mRelativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CategoryActivity.class);
                intent.putExtra(I.CategoryChild.CAT_ID, childBean.getId());
                ArrayList<CategoryChildBean> arraychildList = childList.get(groupPosition);
                intent.putExtra("children", arraychildList);
                intent.putExtra(I.CategoryGroup.NAME, getGroup(groupPosition).getName());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ChildViewHolder {
        RelativeLayout mRelativelayout;
        NetworkImageView mivChild;
        TextView mtvchildname;
    }

    class GroupViewHolder {
        NetworkImageView mivGroup;
        TextView mtvName;
        ImageView mivExpand;
    }

    public void addiTems(ArrayList<CategoryGroupBean> groupList, ArrayList<ArrayList<CategoryChildBean>> childList) {
        this.groupList.addAll(groupList);
        this.childList.addAll(childList);
        notifyDataSetChanged();
    }
}
