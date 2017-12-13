package com.test.yibu.lockdemo.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.yibu.lockdemo.R;

/**
 * Create by Marno on 2017/7/5 下午1:01
 * Function：通用的标题栏封装
 * Desc：
 */
public class TitleView extends FrameLayout {

    private static final int DEFAULT_TEXT_COLOR = -1;
    private static final int DEFAULT_TEXT_BG_COLOR = 0;
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int DEFAULT_SUB_TEXT_SIZE = 12;

    private Context mContext;

    //容器
    private LinearLayout mLeftLayout;
    private LinearLayout mCenterLayout;
    private LinearLayout mRightLayout;

    //左侧
    public TextView mLeftTv;//左标题
    private ImageView mLeftIv;//左图标
    private int mLeftTextSize;//左标题字号
    private int mLeftTextColor;//左标题颜色
    private int mLeftTextBackgroundColor;//左标题背景
    private int mLeftDrawable;//左图标
    private int mLeftDrawableWidth;//左图标宽度
    private int mLeftDrawableHeight;//左图标高度
    private int mLeftDrawablePadding;//左图标内边距
    private int mLeftTextBackgroundResource;//左标题背景图
    private CharSequence mLeftText;//左侧标题文字
    private OnClickListener mLeftTextClickListener;

    //中间
    public TextView mTitleTv;//主标题
    private CharSequence mMainText;//主标题文字
    private int mMainTextSize;//主标题字号
    private int mMainTextColor;//主标题颜色
    private int mMainTextBackgroundColor;//主标题背景色
    private int mMainTextBackgroundResource;//主标题背景图片
    private boolean isTitleFakeBold;//标题字体为粗体
    private OnClickListener mCenterTextClickListener;

    //右侧
    public TextView mRightTv;//右标题
    private ImageView mRightIv;//左图标
    private int mRightTextSize;//右标题字号
    private int mRightTextColor;//右标题颜色
    private int mRightTextBackgroundColor;//右标题背景色
    private int mRightDrawable;//右图片
    private int mRightDrawableWidth;//右图标宽度
    private int mRightDrawableHeight;//右图标高度
    private int mRightDrawablePadding;//右图片内边距
    private int mRightTextBackgroundResource;//右标题背景图
    private CharSequence mRightText;//右侧标题文字
    private OnClickListener mRightTextClickListener;

    //底部
    private View mDividerView;//底部分割线
    private int mDividerHeight;////底部分割线高度
    private int mDividerResource;////底部分割线

    //其他
    private int mHorizontalPadding;//水平（左右）边距


    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initDefaulfAttrs(context);
//        initCustomAttrs(context, attrs);
        initAttributes(context, attrs);
        initViews(context);
    }

    /**
     * 初始化默认参数
     */
    private void initDefaulfAttrs(Context context) {
        //左侧
        mLeftTextSize = Util.dp2px(context, 16.0f);
//        mLeftTextColor = Color.parseColor("#333333");
//        mLeftTextBackgroundColor = 0;
//        mLeftTextBackgroundResource = 0;
//        mLeftDrawable = 0;
//        mLeftDrawablePadding = 0;
//        mLeftText = null;
//
//        //中间
//        mMainTextSize = 0;
//        mMainTextColor = 0;
//        mMainTextBackgroundColor = 0;
//        mMainTextBackgroundResource = 0;
//        isTitleFakeBold = false;
//
//        //右侧
//        mRightTextSize = 0;
//        mRightTextColor = 0;
//        mRightTextBackgroundColor = 0;
//        mRightDrawable = 0;
//        mRightDrawablePadding = 0;
//        mRightTextBackgroundResource = 0;
//        mRightText = null;
//
//        //底部
//        mDividerView = null;
//        mDividerHeight = 0;
//
//        //其他
//        mHorizontalPadding = 0;
    }

    /**
     * 初始化自定义属性
     *
     * @param context 上下文
     * @param attrs   xml中设置的属性
     */
    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScannerView);
        final int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    /**
     * 初始化自定义属性
     *
     * @param context 上下文
     * @param attrs   xml中设置的属性
     */
    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleView);

        this.mLeftText = ta.getString(R.styleable.TitleView_titleLeftText);
        this.mLeftTextSize = ta.getDimensionPixelSize(R.styleable.TitleView_kk_leftTextSize, Util.dp2px(context, 16.0F));
        this.mLeftTextColor = ta.getColor(R.styleable.TitleView_kk_leftTextColor, -1);
        this.mLeftTextBackgroundResource = ta.getResourceId(R.styleable.TitleView_kk_leftTextBackgroundResource, -1);
        this.mLeftTextBackgroundColor = ta.getColor(R.styleable.TitleView_kk_leftTextBackgroundColor, 0);
        this.mLeftDrawable = ta.getResourceId(R.styleable.TitleView_titleLeftIco, -1);
        this.mLeftDrawableWidth = ta.getDimensionPixelSize(R.styleable.TitleView_titleLeftIcoWith, 0);
        this.mLeftDrawableHeight = ta.getDimensionPixelSize(R.styleable.TitleView_titleLeftIcoHeight, 0);
        this.mLeftDrawablePadding = ta.getDimensionPixelSize(R.styleable.TitleView_kk_leftTextDrawablePadding, 0);

        this.mMainText = ta.getString(R.styleable.TitleView_titleText);
        this.mMainTextSize = ta.getDimensionPixelSize(R.styleable.TitleView_kk_mainTextSize, Util.dp2px(context, 16.0F));
        this.mMainTextColor = ta.getColor(R.styleable.TitleView_kk_mainTextColor, -1);
        this.mMainTextBackgroundColor = ta.getColor(R.styleable.TitleView_kk_mainTextBackgroundColor, 0);
        this.mMainTextBackgroundResource = ta.getResourceId(R.styleable.TitleView_kk_mainTextBackgroundResource, -1);

        this.mRightText = ta.getString(R.styleable.TitleView_titleRightText);
        this.mRightTextSize = ta.getDimensionPixelSize(R.styleable.TitleView_kk_rightTextSize, Util.dp2px(context, 16.0F));
        this.mRightTextColor = ta.getColor(R.styleable.TitleView_kk_rightTextColor, -1);
        this.mRightTextBackgroundResource = ta.getResourceId(R.styleable.TitleView_kk_rightTextBackgroundResource, -1);
        this.mRightTextBackgroundColor = ta.getColor(R.styleable.TitleView_kk_rightTextBackgroundColor, 0);
        this.mRightDrawable = ta.getResourceId(R.styleable.TitleView_titleRightIco, -1);
        this.mRightDrawableWidth = ta.getDimensionPixelSize(R.styleable.TitleView_titleRightIcoWith, 0);
        this.mRightDrawableHeight = ta.getDimensionPixelSize(R.styleable.TitleView_titleRightIcoHeight, 0);
        this.mRightDrawablePadding = ta.getDimensionPixelSize(R.styleable.TitleView_kk_rightTextDrawablePadding, 0);

        this.mDividerHeight = ta.getDimensionPixelSize(R.styleable.TitleView_kk_dividerHeight, 0);
        this.mDividerResource = ta.getResourceId(R.styleable.TitleView_kk_dividerResource, -1);
        this.isTitleFakeBold = ta.getBoolean(R.styleable.TitleView_kk_mainTextFakeBold, false);
        mHorizontalPadding = ta.getDimensionPixelSize(R.styleable.TitleView_kk_horizontalPadding, Util.dp2px(mContext, 6.0F));

        ta.recycle();
    }


    private void initCustomAttr(int attr, TypedArray ta) {
        if (attr == R.styleable.TitleView_titleLeftText) {
            mLeftText = ta.getString(R.styleable.TitleView_titleLeftText);
        } else if (attr == R.styleable.TitleView_kk_leftTextSize) {
            mLeftTextSize = ta.getDimensionPixelSize(R.styleable.TitleView_kk_leftTextSize, mLeftTextSize);
        } else if (attr == R.styleable.TitleView_kk_leftTextColor) {
            mLeftTextColor = ta.getColor(R.styleable.TitleView_kk_leftTextColor, -1);
        } else if (attr == R.styleable.TitleView_kk_leftTextBackgroundResource) {
            mLeftTextBackgroundResource = ta.getResourceId(R.styleable.TitleView_kk_leftTextBackgroundResource, -1);
        } else if (attr == R.styleable.TitleView_kk_leftTextBackgroundColor) {
            mLeftTextBackgroundColor = ta.getColor(R.styleable.TitleView_kk_leftTextBackgroundColor, 0);
        } else if (attr == R.styleable.TitleView_titleLeftIco) {
            mLeftDrawable = ta.getResourceId(R.styleable.TitleView_titleLeftIco, -1);
        } else if (attr == R.styleable.TitleView_titleLeftIcoWith) {
            mLeftDrawableWidth = ta.getDimensionPixelSize(R.styleable.TitleView_titleLeftIcoWith, 0);
        } else if (attr == R.styleable.TitleView_titleLeftIcoHeight) {
            mLeftDrawableHeight = ta.getDimensionPixelSize(R.styleable.TitleView_titleLeftIcoHeight, 0);
        } else if (attr == R.styleable.TitleView_kk_leftTextDrawablePadding) {
            mLeftDrawablePadding = ta.getDimensionPixelSize(R.styleable.TitleView_kk_leftTextDrawablePadding, 0);
        } else if (attr == R.styleable.TitleView_titleText) {
            mMainText = ta.getString(R.styleable.TitleView_titleText);
        } else if (attr == R.styleable.TitleView_kk_mainTextSize) {
            mMainTextSize = ta.getDimensionPixelSize(R.styleable.TitleView_kk_mainTextSize, Util.dp2px(mContext, 16.0f));
        } else if (attr == R.styleable.TitleView_kk_mainTextColor) {
            mMainTextColor = ta.getColor(R.styleable.TitleView_kk_mainTextColor, -1);
        } else if (attr == R.styleable.TitleView_kk_mainTextBackgroundColor) {
            mMainTextBackgroundColor = ta.getColor(R.styleable.TitleView_kk_mainTextBackgroundColor, 0);
        } else if (attr == R.styleable.TitleView_kk_mainTextBackgroundResource) {
            mMainTextBackgroundResource = ta.getResourceId(R.styleable.TitleView_kk_mainTextBackgroundResource, -1);
        } else if (attr == R.styleable.TitleView_titleRightText) {
            mRightText = ta.getString(R.styleable.TitleView_titleRightText);
        } else if (attr == R.styleable.TitleView_kk_rightTextSize) {
            mRightTextSize = ta.getDimensionPixelSize(R.styleable.TitleView_kk_rightTextSize, Util.dp2px(mContext, 16.0f));
        } else if (attr == R.styleable.TitleView_kk_rightTextColor) {
            mRightTextColor = ta.getColor(R.styleable.TitleView_kk_rightTextColor, -1);
        } else if (attr == R.styleable.TitleView_kk_rightTextBackgroundResource) {
            mRightTextBackgroundResource = ta.getResourceId(R.styleable.TitleView_kk_rightTextBackgroundResource, -1);
        } else if (attr == R.styleable.TitleView_kk_rightTextBackgroundColor) {
            mRightTextBackgroundColor = ta.getColor(R.styleable.TitleView_kk_rightTextBackgroundColor, 0);
        } else if (attr == R.styleable.TitleView_titleRightIco) {
            mRightDrawable = ta.getResourceId(R.styleable.TitleView_titleRightIco, -1);
        } else if (attr == R.styleable.TitleView_kk_rightTextDrawablePadding) {
            mRightDrawablePadding = ta.getDimensionPixelSize(R.styleable.TitleView_kk_rightTextDrawablePadding, 0);
        } else if (attr == R.styleable.TitleView_kk_horizontalPadding) {
            mHorizontalPadding = ta.getDimensionPixelSize(R.styleable.TitleView_kk_horizontalPadding, Util.dp2px(mContext, 6.0F));
        } else if (attr == R.styleable.TitleView_kk_dividerHeight) {
            mDividerHeight = ta.getDimensionPixelSize(R.styleable.TitleView_kk_dividerHeight, 0);
        } else if (attr == R.styleable.TitleView_kk_mainTextFakeBold) {
            isTitleFakeBold = ta.getBoolean(R.styleable.TitleView_kk_mainTextFakeBold, false);
        }
    }

    /**
     * 初始化组件
     *
     * @param context
     */
    private void initViews(Context context) {
        LayoutParams params = new LayoutParams(-2, -1);
        initLeftLayout(context, params);
        initRightLayout(context, params);
        initCenterLayout(context);

        LayoutParams lp1 = new LayoutParams(-2, -1);
        LayoutParams lp2 = new LayoutParams(-2, -1);
        LayoutParams lp3 = new LayoutParams(-2, -1);
        lp1.gravity = Gravity.LEFT;
        this.addView(this.mLeftLayout, lp1);
        lp2.gravity = Gravity.CENTER;
        this.addView(this.mCenterLayout, lp2);
        lp3.gravity = Gravity.RIGHT;
        this.addView(this.mRightLayout, lp3);

        if (mDividerHeight > 0) {
            LayoutParams dividerParams = new LayoutParams(-1, this.mDividerHeight);
            dividerParams.gravity = Gravity.BOTTOM;
            this.mDividerView = new View(context);
            this.addView(this.mDividerView, dividerParams);
            if (mDividerResource != -1) {
                setDivider(mDividerResource);
            }
        }
    }

    //初始化中间布局
    private void initCenterLayout(Context context) {
        this.mCenterLayout = new LinearLayout(context);
        this.mCenterLayout.setGravity(17);
//        this.mCenterLayout.setOrientation(VERTICAL);
        this.mTitleTv = new TextView(context);
        this.mTitleTv.setGravity(17);
        this.setTitle(this.mMainText);
        this.mTitleTv.getPaint().setFakeBoldText(this.isTitleFakeBold);
        this.mTitleTv.setTextSize((float) Util.px2sp(context, (float) this.mMainTextSize));
        this.mTitleTv.setTextColor(this.mMainTextColor);
        this.mTitleTv.setBackgroundColor(this.mMainTextBackgroundColor);
        if (this.mMainTextBackgroundResource != -1) {
            this.mTitleTv.setBackgroundResource(this.mMainTextBackgroundResource);
        }
        this.mTitleTv.setTag("skin:main_title:textColor");
        this.mCenterLayout.addView(this.mTitleTv);
    }

    //初始化右侧布局
    private void initRightLayout(Context context, LayoutParams params) {
        this.mRightLayout = new LinearLayout(context);
        this.mRightLayout.setGravity(16);
        this.mRightLayout.setPadding(0, 0, this.mHorizontalPadding, 0);

        this.mRightTv = new TextView(context);
        this.mRightTv.setGravity(17);
        this.setRightText(this.mRightText);
        this.mRightTv.setSingleLine();
        this.mRightTv.setTextSize((float) Util.px2sp(context, (float) this.mRightTextSize));
        this.mRightTv.setTextColor(this.mRightTextColor);
        this.mRightTv.setBackgroundColor(this.mRightTextBackgroundColor);
        if (this.mRightTextBackgroundResource != -1) {
            this.mRightTv.setBackgroundResource(this.mRightTextBackgroundResource);
        }

        if (this.mRightDrawable != -1) {
            this.setRightTextDrawable(this.mRightDrawable, this.mRightDrawablePadding);
        }

        this.mRightLayout.addView(this.mRightTv, params);
    }

    //初始化左侧布局
    private void initLeftLayout(Context context, LayoutParams params) {
        this.mLeftLayout = new LinearLayout(context);
        this.mLeftLayout.setGravity(16);
        this.mLeftLayout.setPadding(this.mHorizontalPadding, 0, 0, 0);

        this.mLeftTv = new TextView(context);
        this.mLeftTv.setGravity(17);
        this.mLeftTv.setPadding(0, 0, Util.dp2px(context, 16), 0);
        this.setLeftText(this.mLeftText);
        this.mLeftTv.setSingleLine();
        this.mLeftTv.setTextSize((float) Util.px2sp(context, (float) this.mLeftTextSize));
        this.mLeftTv.setTextColor(this.mLeftTextColor);
        this.mLeftTv.setBackgroundColor(this.mLeftTextBackgroundColor);
        if (this.mLeftTextBackgroundResource != -1) {
            this.mLeftTv.setBackgroundResource(this.mLeftTextBackgroundResource);
        }
        if (this.mLeftDrawable != -1) {
            this.setLeftTextDrawable(this.mLeftDrawable, this.mLeftDrawablePadding);
        }
        this.mLeftLayout.addView(this.mLeftTv, params);
    }

    public void setLeftText(CharSequence title) {
        this.mLeftTv.setText(title);
    }

    public void setLeftText(int id) {
        this.mLeftTv.setText(id);
    }

    public void setLeftTextSize(int unit, float size) {
        this.mLeftTv.setTextSize(unit, size);
    }

    public void setLeftTextSize(float size) {
        this.mLeftTv.setTextSize(size);
    }

    public void setLeftTextColor(int id) {
        this.mLeftTv.setTextColor(id);
    }

    public void setLeftTextColor(ColorStateList color) {
        try {
            this.mLeftTv.setTextColor(color);
        } catch (Exception var3) {
            ;
        }

    }

    public void setLeftTextBackgroundColor(int color) {
        this.mLeftTv.setBackgroundColor(color);
    }

    public void setLeftTextBackgroundResource(int id) {
        this.mLeftTv.setBackgroundResource(id);
    }

    public void setLeftTextDrawable(int id, int drawablePadding) {
        Util.setTextDrawable(mLeftTv, getResources(), id, mLeftDrawableWidth, mLeftDrawableHeight);
        this.setLeftTextDrawablePadding(drawablePadding);
    }


    public void setLeftTextDrawablePadding(int drawablePadding) {
        this.mLeftDrawablePadding = drawablePadding;
        this.mLeftTv.setCompoundDrawablePadding(Util.dp2px(mContext, (float) this.mLeftDrawablePadding));
    }

    public void setLeftTextDrawable(int id) {
        this.setLeftTextDrawable(id, this.mLeftDrawablePadding);
    }

    public void setLeftTextPadding(int left, int top, int right, int bottom) {
        this.mLeftTv.setPadding(left, top, right, bottom);
    }

    public void setOnLeftTextClickListener(OnClickListener l) {
        this.mLeftTextClickListener = l;
        this.mLeftTv.setOnClickListener(mLeftTextClickListener);
    }

    public void setLeftVisible(boolean visible) {
        this.mLeftTv.setVisibility(visible ? VISIBLE : GONE);
    }

    public OnClickListener getLeftTextClickListener() {
        return mLeftTextClickListener;
    }

    public OnClickListener getCenterTextClickListener() {
        return mCenterTextClickListener;
    }

    public OnClickListener getRightTextClickListener() {
        return mRightTextClickListener;
    }

    public void setOnCenterClickListener(OnClickListener l) {
        mCenterTextClickListener = l;
        this.mCenterLayout.setOnClickListener(mCenterTextClickListener);
    }

    public void setTitle(int id) {
        this.mTitleTv.setText(id);
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitleTv.setText(charSequence);
    }

    public void setMainTextSize(float mainTextSpValue) {
        this.mTitleTv.setTextSize(mainTextSpValue);
    }

    public void setMainTextSize(int unit, float mainTextSpValue) {
        this.mTitleTv.setTextSize(unit, mainTextSpValue);
    }

    public void setMainTextColor(int id) {
        this.mTitleTv.setTextColor(id);
    }

    public void setMainTextBackgroundColor(int color) {
        this.mTitleTv.setBackgroundColor(color);
    }

    public void setMainTextBackgroundResource(int id) {
        this.mTitleTv.setBackgroundResource(id);
    }

    public void setMainTextFakeBold(boolean isFakeBold) {
        this.isTitleFakeBold = isFakeBold;
    }

    public void setMainTextPadding(int left, int top, int right, int bottom) {
        this.mTitleTv.setPadding(left, top, right, bottom);
    }

    public void setRightText(CharSequence title) {
        this.mRightTv.setText(title);
    }

    public void setRightText(int id) {
        this.mRightTv.setText(id);
    }

    public void setRightTextSize(int unit, float size) {
        this.mRightTv.setTextSize(unit, size);
    }

    public void setRightTextSize(float size) {
        this.mRightTv.setTextSize(size);
    }

    public void setRightEnabled(boolean enabled) {
        this.mRightTv.setEnabled(enabled);
    }

    public void setRightTextColor(int id) {
        this.mRightTv.setTextColor(id);
    }

    public void setRightTextColor(ColorStateList color) {
        try {
            this.mRightTv.setTextColor(color);
        } catch (Exception var3) {
            ;
        }

    }

    public void setRightTextBackgroundColor(int color) {
        this.mRightTv.setBackgroundColor(color);
    }

    public void setRightTextBackgroundResource(int id) {
        this.mRightTv.setBackgroundResource(id);
    }

    public void setRightTextDrawable(int id, int drawablePadding) {
        Util.setTextDrawable(mRightTv, getResources(), id, mRightDrawableWidth, mRightDrawableHeight);
        this.setRightTextDrawablePadding(drawablePadding);
    }

    public void setRightTextDrawablePadding(int drawablePadding) {
        this.mRightDrawablePadding = drawablePadding;
        this.mRightTv.setCompoundDrawablePadding(Util.dp2px(this.getContext(), (float) this.mRightDrawablePadding));
    }

    public void setRightTextDrawable(int id) {
        this.setRightTextDrawable(id, this.mRightDrawablePadding);
    }

    public void setRightTextPadding(int left, int top, int right, int bottom) {
        this.mRightTv.setPadding(left, top, right, bottom);
    }

    public void setOnRightTextClickListener(OnClickListener l) {
        this.mRightTextClickListener = l;
        this.mRightTv.setOnClickListener(mRightTextClickListener);
    }

    public void setRightVisible(boolean visible) {
        this.mRightTv.setVisibility(visible ? VISIBLE : GONE);
    }


    public void setDivider(Drawable drawable) {
        this.mDividerView.setBackground(drawable);
    }

    public void setDivider(int resId) {
        this.mDividerView.setBackgroundResource(resId);
    }

    public void setDividerBackgroundColor(int color) {
        this.mDividerView.setBackgroundColor(color);
    }

    public void setDividerBackgroundResource(int resourceId) {
        this.mDividerView.setBackgroundResource(resourceId);
    }

    public void setDividerHeight(int dividerHeight) {
        this.mDividerHeight = dividerHeight;
        this.mDividerView.getLayoutParams().height = dividerHeight;
    }

    public void setHorizonalPadding(int paddingValue) {
        this.mHorizontalPadding = paddingValue;
        this.mLeftLayout.setPadding(this.mHorizontalPadding, 0, 0, 0);
        this.mRightLayout.setPadding(0, 0, this.mHorizontalPadding, 0);
    }

    public TextView getLeftTv() {
        return mLeftTv;
    }

    public TextView getTitleTv() {
        return mTitleTv;
    }

    public TextView getRightTv() {
        return mRightTv;
    }


    /**
     * 辅助类
     */
    private static class Util {

        private static int dp2px(Context context, float dipValue) {
            float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5F);
        }

        private static int px2sp(Context context, float pxValue) {
            float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (pxValue / fontScale + 0.5F);
        }

        //获取状态栏高度
        private static int getStatusBarHeight(Context context) {
            int result = 0;
            int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resId > 0) {
                result = context.getResources().getDimensionPixelOffset(resId);
            }
            return result;
        }

        /**
         * 设置TextView 的Drawable(图片draw到TextView的左边)
         *
         * @param textView
         * @param resources * @param resourcesId
         * @param width
         * @param height
         */
        private static void setTextDrawable(TextView textView, Resources resources, int resourcesId, int width, int height) {
            if (width != 0 || height != 0) {
                Bitmap bmp = BitmapFactory.decodeResource(resources, resourcesId);
                width = width == 0 ? bmp.getWidth() : width;
                height = height == 0 ? bmp.getHeight() : height;
                bmp = Bitmap.createScaledBitmap(bmp, width, height, false);
                Drawable drawable = new BitmapDrawable(resources, bmp);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                textView.setCompoundDrawables(drawable, null, null, null);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(resourcesId, 0, 0, 0);
            }

        }

    }
}
