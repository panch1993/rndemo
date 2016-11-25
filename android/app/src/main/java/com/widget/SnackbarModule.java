package com.widget;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Created by panchenhuan on 16/11/2.
 */

public class SnackbarModule extends ReactContextBaseJavaModule {
    public SnackbarModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "SnackbarModule";
    }

    @ReactMethod
    public void show(String msg, String action, final Callback onclick) {
        Snackbar.make(getCurrentActivity().getWindow().getDecorView(),msg, Snackbar.LENGTH_SHORT)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onclick.invoke();
                    }
                })
                .show();
    }
}
