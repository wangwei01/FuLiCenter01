package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.db.UserDao;
import cn.ucai.fulicenter.task.DownLoadCartListTask;
import cn.ucai.fulicenter.task.DownLoadCollectCountTask;
import cn.ucai.fulicenter.task.DownLoadContactListTask;
import cn.ucai.fulicenter.task.DownLoadContactTask;

/**
 * 开屏页
 */
public class SplashActivity extends BaseActivity {
    SplashActivity mcontext;

    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.activity_splash);
        super.onCreate(arg0);
        mcontext = this;


    }

    @Override
    protected void onStart() {
        super.onStart();

        final String username = FuLiCenterApplication.getInstance().getUserName();
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            new DownLoadContactTask(mcontext, username, 0, 20).execute();
            new DownLoadContactListTask(mcontext, username, 0, 20).execute();
            new DownLoadCollectCountTask(mcontext).execute();
            new DownLoadCartListTask(mcontext, username, 0, 20).execute();
            Intent intent = new Intent("update_cart");
            mcontext.sendStickyBroadcast(intent);
        }
        new Thread(new Runnable() {
            public void run() {
                if (DemoHXSDKHelper.getInstance().isLogined()) {

                    UserDao dao = new UserDao(mcontext);
                    UserBean userbean = dao.findUserByUserName(username);
                    FuLiCenterApplication.getInstance().setUser(userbean);

                    Intent intent = new Intent("update_user");
                    sendBroadcast(intent);


					/*// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					//等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}*/

                    //进入主页面
                    startActivity(new Intent(SplashActivity.this, FuLiCenterMainActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                }
                startActivity(new Intent(SplashActivity.this, FuLiCenterMainActivity.class));
                finish();
            }
        }).start();
    }

    /**
     * 获取当前应用程序的版本号
     */
    private String getVersion() {
        String st = getResources().getString(R.string.Version_number_is_wrong);
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return st;
        }
    }
}
