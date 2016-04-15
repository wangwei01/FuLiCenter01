package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.easemob.util.HanziToPinyin;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.fulicenter.Constant;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.activity.ChatActivity;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.bean.GroupBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.data.RequestManager;
import cn.ucai.fulicenter.domain.User;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     *
     * @param username
     * @return
     */
    public static User getUserInfo(String username) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if (user == null) {
            user = new User(username);
        }

        if (user != null) {
            //demo没有这些数据，临时填充
            if (TextUtils.isEmpty(user.getNick()))
                user.setNick(username);
        }
        return user;
    }


    public static UserBean getUserBeanInfo(String username) {
        UserBean user = SuperWeChatApplication.getInstance().getUserList().get(username);
        if (user == null) {
            user = new UserBean(username);
        }

        if (user != null) {
            //demo没有这些数据，临时填充
            if (TextUtils.isEmpty(user.getNick()))
                user.setNick(username);
        }
        return user;
    }

    public static GroupBean getGroupBeanInfo(String groupname) {
        ArrayList<GroupBean> groupList = SuperWeChatApplication.getInstance().getGroupList();
        for (GroupBean groupBean : groupList) {
            if (groupBean.getGroupId().equals(groupname)) {
                return groupBean;
            }
        }
        return null;
    }

    public static void setGroupAvatar(String groupname, NetworkImageView imageView) {
        GroupBean groupBean = getGroupBeanInfo(groupname);
        setGroupBeanAvatar(groupBean, imageView);
    }


    public static void setGroupBeanAvatar(GroupBean groupBean, NetworkImageView imageView) {
        imageView.setDefaultImageResId(R.drawable.group_icon);
        if (groupBean != null && groupBean.getAvatar() != null) {
            String path = I.DOWNLOAD_AVATAR_URL + groupBean.getAvatar();
            imageView.setImageUrl(path, RequestManager.getImageLoader());
        } else {
            imageView.setErrorImageResId(R.drawable.group_icon);
        }
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        User user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    public static void setUserBeanAvatar(String username, NetworkImageView imageView) {
        UserBean userBean = getUserBeanInfo(username);
        if (userBean != null && userBean.getAvatar() != null) {
            setavatar(userBean,imageView);
        } else {
            for (UserBean userBean1 : ChatActivity.mcurrentmembers) {
                if (ChatActivity.mcurrentmembers != null) {
                    Log.i("main", "setUserBeanAvatar" + userBean1.toString());
                    if (username.equals(userBean1.getUserName())) {
                        if (userBean1 != null && userBean1.getAvatar() != null) {
                            setavatar(userBean1, imageView);
                        }
                    }
                }
            }
        }
    }


    public static void setavatar( UserBean userBean,NetworkImageView imageView) {
        imageView.setDefaultImageResId(R.drawable.default_avatar);
        if (userBean != null && userBean.getAvatar() != null) {
            imageView.setImageUrl(I.DOWNLOAD_AVATAR_URL + userBean.getAvatar(), RequestManager.getImageLoader());
        } else {
            imageView.setErrorImageResId(R.drawable.default_avatar);
        }
    }

    public static void setUserBeanAvatar(UserBean userBean, NetworkImageView imageView) {
        if (userBean != null && userBean.getAvatar() != null) {
            imageView.setImageUrl(I.DOWNLOAD_AVATAR_URL + userBean.getAvatar(), RequestManager.getImageLoader());
        } else {
            imageView.setErrorImageResId(R.drawable.default_avatar);
        }
    }


    /**
     * 设置当前用户头像
     */
    public static void setCurrentUserAvatar(Context context, ImageView imageView) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if (user != null && user.getAvatar() != null) {
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    public static void setCurrentUserBeanAvatar(NetworkImageView imageView) {
        UserBean user = SuperWeChatApplication.getInstance().getUser();
        if (user != null && user.getAvatar() != null) {
            imageView.setImageUrl(I.DOWNLOAD_AVATAR_URL + user.getAvatar(), RequestManager.getImageLoader());
        } else {
            imageView.setErrorImageResId(R.drawable.default_avatar);
        }
    }


    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username, TextView textView) {
        User user = getUserInfo(username);
        if (user != null) {
            textView.setText(user.getNick());
        } else {
            textView.setText(username);
        }
    }

    public static void setGroupMembersNick(String groupid, String username, TextView textView) {
        UserBean userBean = getGroupMemberInfo(groupid, username);
        if (userBean != null) {
            if (username.equals(userBean.getUserName())) {
                textView.setText(userBean.getNick());
            } else {
                textView.setText(username);
            }
        }
    }

    public static UserBean getGroupMemberInfo(String groupid, String username) {

        HashMap<String, ArrayList<UserBean>> groupMembers = SuperWeChatApplication.getInstance().getGroupMembers();
        ArrayList<UserBean> userBeenList = groupMembers.get(groupid);
        if (userBeenList != null) {
            for (UserBean userBean : userBeenList) {
             //   Log.i("main","getGroupMemberInfo"+userBean.toString());
                if (username.equals(userBean.getUserName())) {
                    return userBean;
                }
            }
        }
            return null;
    }

    public static void setGroupMemberNick(String username, TextView textView) {
        ArrayList<UserBean> mcurrentmembers = ChatActivity.mcurrentmembers;
        for (UserBean userBean1 : mcurrentmembers) {
            if (username.equals(userBean1.getUserName())) {
                if (userBean1!=null) {
                 //   Log.i("main","setGroupMemberNick"+userBean1.toString());
                    textView.setText(userBean1.getNick());
                }
            }
        }
    }

    public static void setUserBeanNick(String username, TextView textView) {

        UserBean userBean = getUserBeanInfo(username);
        if (userBean != null) {
            textView.setText(userBean.getNick());
        } else {
            textView.setText(username);
        }

    }


    public static void setUserBeanNickNF(UserBean user, TextView textView) {
        if (user != null) {
            textView.setText(user.getNick());
        } else {
            textView.setText(user.getUserName());
        }
    }

    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if (textView != null) {
            textView.setText(user.getNick());
        }
    }

    public static void setCurrentUserBeanNick(TextView textView) {
        UserBean user = SuperWeChatApplication.getInstance().getUser();
        if (textView != null) {
            textView.setText(user.getNick());
        }
    }


    /**
     * 保存或更新某个用户
     */
    public static void saveUserInfo(User newUser) {
        if (newUser == null || newUser.getUsername() == null) {
            return;
        }
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
    }

    public static void setUserHearder(String username, UserBean user) {
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUserName();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
    }

}
