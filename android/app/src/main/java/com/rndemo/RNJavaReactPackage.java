package com.rndemo;

import com.baidumaprn.BaiduMapManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.ifly.IFlyListenModule;
import com.record.RecordModule;
import com.touchcheck.TouchIdModule;
import com.watermark.WatermarkModule;
import com.widget.SnackbarModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by panchenhuan on 16/10/11.
 */
public class RNJavaReactPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules=new ArrayList<>();
        modules.add(new IFlyListenModule(reactContext));
        modules.add(new RecordModule(reactContext));
        modules.add(new TouchIdModule(reactContext));
        modules.add(new WatermarkModule(reactContext));
        modules.add(new SnackbarModule(reactContext));
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return  Arrays.<ViewManager>asList(
                new BaiduMapManager()
        );
    }
}
