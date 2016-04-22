package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.easemob.chat.EMMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.task.UpdateCartTask;

/**
 * Created by clawpo on 16/3/28.
 */
public class Utils {
    public static String getPackageName(Context context){
        return context.getPackageName();
    }
    
    public static void showToast(Context context,String text,int time){
        Toast.makeText(context,text,time).show();
    }
    
    public static void showToast(Context context,int  strId,int time){
        Toast.makeText(context, strId, time).show();
    }

    /**
     * 将数组转换为ArrayList集合
     * @param ary
     * @return
     */
    public static <T> ArrayList<T> array2List(T[] ary){
        List<T> list = Arrays.asList(ary);
        ArrayList<T> arrayList=new ArrayList<T>(list);
        return arrayList;
    }

    /**
     * 添加新的数组元素：数组扩容
     * @param array：数组
     * @param t：添加的数组元素
     * @return：返回添加后的数组
     */
    public static <T> T[] add(T[] array,T t){
        array=Arrays.copyOf(array, array.length+1);
        array[array.length-1]=t;
        return array;
    }

    /**
     * 返回发送消息者，发送消息者可能是群聊中成员或单聊中的好友
     * @param chatType：群聊/单聊
     * @param groupId：群聊的groupId或单聊中的登陆者userName
     * @param userName：发送消息者的userName
     * @return
     */
    public static UserBean getMessageFromUser(EMMessage.ChatType chatType, String groupId, String userName){
        ArrayList<UserBean> userList = null;
        switch (chatType) {
            case GroupChat://群聊
               // HashMap<String,ArrayList<UserBean>> groupMembers = FuLiCenterApplication.getInstance().getGroupMembers();
                //获取指定groupId的群聊成员集合
               // userList = groupMembers.get(groupId);
                break;
            case ChatRoom:
                break;
            default://单聊
                userList = FuLiCenterApplication.getInstance().getContactList();
                break;
        }
        //获取发送消息者
        UserBean user = new UserBean();
        user.setUserName(userName);
        if(userList==null ||userList.isEmpty()){
            return null;
        }
        int id = userList.indexOf(user);
        if(id>=0){
            return userList.get(id);
        }
        return null;
    }

    public static int px2dp(Context context,int px) {
        int  density = (int) context.getResources().getDisplayMetrics().density;
        return px / density;
    }
    public static int dp2px(Context context,int dp) {
        int  density = (int) context.getResources().getDisplayMetrics().density;
        return density/dp;
    }


    public static void addCart(Context mContext, GoodDetailsBean goods) {
        boolean isexist=false;
        String userName = null;
        UserBean user = FuLiCenterApplication.getInstance().getUser();
        if (user != null) {
            userName = user.getUserName();
        } else {
            userName = "";
        }
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        int goodsId = goods.getGoodsId();
        CartBean cart = null;
        for(int i=0;i<cartList.size()&&!isexist;i++) {
            if (goodsId == cartList.get(i).getGoodsId()) {
                int count = cartList.get(i).getCount();
                cartList.get(i).setCount(++count);
                cart = cartList.get(i);
                isexist = true;
            }
        }

        if (!isexist) {
            cart = new CartBean(0, userName, goods.getGoodsId(), 1, true);
        }

        Log.i("main", "addCart" + cart.getCount());
        new UpdateCartTask(mContext,cart).execute();
    }


    public static void DeleteCaet(Context mContext, GoodDetailsBean goods) {
        boolean isexist = false;
        CartBean cartBean = null;
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        int goodsId = goods.getGoodsId();
        for(int i=0;i<cartList.size()&&!isexist;i++) {
            if (goodsId == cartList.get(i).getGoodsId()) {
                int count = cartList.get(i).getCount();
                cartList.get(i).setCount(count - 1);
                cartBean = cartList.get(i);
                isexist = true;
            }
        }
        if (isexist) {
            new UpdateCartTask(mContext, cartBean).execute();
        }
    }

    public static int  sumCartCount() {
        ArrayList<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        int count=0;
        for(int i=0;i<cartList.size();i++) {
            count += cartList.get(i).getCount();
        }
        return count;
    }
}
