package com.andrewtse.testdemo.sound.pcm;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Process;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xk
 * @date 2019/1/6
 * PCM播放
 */
public class PCMAudioPlayer {

    //默认配置是AudioTrack
    private static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;//音乐
    private static final int DEFAULT_SAMPLE_RATE = 44100;//采样频率
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO;//注意是out
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;

    private final ExecutorService mExecutorService;

    private AudioTrack mAudioTrack;//音轨
    private DataInputStream dis;//流
    private boolean isStart = false;
    private static PCMAudioPlayer mInstance;//单例
    private int mMinBufferSize;//最小缓存大小

    private PCMAudioPlayer() {
        mMinBufferSize = AudioTrack.getMinBufferSize(DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
        mAudioTrack = new AudioTrack(DEFAULT_STREAM_TYPE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT, mMinBufferSize * 2, DEFAULT_PLAY_MODE);
        mExecutorService = Executors.newSingleThreadExecutor();//线程池
    }

    public static PCMAudioPlayer getInstance() {
        if (mInstance == null) {
            synchronized (PCMAudioPlayer.class) {
                if (mInstance == null) {
                    mInstance = new PCMAudioPlayer();
                }
            }
        }
        return mInstance;
    }

    /**
     * 播放文件
     */
    private void setPath(String path) throws Exception {
        File file = new File(path);
        dis = new DataInputStream(new FileInputStream(file));
    }

    /**
     * 启动播放
     */
    public void startPlay(String path) {
        try {
            isStart = true;
            setPath(path);
            mExecutorService.execute(new PlayRunnable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        try {

            if (mAudioTrack != null) {
                if (mAudioTrack.getState() == AudioRecord.STATE_INITIALIZED) {
                    mAudioTrack.stop();
                }
            }
            if (dis != null) {
                isStart = false;
                dis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.release();
            mAudioTrack = null;
        }
        mExecutorService.shutdownNow();//停止线程池
    }

    private class PlayRunnable implements Runnable {

        @Override
        public void run() {
            try {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                byte[] buff = new byte[mMinBufferSize];
                int readCount = 0;
                while (dis.available() > 0) {
                    readCount = dis.read(buff);
                    if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (readCount != 0 && readCount != -1) {
                        mAudioTrack.play();
                        mAudioTrack.write(buff, 0, readCount);
                    }
                }
                stopPlay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
