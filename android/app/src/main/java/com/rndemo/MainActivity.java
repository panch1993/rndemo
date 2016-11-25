package com.rndemo;

import com.facebook.react.ReactActivity;
import com.iflytek.sunflower.FlowerCollector;

public class MainActivity extends ReactActivity {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "rnDemo";
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlowerCollector.onResume(this); }
    @Override
    protected void onPause() {
        super.onPause();
        FlowerCollector.onPause(this); }
}
