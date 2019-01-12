package com.andrewtse.testdemo.activity;

import android.graphics.drawable.AnimationDrawable;
import android.media.AudioFormat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.io.FileHelper;
import com.andrewtse.testdemo.io.StrUtil;
import com.andrewtse.testdemo.sound.media.MediaRecorderTask;
import com.andrewtse.testdemo.sound.media.MusicPlayer;
import com.andrewtse.testdemo.sound.pcm.PcmToWavUtil;
import com.andrewtse.testdemo.sound.widget.RhythmView;
import java.io.File;

public class MediaRecorderActivity extends AppCompatActivity {

    @BindView(R.id.id_iv_media_record)
    ImageView mIvRecord;
    @BindView(R.id.id_tv_state)
    TextView mTvState;
    @BindView(R.id.iv_start_play)
    ImageView mIvStartPlay;
    @BindView(R.id.id_rth)
    RhythmView mRhythmView;

    private AnimationDrawable animation;

    private boolean isOpen = false;
    private File mFile;
    private MediaRecorderTask mMediaRecorderTask;
    private MusicPlayer mMusicPlayer;

    private static final int DEFAULT_SAMPLE_RATE = 44100;//采样频率
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;//单声道
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;//输出格式：16位pcm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);
        ButterKnife.bind(this);

        mIvRecord.setBackgroundResource(R.drawable.play);
        animation = (AnimationDrawable) mIvRecord.getBackground();

        mMusicPlayer = new MusicPlayer();

        String inPath = "/sdcard/pcm录音/keke.pcm";
        String outPath = "/sdcard/pcm录音/keke.wav";

        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
        pcmToWavUtil.pcmToWav(inPath, outPath);

        mMediaRecorderTask = new MediaRecorderTask();
        mMediaRecorderTask.setOnVolumeChangeListener(per -> mRhythmView.setPerHeight(per));

        mIvRecord.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        animation.start();
                        startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                        animation.stop();
                        animation.selectDrawable(0);
                        stopRecode();
                        break;
                }
                return true;
            }
        });

        mIvStartPlay.setOnClickListener(e -> {
            if (!isOpen) {
                btnPlay();
            } else {
                btnStop();
            }
            isOpen = !isOpen;
        });
    }

    /**
     * 开启录音
     */
    private void startRecord() {
        mFile = FileHelper.get().createFile("MediaRecorder录音/" + StrUtil.getCurrentTime_yyyyMMddHHmmss() + ".mp3");
        mMediaRecorderTask.start(mFile);
    }

    /**
     * 停止录制
     */
    private void stopRecode() {
        mMediaRecorderTask.stop();
        mTvState.setText("录制" + mMediaRecorderTask.getAllTime() + "秒");
    }

    public void btnPlay() {
        mMusicPlayer.start("/sdcard/MediaRecorder录音/20190111175212.mp3");
        mIvStartPlay.setBackgroundResource(R.drawable.icon_stop_3);
    }

    public void btnStop() {
        mIvStartPlay.setBackgroundResource(R.drawable.icon_start_3);
        mMusicPlayer.pause();
    }
}
