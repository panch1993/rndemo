package com.rndemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.react.ReactApplication;
import com.oblador.vectoricons.VectorIconsPackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.iflytek.cloud.SpeechUtility;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
public static final String HAS_FINGERPRINT_API = "hasFingerPrintApi";
  public static final String SETTINGS = "settings";
  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    protected boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
            new VectorIconsPackage(),new RNJavaReactPackage()
      );
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
      return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SpeechUtility.createUtility(this, "appid=57ecad94");
    SDKInitializer.initialize(MainApplication.this);

     SharedPreferences sp = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        if (sp.contains(HAS_FINGERPRINT_API)) { // 检查是否存在该值，不必每次都通过反射来检查
          return;
        }
        SharedPreferences.Editor editor = sp.edit();
        try {
          Class.forName("android.hardware.fingerprint.FingerprintManager"); // 通过反射判断是否存在该类
          editor.putBoolean(HAS_FINGERPRINT_API, true);
        } catch (ClassNotFoundException e) {
          editor.putBoolean(HAS_FINGERPRINT_API, false);
          e.printStackTrace();
        }
        editor.apply();
  }
}
