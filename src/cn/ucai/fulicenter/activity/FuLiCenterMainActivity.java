package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import cn.ucai.fulicenter.R;

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
    int currentindex = -1;
    RadioButton[] mRagioButtonArr;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_fulicenter_main);
        mRagioButtonArr=new RadioButton[5];
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (index == -1) {
            index=0;
        }
        setDefaultchecked(index);
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
        mRagioButtonArr[0] = mrbNewgoods;
        mRagioButtonArr[1] = mrbBoutique;
        mRagioButtonArr[2] = mrbCategory;
        mRagioButtonArr[3] = mrbCart;
        mRagioButtonArr[4] = mrbPersonCenter;
    }

    public void onCheckedChange(View view) {
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
            index=3;
            break;
            case R.id.layout_Personal_Center:
            index=4;
            break;
        }
        if (currentindex != index) {
            currentindex = index;
            setDefaultchecked(currentindex);
        }
    }
}
