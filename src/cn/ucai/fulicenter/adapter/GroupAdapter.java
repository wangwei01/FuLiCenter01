/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.utils.UserUtils;

public class GroupAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private String newGroup;
    private String addPublicGroup;

    ArrayList<GroupBean> mGroupList;
    Context mContext;


    public GroupAdapter(Context context, ArrayList<GroupBean> groupList) {
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mGroupList = groupList;
        newGroup = context.getResources().getString(R.string.The_new_group_chat);
        addPublicGroup = context.getResources().getString(R.string.add_public_group_chat);
    }

    @Override
    public int getCount() {
        return mGroupList == null ? 3 : mGroupList.size() + 3;
    }

    @Override
    public GroupBean getItem(int position) {
        if (position >= 3) {
            return mGroupList.get(position - 3);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else if (position == 2) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == 0) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.search_bar_with_padding, null);
            }
            final EditText query = (EditText) convertView.findViewById(R.id.query);
            final ImageButton clearSearch = (ImageButton) convertView.findViewById(R.id.search_clear);
            query.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //getFilter().filter(s);
                    if (s.length() > 0) {
                        clearSearch.setVisibility(View.VISIBLE);
                    } else {
                        clearSearch.setVisibility(View.INVISIBLE);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void afterTextChanged(Editable s) {
                }
            });
            clearSearch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    query.getText().clear();
                }
            });
        } else if (getItemViewType(position) == 1) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_add_group, null);
            }
            ((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.create_group);
            ((TextView) convertView.findViewById(R.id.name)).setText(newGroup);
        } else if (getItemViewType(position) == 2) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_add_group, null);
            }
            ((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.add_public_group);
            ((TextView) convertView.findViewById(R.id.name)).setText(addPublicGroup);
            ((TextView) convertView.findViewById(R.id.header)).setVisibility(View.VISIBLE);

        } else {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_group, null);
            }
            String groupid = getItem(position).getGroupId();
            String name = getItem(position).getName();
            Log.i("main", groupid);
            ((TextView) convertView.findViewById(R.id.name)).setText(name);
            UserUtils.setGroupAvatar(groupid, (NetworkImageView) convertView.findViewById(R.id.avatar));
        }

        return convertView;
    }

    public void initList(ArrayList<GroupBean> groupBean) {
        if (groupBean != null) {
            mGroupList.addAll(groupBean);
            notifyDataSetChanged();
        }
    }


    public void addItem(GroupBean groupbean) {
        if (groupbean != null) {
            mGroupList.add(groupbean);
            notifyDataSetChanged();
        }
    }
}