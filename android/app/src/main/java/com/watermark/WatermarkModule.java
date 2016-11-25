package com.watermark;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 * Created by panchenhuan on 16/10/14.
 */
public class WatermarkModule extends ReactContextBaseJavaModule {
    private ReactApplicationContext mContext;

    public WatermarkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "Watermark";
    }


    /**
     * 图片水印
     * 原图、水印 uri
     */
    @ReactMethod
    public void creatWatermarkByImg(final String urlsrc, final String urlwm) {
        ImageTask imageTask = new ImageTask(urlsrc, urlwm, TYPE_IMG);
        imageTask.executeOnExecutor(getMyExecutor());
    }

    /**
     * 文字水印
     * 原图uri 文字
     */
    @ReactMethod
    public void creatWatermarkByText(final String urlsrc, final String text) {
        ImageTask imageTask = new ImageTask(urlsrc, text, TYPE_TEXT);
        imageTask.executeOnExecutor(getMyExecutor());
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private Executor myExecutor;

    /**
     * 创建固定长度的线程池
     */
    public Executor getMyExecutor() {
        if (myExecutor == null) {
            myExecutor = Executors.newFixedThreadPool(15);
        }
        return myExecutor;
    }

    private static final int TYPE_IMG  = 1;
    private static final int TYPE_TEXT = 2;

    class ImageTask extends AsyncTask<Void, Void, String> {
        private int    type;
        private String src;
        private String str2;

        public ImageTask(String urlsrc, String str2, int type) {
            src = urlsrc;
            this.str2 = str2;
            this.type = type;
        }

        @Override
        protected String doInBackground(Void... params) {
            Bitmap bitmapSrc = ImageUtil.getBitmap(src,mContext);
            Bitmap watermarkImg = null;
            if (bitmapSrc == null) {
                return null;
            }
            if (type == TYPE_IMG) {//图片水印
                Bitmap bitmapWm = ImageUtil.getBitmap(str2,mContext);
                if (bitmapWm == null) {
                    return null;
                }
                watermarkImg = ImageUtil.createWaterMaskLeftBottom(mContext, bitmapSrc, bitmapWm, 20, 20);
            } else if (type == TYPE_TEXT) {//文字水印
                watermarkImg = ImageUtil.drawTextToLeftBottom(mContext, bitmapSrc, str2, 15, Color.BLACK, 20, 20);
            }
            String base64 = ImageUtil.bitmapToBase64(watermarkImg);
            return base64;
        }

        @Override
        protected void onPostExecute(String s) {
            WritableMap params = Arguments.createMap();
            params.putString("base64", s);
            if (type == TYPE_IMG) {
                sendEvent(mContext, "imageBase64", params);// 向JS发送事件,并传递参数
            } else if (type == TYPE_TEXT) {
                sendEvent(mContext, "textBase64", params);// 向JS发送事件,并传递参数
            }
        }
    }

}
