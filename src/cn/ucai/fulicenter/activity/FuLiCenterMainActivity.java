package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CartFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodFragment;
import cn.ucai.fulicenter.fragment.PersonCenterFragment;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by sks on 2016/4/16.
 */
public class FuLiCenterMainActivity  extends  BaseActivity {
    RadioButton mrbNewgoods;
    RadioButton mrbBoutique;
    RadioButton mrbCategory;
    RadioButton mrbCart;
    RadioButton mrbPersonCenter;
    int index;
    int currentindex;
    RadioButton[] mRagioButtonArr;

    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonCenterFragment mPersonCenterFragment;

    CartFragment   mCartFragment;
    Fragment[] myFragmentArr=new Fragment[5];
    FuLiCenterMainActivity mContext;
    TextView mtvCartHint;

    String action;
    UserBean currentuser;

    CartChangedReceiver  mCartChangedReceiver;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_fulicenter_main);
        mContext = this;
        mRagioButtonArr=new RadioButton[5];
        registerCartChangedReceiver();
        initView();
        initFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_contain, mNewGoodFragment)
                .add(R.id.fragment_contain, mBoutiqueFragment)
                .hide(mBoutiqueFragment)
                .add(R.id.fragment_contain, mCategoryFragment)
                .hide(mCategoryFragment)
               // .add(R.id.fragment_contain, mPersonCenterFragment)
              //  .hide(mPersonCenterFragment)
                .add(R.id.fragment_contain, mCartFragment)
                .hide(mCartFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mPersonCenterFragment = new PersonCenterFragment();
        mCartFragment = new CartFragment();
        myFragmentArr[0] = mNewGoodFragment;
        myFragmentArr[1] = mBoutiqueFragment;
        myFragmentArr[2] = mCategoryFragment;
        myFragmentArr[3] = mCartFragment;
        myFragmentArr[4] = mPersonCenterFragment;
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_cart_hint, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_time_up:
                break;
            case R.id.mi_time_down:
                break;
            case R.id.mi_price_up:
                break;
            case R.id.mi_price_down:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        setDefaultchecked(index);
        setFragment();
    }



    @Override
    protected void onStart() {
        super.onStart();
        action = getIntent().getStringExtra("action");
    }

    private void setDefaultchecked(int index) {
        for(int i=0;i<mRagioButtonArr.length;i++) {
            if (index == i) {
                mRagioButtonArr[i].setChecked(true);
            } else {
                mRagioButtonArr[i].setChecked(false);
            }

        }
    }

    private void initView() {
        mrbNewgoods = (RadioButton) findViewById(R.id.layout_New_Goods);
        mrbBoutique = (RadioButton) findViewById(R.id.layout_Boutique);
        mrbCategory = (RadioButton) findViewById(R.id.layout_Category);
        mrbCart = (RadioButton) findViewById(R.id.layout_cart);
        mrbPersonCenter = (RadioButton) findViewById(R.id.layout_Personal_Center);

        mtvCartHint = (TextView) findViewById(R.id.tvCartHint);

        mRagioButtonArr[0] = mrbNewgoods;
        mRagioButtonArr[1] = mrbBoutique;
        mRagioButtonArr[2] = mrbCategory;
        mRagioButtonArr[3] = mrbCart;
        mRagioButtonArr[4] = mrbPersonCenter;
    }

    public void onCheckedChange(View view) {
        currentuser = FuLiCenterApplication.getInstance().getUser();
        switch (view.getId()) {
            case R.id.layout_New_Goods:
            index=0;
            break;
            case R.id.layout_Boutique:
            index=1;
            break;
            case R.id.layout_Category:
            index=2;
            break;
            case R.id.layout_cart:
                if (currentuser != null) {
                    index = 3;
                } else {
                    gotoLogin("cart");
                }
            break;
            case R.id.layout_Personal_Center:
                if (currentuser != null) {
                    index = 4;
                } else {
                    gotoLogin("person");
                }
            break;
        }



        if (currentindex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(myFragmentArr[currentindex]);
            if (!myFragmentArr[index].isAdded()) {
                trx.add(R.id.fragment_contain, myFragmentArr[index]);
            }
            trx.show(myFragmentArr[index]).commit();
        }


        setDefaultchecked(index);
        currentindex = index;
    }

    private void gotoLogin(String person) {
        Intent intent = new Intent(FuLiCenterMainActivity.this, LoginActivity.class);
        intent.putExtra("action", person);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        action = intent.getStringExtra("action");
    }

    private void setFragment() {
        currentuser = FuLiCenterApplication.getInstance().getUser();
        action = getIntent().getStringExtra("action");
        if (action != null && currentuser != null) {
            if (action.equals("person")) {
                index=4;

            }
            if (action.equals("cart")) {
                index=3;

            }
            getIntent().removeExtra("action");
        }

        if (currentuser == null && currentindex == 4) {
            index = 0;
        }

        if (currentindex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(myFragmentArr[currentindex]);
            if (!myFragmentArr[index].isAdded()) {
                trx.add(R.id.fragment_contain, myFragmentArr[index]);
            }
            trx.show(myFragmentArr[index]).commit();
        }

        setDefaultchecked(index);
        currentindex = index;
    }


    class CartChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.sumCartCount();
            if (count > 0) {
                mtvCartHint.setText("" + count);
                mtvCartHint.setVisibility(View.VISIBLE);
            } else {
                mtvCartHint.setVisibility(View.GONE);
            }
        }
    }

    /**
     *
     * osajfpojaofjsajofjsal
     */
    private void registerCartChangedReceiver() {
        mCartChangedReceiver = new CartChangedReceiver();
        IntentFilter filter = new IntentFilter("update_cart");
        mContext.registerReceiver(mCartChangedReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCartChangedReceiver != null) {
            mContext.unregisterReceiver(mCartChangedReceiver);
        }
    }
}
