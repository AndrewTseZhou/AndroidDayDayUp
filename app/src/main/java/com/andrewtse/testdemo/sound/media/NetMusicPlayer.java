package com.andrewtse.testdemo.sound.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author xk
 * @date 2019/1/11
 */
public class NetMusicPlayer {

    private static final String TAG = "NetMusicPlayer";

    private MediaPlayer mPlayer;
    private Timer mTimer;
    private Context mContext;

    private boolean isInitialized = false;//是否初始化
    private final Handler mHandler;

    private OnSeekListener mOnSeekListener;
    private OnBufferListener mOnBufferListener;

    private String mUriStr;

    public NetMusicPlayer(Context context) {
        mContext = context;
        mTimer = new Timer();
        mHandler = new Handler();
        init();
    }

    private void init() {
        mPlayer = new MediaPlayer();

        mPlayer.setOnErrorListener((mp, what, extra) -> false);

        //当装载流媒体完毕的时候回调
        mPlayer.setOnPreparedListener(mp -> isInitialized = true);

        //播放完成监听
        mPlayer.setOnCompletionListener(mp -> start(mUriStr));

        //seekTo方法完成回调
        mPlayer.setOnSeekCompleteListener(mp -> {
            Log.d(TAG, "OnSeekComplete: ");
        });

        //网络流媒体的缓冲变化时回调
        mPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            if (mOnBufferListener != null) {
                mOnBufferListener.onSeek(percent);
            }
        });
    }

    /**
     * 播放
     */
    public void start(String uriStr) {
        //未初始化和正在播放时return
        if (!isInitialized && mPlayer.isPlaying()) {
            return;
        }

        Uri uri = Uri.parse(uriStr);
        try {
            if (uriStr != null && uri != null) {
                mUriStr = uriStr;
                mPlayer.reset();
                mPlayer.setDataSource(mContext, uri);
                mPlayer.prepareAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPlayer.start();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isPlaying()) {
                    int pos = mPlayer.getCurrentPosition();
                    int duration = mPlayer.getDuration();
                    mHandler.post(() -> {
                        if (mOnSeekListener != null) {
                            mOnSeekListener.onSeek((int) (pos * 1.f / duration * 100));
                        }
                    });
                }
            }
        }, 0, 1000);
    }

    public boolean isPlaying() {
        //未初始化和正在播放时return
        if (!isInitialized) {
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
            mPlayer.release();//释放资源
            mPlayer = null;
        }
        isInitialized = false;

        mTimer.cancel();
        mTimer = null;
    }

    /**
     * 停止
     */
    private void stop() {
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
        start(mUriStr);
    }

    //------------设置进度监听-----------
    public interface OnSeekListener {

        void onSeek(int per_100);
    }

    public void setOnSeekListener(OnSeekListener onSeekListener) {
        mOnSeekListener = onSeekListener;
    }


    //------------设置缓存进度监听-----------
    public interface OnBufferListener {

        void onSeek(int per_100);
    }

    public void setOnBufferListener(OnBufferListener onBufferListener) {
        mOnBufferListener = onBufferListener;
    }
}
