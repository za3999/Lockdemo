package com.test.yibu.lockdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.test.yibu.lockdemo.R;

/**
 * Created by zhengcf on 2017/7/5.
 */

public class ShadowRoundImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint mBitmapPaint = new Paint();
    private int mRadius;
    private Matrix mMatrix = new Matrix();
    private BitmapShader mBitmapShader;
    private int mWidth;
    float firstShadowWidth, secondShadowWidth;
    int firstShadowColor, secondShadowColor;
    private Paint mShadowPaint = new Paint();
    private Paint mSecondShadowPaint = new Paint();


    public ShadowRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBitmapPaint.setAntiAlias(true);
        parseTypeArray(context, attrs);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.STROKE);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(firstShadowColor);
        mShadowPaint.setStrokeWidth(firstShadowWidth);

        mSecondShadowPaint.setAntiAlias(true);
        mSecondShadowPaint.setStyle(Paint.Style.STROKE);
        mSecondShadowPaint.setAntiAlias(true);
        mSecondShadowPaint.setColor(secondShadowColor);
        mSecondShadowPaint.setStrokeWidth(secondShadowWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.getDrawable() != null) {
            this.setUpShader();
            canvas.drawCircle((float) mRadius, (float) mRadius, (float) mRadius - firstShadowWidth - secondShadowWidth, mBitmapPaint);
            if (firstShadowWidth != 0f) {
                canvas.drawCircle((float) mRadius, (float) mRadius, (float) mRadius - firstShadowWidth -
                        secondShadowWidth, mShadowPaint);
            }
            if (secondShadowWidth != 0f) {
                canvas.drawCircle((float) mRadius, (float) mRadius, (float) mRadius - firstShadowWidth, mSecondShadowPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWidth = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        this.mRadius = this.mWidth / 2;
        this.setMeasuredDimension(this.mWidth, this.mWidth);
    }

    private void setUpShader() {
        Drawable drawable = this.getDrawable();
        if (drawable != null) {
            Bitmap bmp = this.drawableToBitmap(drawable);
            this.mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            float scale = Math.max((float) this.getWidth() * 1.0F / (float) bmp.getWidth(), (float) this.getHeight() * 1.0F / (float) bmp.getHeight());
            this.mMatrix.setScale(scale, scale);
            this.mBitmapShader.setLocalMatrix(this.mMatrix);
            this.mBitmapPaint.setShader(this.mBitmapShader);
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable w1 = (BitmapDrawable) drawable;
            return w1.getBitmap();
        } else {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
            return bitmap;
        }
    }


    private void parseTypeArray(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowRoundImageView);
        firstShadowWidth = a.getDimension(R.styleable.ShadowRoundImageView_firstShadowWidth, 0f);
        secondShadowWidth = a.getDimension(R.styleable.ShadowRoundImageView_secondShadowWidth, 0f);
        firstShadowColor = a.getColor(R.styleable.ShadowRoundImageView_firstShadowColor, 0XFFFFFFFF);
        secondShadowColor = a.getColor(R.styleable.ShadowRoundImageView_secondShadowColor, 0XFFFFFFFF);
        a.recycle();
    }

}
