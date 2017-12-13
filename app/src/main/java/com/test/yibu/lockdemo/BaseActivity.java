package com.test.yibu.lockdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.test.yibu.lockdemo.util.TintStatusBarUtil;
import com.test.yibu.lockdemo.view.TitleView;

import butterknife.ButterKnife;

/**
 * Created by zhengcf on 2017/11/20.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        TintStatusBarUtil.translucentStatusBar(this);
        TintStatusBarUtil.statusBarLightMode(this);
    }

    public abstract int getLayoutRes();

    /**
     * 初始化TitleView
     */
    protected void initTitleBar(View titleView) {
        if (null != titleView) {
            TintStatusBarUtil.fixTranslucentStatusBarHeight(titleView, this);
        }
    }

}
