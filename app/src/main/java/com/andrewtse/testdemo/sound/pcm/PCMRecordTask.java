package com.andrewtse.testdemo.sound.pcm;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

/**
 * @author xk
 * @date 2019/1/6
 * 声音、录音与播放(PCM)
 */
public class PCMRecordTask {

    //默认配置AudioRecord
    private static final int DEFAULT_SOURCE = AudioSource.MIC;//麦克风采集
    private static final int DEFAULT_SAMPLE_RATE = 44100;//采样频率
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;//单声道
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;//输出格式：16位PCM

    private AudioRecord mAudioRecord;//录音机
    private int mMinBufferSize = 2048;//最小缓存数组大小

    private Thread mRecordThread;//录音线程
    private boolean mIsStarted = false;//是否已开启
    private volatile boolean mIsRecording = false;//是否正在录制

    private OnRecording mOnRecording;//录制时的监听
    private long mStartTime;//开始录制时间
    private int mWorkingTime;

    /**
     * 开始录制
     */
    public boolean startRecord() {
        return startRecord(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
    }

    public boolean startRecord(int source, int sampleRate, int channel, int format) {
        if (mIsStarted) {
            return false;
        }

        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, format);
        mAudioRecord = new AudioRecord(source, sampleRate, channel, format, mMinBufferSize);
        mAudioRecord.startRecording();

        mIsRecording = true;
        mRecordThread = new Thread(new RecordRunnable());
        mRecordThread.start();
        mIsStarted = true;
        mStartTime = System.currentTimeMillis();
        return true;
    }

    public void stopRecord() {
        if (!mIsStarted) {
            return;
        }

        mIsRecording = false;
        try {
            mRecordThread.interrupt();
            mRecordThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mAudioRecord.getRecordingState()==AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }

        mAudioRecord.release();
        mAudioRecord = null;
        mIsStarted = false;
        mWorkingTime = (int) ((System.currentTimeMillis() - mStartTime) / 100);
    }

    public int getWorkingTime() {
        return mWorkingTime;
    }

    public void setOnRecording(OnRecording onRecording) {
        mOnRecording = onRecording;
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    private class RecordRunnable implements Runnable {

        @Override
        public void run() {
            while (mIsRecording) {
                byte[] buff = new byte[mMinBufferSize];
                int read = mAudioRecord.read(buff, 0, mMinBufferSize);
                if (mOnRecording != null) {
                    if (read > 0) {
                        mOnRecording.onRecording(buff, read);
                    } else {
                        mOnRecording.onError(new RuntimeException("Error when startRecord"));
                    }
                }
            }
        }
    }
}
