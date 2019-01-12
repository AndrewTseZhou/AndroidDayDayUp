package com.andrewtse.testdemo.sound.media;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author xk
 * @date 2019/1/8
 * 本地音乐播放
 */
public class MusicPlayer {

    private static final String TAG = "MusicPlayer";

    private MediaPlayer mPlayer;

    private Timer mTimer;
    private final Handler mHandler;

    private String mFilePath;

    private OnSeekListener mOnSeekListener;
    private OnBufferListener mOnBufferListener;

    public MusicPlayer() {
        mTimer = new Timer();
        mHandler = new Handler();
        init();
    }

    private void init() {
        mPlayer = new MediaPlayer();

        mPlayer.setOnErrorListener((mp, what, extra) -> false);

        //当装载流媒体完成时的回调
        mPlayer.setOnPreparedListener(mp -> {
            Log.d(TAG, "OnPreparedListener: ");
        });

        //播放完成监听
        mPlayer.setOnCompletionListener(mp -> {
            Log.d(TAG, "OnCompletion: ");
//            start(mFilePath);
        });

        //seekTo方法完成回调
        mPlayer.setOnSeekCompleteListener(mp -> {
            Log.d(TAG, "OnSeekComplete: ");
        });

        mPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            //流媒体的缓冲变化时回调
            if (mOnBufferListener != null) {
                mOnBufferListener.onSeek(percent * 100);
            }
            Log.d(TAG, "OnBufferingUpdate");
        });
    }

    public void start(String filePath) {
        //未初始化和正在播放时
        if (mPlayer != null && mPlayer.isPlaying()) {
            return;
        }

        try {
            if (filePath != null) {
                mFilePath = filePath;
                mPlayer.reset();
                mPlayer.setDataSource(filePath);
                mPlayer.prepare();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPlayer.start();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isPlaying()) {
                    int position = mPlayer.getCurrentPosition();
                    int duration = mPlayer.getDuration();
                    mHandler.post(() -> {
                        if (mOnSeekListener != null) {
                            mOnSeekListener.onSeek((int) (position * 1.f / duration * 100));
                        }
                    });
                }
            }
        }, 0, 1000);
    }

    public boolean isPlaying() {
        //未初始化和正在播放时return
        if (mPlayer == null) {
            return false;
        }
        return mPlayer.isPlaying();
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mTimer.cancel();
        mTimer = null;
    }

    /**
     * 停止
     */
    public void stop() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    /**
     * 重头开始(stop之后)
     */
    public void reStart() {
        mPlayer.seekTo(0);
        mPlayer.start();
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    /**
     * 跳转到
     */
    public void seekTo(int pre_100) {
        pause();
        mPlayer.seekTo((int) (pre_100 / 100.f * mPlayer.getDuration()));
        start(mFilePath);
    }

    //------------设置播放进度监听-----------
    public interface OnSeekListener {

        void onSeek(int per_100);
    }

    public void setOnSeekListener(OnSeekListener onSeekListener) {
        mOnSeekListener = onSeekListener;
    }

    //------------设置缓冲进度监听-----------
    public interface OnBufferListener {

        void onSeek(int per_100);
    }

    public void setOnBufferListener(OnBufferListener onBufferListener) {
        mOnBufferListener = onBufferListener;
    }
}
