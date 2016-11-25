package com.record;

import android.media.MediaRecorder;
import android.os.Environment;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class RecordModule extends ReactContextBaseJavaModule {

    private MediaRecorder mMediaRecorder = null;
    private ReactContext    reactContext;
    private String          filePath;
    private ArrayList<File> mRecList;
    private boolean mInit = true;

    public RecordModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        filePath = Environment.getExternalStorageDirectory() + "/com.voicemanager/audio";
        mRecList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public String getName() {
        return "RecordModule";
    }

    //TODO 修改文件默认路径
    @ReactMethod
    public void startRecording() {
            if (mInit) {
                cleanFieArrayList(mRecList);
            }
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


            // 创建音频文件,合并的文件放这里
            File recFile = new File(filePath, new Date().getTime() + ".amr");
            mRecList.add(recFile);

            mMediaRecorder.setOutputFile(recFile.getAbsolutePath());
            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaRecorder.start();
        } else {
            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaRecorder.start();
        }
    }

    @ReactMethod
    public void stopRecording(String filename) {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;

            File file = getOutputVoiceFile(mRecList, filename);
            if (file != null && file.length() > 0) {
                cleanFieArrayList(mRecList);
            }
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("stopRecording", file.getAbsolutePath());
        } else if (!mInit) {
            File file = getOutputVoiceFile(mRecList, filename);
            if (file != null && file.length() > 0) {
                cleanFieArrayList(mRecList);
            }
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("stopRecording", file.getAbsolutePath());
        }
        mInit = true;
    }

    @ReactMethod
    public void pauseRecording() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;

            mInit = false;
        }
    }

    private void cleanFieArrayList(ArrayList<File> list) {
        for (File file : list) {
            file.delete();
        }
        list.clear();
    }

    /**
     * 合并录音
     *
     * @param list
     * @param filename
     * @return
     */
    private File getOutputVoiceFile(ArrayList<File> list, String filename) {
        // 创建音频文件,合并的文件放这里
        File resFile = new File(filePath, filename + ".amr");
        FileOutputStream fileOutputStream = null;

        if (!resFile.exists()) {
            try {
                resFile.createNewFile();
            } catch (IOException e) {
            }
        }
        try {
            fileOutputStream = new FileOutputStream(resFile);
        } catch (IOException e) {
        }
        // list里面为暂停录音 所产生的 几段录音文件的名字，中间几段文件的减去前面的6个字节头文件
        for (int i = 0; i < list.size(); i++) {
            File file = list.get(i);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] myByte = new byte[fileInputStream.available()];
                // 文件长度
                int length = myByte.length;
                // 头文件
                if (i == 0) {
                    while (fileInputStream.read(myByte) != -1) {
                        assert fileOutputStream != null;
                        fileOutputStream.write(myByte, 0, length);
                    }
                } else {
                    // 之后的文件，去掉头文件就可以了
                    while (fileInputStream.read(myByte) != -1) {
                        assert fileOutputStream != null;
                        fileOutputStream.write(myByte, 6, length - 6);
                    }
                }
                assert fileOutputStream != null;
                fileOutputStream.flush();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 结束后关闭流
        try {
            assert fileOutputStream != null;
            fileOutputStream.close();
        } catch (IOException e) {
        }

        return resFile;
    }
}