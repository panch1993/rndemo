package com.ifly;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.ifly.util.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by panchenhuan on 16/10/9.
 */
public class IFlyListenModule extends ReactContextBaseJavaModule {

    private SpeechRecognizer  mIat;
    private SharedPreferences mSharedPreferences;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private int   ret;
    private ReactApplicationContext reactContext;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();


    public IFlyListenModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "IFlyListenModule";
    }

    /**
     * 停止语音识别
     */
    @ReactMethod
    public void stopListening() {
        if (mIat != null && mIat.isListening()) {
            mIat.stopListening();
        }

    }
    /**
     * 取消语音识别
     */

    @ReactMethod
    public void cancel(Callback callback) {
        if (mIat != null && mIat.isListening()) {
            mIat.cancel();
            callback.invoke("取消识别");
        }
    }
    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
    @ReactMethod
    public void startListening() {
        // 移动数据分析，收集开始听写事件
        FlowerCollector.onEvent(getCurrentActivity(), "iat_recognize");
        mSharedPreferences = getCurrentActivity().getSharedPreferences("ifly",
                Activity.MODE_PRIVATE);
        /**
         * 听写监听器。
         */
        RecognizerListener mRecognizerListener = new RecognizerListener() {

            @Override
            public void onBeginOfSpeech() {
                // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
                Toast.makeText(getCurrentActivity(), "请开始说话。。。", Toast.LENGTH_SHORT).show();
                sendEvent(reactContext,"onBeginOfSpeech",null);
            }

            @Override
            public void onError(SpeechError error) {
                // Tips：
                // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
                // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
                WritableMap parms = Arguments.createMap();
                parms.putInt("errorCode",error.getErrorCode());
                parms.putString("msg",error.getErrorDescription());
                sendEvent(reactContext,"onError",parms);
            }

            @Override
            public void onEndOfSpeech() {
                // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//                Toast.makeText(getCurrentActivity(), "倾听结束", Toast.LENGTH_SHORT).show();
                sendEvent(reactContext,"onEndOfSpeech",null);
            }

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                Log.d(TAG, results.getResultString());
                String s = printResult(results);
                WritableMap parms = Arguments.createMap();
                parms.putString("results",s);
                parms.putBoolean("isLast",isLast);
                sendEvent(reactContext,"onResult",parms);
                if (isLast) {
                    // TODO 最后的结果
                }
            }

            @Override
            public void onVolumeChanged(final int volume, byte[] data) {
                //            showTip("当前正在说话，音量大小：" + volume);
                WritableMap parms = Arguments.createMap();
                parms.putInt("volume",volume);
//                parms.put("isLast",isLast);
                sendEvent(reactContext,"onVolumeChanged",parms);
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
                // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
                // 若使用本地能力，会话id为null
                //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                //		Log.d(TAG, "session id =" + sid);
                //	}
            }
        };
        /**
         * 初始化监听器。
         */
        InitListener mInitListener = new InitListener() {
            @Override
            public void onInit(int code) {
                //                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    //                    showTip("初始化失败，错误码：" + code);
                    WritableMap parms = Arguments.createMap();
                    parms.putInt("errorCode",code);
                    parms.putString("msg","初始化失败");
                    sendEvent(reactContext,"onError",parms);
                }
            }
        };
        mIat = SpeechRecognizer.createRecognizer(getCurrentActivity(), mInitListener);

        // 清空显示内容

        // 设置参数
        setParam();
        // 不显示听写对话框
        ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            //            showTip("听写失败,错误码：" + ret);
            WritableMap parms = Arguments.createMap();
            parms.putInt("errorCode",ret);
            parms.putString("msg","听写失败");
            sendEvent(reactContext,"onError",parms);
        }
    }

    //
    //    private void showTip(final String str) {
    //        Log.d(TAG, str);
    ////        callback.invoke(str);
    //    }

    private String TAG = "panc";


    private String printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        //        showTip(resultBuffer.toString());
        return resultBuffer.toString();
        //        mResultText.setText(resultBuffer.toString());
        //        mResultText.setSelection(mResultText.length());
    }

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");


        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

}
