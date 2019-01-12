package com.andrewtse.testdemo.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.io.FileHelper;
import com.andrewtse.testdemo.io.StrUtil;
import com.andrewtse.testdemo.sound.pcm.OnRecording;
import com.andrewtse.testdemo.sound.pcm.PCMAudioPlayer;
import com.andrewtse.testdemo.sound.pcm.PCMRecordTask;
import com.andrewtse.testdemo.sound.pcm.PcmFileWaveView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PCMActivity extends AppCompatActivity {

    private ImageView mIdIvRecord;
    private TextView mIdTvState;
    private ImageView mIvStartPlay;
    private PcmFileWaveView mIdPcm;

    private AnimationDrawable animation;
    private boolean isOpen = false;
    private File mFile;
    private FileOutputStream mFos;
    private PCMRecordTask mPcmRecordTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcm);

        mIdIvRecord = findViewById(R.id.id_iv_record);
        mIdTvState = findViewById(R.id.id_tv_state);
        mIvStartPlay = findViewById(R.id.iv_start_play);
        mIdPcm = findViewById(R.id.id_pcm);

        mIdIvRecord.setBackgroundResource(R.drawable.play);
        animation = (AnimationDrawable) mIdIvRecord.getBackground();

        mPcmRecordTask = new PCMRecordTask();

        mIdPcm.showPcmFileWave(new File("/sdcard/pcm录音/keke.pcm"));
        mIdPcm.setProgress(0.5f);
        mPcmRecordTask.setOnRecording(new OnRecording() {
            @Override
            public void onRecording(byte[] data, int len) {
                try {
                    mFos.write(data, 0, len);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

        mIdIvRecord.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        animation.start();
                        startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                        animation.stop();
                        animation.selectDrawable(0);
                        stopRecord();
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
            PCMAudioPlayer.getInstance().startPlay("/sdcard/pcm录音/keke.pcm");
        });
    }

    private void startRecord() {
        try {
            mFile = FileHelper.get().createFile("pcm录音/" + StrUtil.getCurrentTime_yyyy_MM_dd_HH_mm_ss() + ".pcm");
            mFos = new FileOutputStream(mFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mPcmRecordTask.startRecord();
    }

    private void stopRecord() {
        mPcmRecordTask.stopRecord();
        mIdTvState.setText("录制" + mPcmRecordTask.getWorkingTime() + "秒");
    }

    public void setInfo(String str) {
        mIdTvState.setText(str);
    }

    public void btnPlay() {

        mIvStartPlay.setBackgroundResource(R.drawable.icon_stop_3);
    }

    public void btnStop() {
        mIvStartPlay.setBackgroundResource(R.drawable.icon_start_3);
    }
}
