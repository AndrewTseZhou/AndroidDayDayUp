package com.andrewtse.testdemo.sound.media;

import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author xk
 * @date 2019/1/11
 */
public class MediaRecorderTask {

    private MediaRecorder mRecorder;
    private long mStartTime;
    private int mAllTime;
    private boolean isRecording;
    private File mFile;

    private Timer mTimer;
    private final Handler mHandler;

    private OnVolumeChangeListener mOnVolumeChangeListener;

    public MediaRecorderTask() {
        mTimer = new Timer();
        mHandler = new Handler();
    }

    public void start(File file) {
        doRecorder(file);
    }

    private void doRecorder(File file) {
        mAllTime = 0;
        mFile = file;
        if (mRecorder == null) {
            //1.获取MediaRecorder实例
            mRecorder = new MediaRecorder();
        }
        //配置MediaRecorder
        //2.设置音频来源
        mRecorder.setAudioSource(AudioSource.MIC);
        //3.设置音频输出格式
        mRecorder.setOutputFormat(OutputFormat.MPEG_4);
        //4.采样频率
        mRecorder.setAudioSamplingRate(44100);
        //5.设置音频的编码方式
        mRecorder.setAudioEncoder(AudioEncoder.AAC);
        //6.设置音质编码频率96Kbps
        mRecorder.setAudioEncodingBitRate(96000);
        //7.设置录音文件位置
        mRecorder.setOutputFile(file.getAbsolutePath());

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mStartTime = System.currentTimeMillis();
        if (mRecorder != null) {
            mRecorder.start();
            isRecording = true;
            cbkVolume();
        }
    }

    /**
     * 每隔1秒回调一次音量
     */
    private void cbkVolume() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isRecording) {
                    float per;
                    try {
                        //获取音量大小
                        per = mRecorder.getMaxAmplitude() / 32767f;//最大32767
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        per = (float) Math.random();
                    }
                    if (mOnVolumeChangeListener != null) {
                        float finalPer = per;
                        mHandler.post(() -> {
                            mOnVolumeChangeListener.volumeChange(finalPer);
                        });
                    }
                }
            }
        }, 0, 1000);
    }

    @RequiresApi(api = VERSION_CODES.N)
    public void pause() {
        mAllTime += System.currentTimeMillis() - mStartTime;
        mRecorder.pause();
        isRecording = false;
        mStartTime = System.currentTimeMillis();
    }

    @RequiresApi(api = VERSION_CODES.N)
    public void resume() {
        mRecorder.resume();
        isRecording = true;
    }

    public void stop() {
        try {
            mAllTime += System.currentTimeMillis() - mStartTime;
            mRecorder.stop(); // [7]停止录制
            isRecording = false;
            mRecorder.release();
            mRecorder = null;
        } catch (RuntimeException e) {
            mRecorder.reset();//[8] You can reuse the object by going back
            mRecorder.release(); //[9] Now the object cannot be reused
            mRecorder = null;
            isRecording = false;
            if (mFile.exists())
                mFile.delete();
        }
    }

    public int getAllTime() {
        return mAllTime / 1000;
    }

    //---------设置音量改变监听-------------
    public interface OnVolumeChangeListener {

        void volumeChange(float per);
    }

    public void setOnVolumeChangeListener(OnVolumeChangeListener onVolumeChangeListener) {
        mOnVolumeChangeListener = onVolumeChangeListener;
    }
}
