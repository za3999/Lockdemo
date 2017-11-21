package cn.marno.kkqrcode;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

/**
 * Create by Marno on 2016/12/7 14:30
 * Function：扫描界面
 * Desc：
 */
public class KKScannerView extends ScannerView {
    private MultiFormatReader mMultiFormatReader;

    public KKScannerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KKScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMultiFormatReader();
    }

    private void initMultiFormatReader() {
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(QRDecoder.HINTS);
    }

    @Override
    public String processData(byte[] data, int width, int height, boolean isRetry) {
        String result = null;
        Result rawResult = null;

        try {
            PlanarYUVLuminanceSource source = null;
            Rect rect = mScanBoxView.getScanBoxAreaRect(height);
            if (rect != null && !isRetry && rect.left + rect.width() <= width && rect.top + rect.height() <= height) {
                source = new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
            } else {
                source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
            }
            rawResult = mMultiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
        } catch (Exception e1) {
        } finally {
            mMultiFormatReader.reset();
        }

        if (rawResult != null) {
            result = rawResult.getText();
        }
        return result;
    }
}