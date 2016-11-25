package com.touchcheck;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.rndemo.R;

/** keyikeyile
 * Created by panchenhuan on 16/10/11.
 * <uses-permission android:name="android.permission.USE_FINGERPRINT"/>
 * API>23
 * {ReactPackage} modules.add(new TouchIdModule(reactContext));
 */
public class TouchIdModule extends ReactContextBaseJavaModule {
    public static final String HAS_FINGERPRINT_API = "hasFingerPrintApi";
    public static final String SETTINGS            = "settings";
    private FingerprintManager manager;
    private CancellationSignal mCancel;
    private AlertDialog        mAlertDialog;

    public TouchIdModule(ReactApplicationContext reactContext) {
        super(reactContext);
        SharedPreferences sp = reactContext.getSharedPreferences(SETTINGS, reactContext.MODE_PRIVATE);
        if (sp.getBoolean(HAS_FINGERPRINT_API, true)) {

            if (ActivityCompat.checkSelfPermission(reactContext, Manifest.permission.USE_FINGERPRINT) ==
                    PackageManager.PERMISSION_GRANTED) {
                manager = (FingerprintManager) reactContext.getSystemService(reactContext.FINGERPRINT_SERVICE);
            } else {
                System.err.println("没有指纹识别权限");
            }

        } else {
            System.err.println("不包含指纹势别API");
        }
    }

    @Override
    public String getName() {
        return "TouchIdModule";
    }

    /**
     * 是否可用指纹识别
     *
     * @return
     */
    @ReactMethod
    public boolean isHardwareDetected() {
        return manager.isHardwareDetected();
    }

    /**
     * 是否有已登记指纹
     *
     * @return
     */
    @ReactMethod
    public boolean hasEnrolledFingerprints() {
        return manager.hasEnrolledFingerprints();
    }

    /**
     * 开启指纹识别
     */
    @ReactMethod
    public void touchIDStart() {
        if (!hasEnrolledFingerprints()) {
            Toast.makeText(getCurrentActivity(), "请先录入指纹", Toast.LENGTH_SHORT).show();
            return;
        }
        mCancel = new CancellationSignal();
        showDialog("请输入指纹");
        manager.authenticate(null, mCancel, 0, new MyCallBack(), null);
    }

    public class MyCallBack extends FingerprintManager.AuthenticationCallback {


        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            WritableMap parms = Arguments.createMap();
            parms.putInt("errMsgId", errMsgId);
            parms.putString("errString", errString.toString());
            sendEvent(getReactApplicationContext(), "touchIDOnError", parms);
            if (errMsgId != 5) {
                showDialog(errString.toString());
            }
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            sendEvent(getReactApplicationContext(), "touchIDOnError", null);
            showDialog("指纹验证失败,请重试");
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            System.out.println(helpMsgId + "--" + helpString);
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult
                                                      result) {
            sendEvent(getReactApplicationContext(), "touchIDOnSuccess", null);
            if (mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
        }
    }

    private void showDialog(String msg) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        mAlertDialog = new AlertDialog.Builder(getCurrentActivity()).create();
        mAlertDialog.setTitle("指纹识别");
        mAlertDialog.setIcon(R.mipmap.ic_fp_40px);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlertDialog.dismiss();
                mCancel.cancel();
            }
        });

        mAlertDialog.setMessage(msg);
        mAlertDialog.show();
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
