package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.sound.media.MusicPlayer;
import com.andrewtse.testdemo.sound.media.NetMusicPlayer;
import com.andrewtse.testdemo.sound.widget.AlphaImageView;
import com.andrewtse.testdemo.sound.widget.ProgressView;

public class MediaPlayerActivity extends AppCompatActivity {

    @BindView(R.id.id_pv_pre)
    public ProgressView mProgressView;
    @BindView(R.id.id_iv_ctrl)
    public ImageView mIvCtrl;
    @BindView(R.id.id_iv_next)
    public AlphaImageView mIvNext;
    @BindView(R.id.id_iv_pre_list)
    public AlphaImageView mIvPreList;
    @BindView(R.id.id_tv_music_name)
    public TextView mTvMusicName;
    @BindView(R.id.id_tv_singer)
    public TextView mTvSignerName;

    private MusicPlayer mLocalMusicPlayer;
    private NetMusicPlayer mNetMusicPlayer;

    private final String mFilePath = "/sdcard/Music/";
    private final String mFileUri = "http://172.18.67.240:8080/test_music.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);

        //本地音乐播放--start
//        mLocalMusicPlayer = new MusicPlayer();
//
//        mLocalMusicPlayer.setOnSeekListener(per_100 -> mProgressView.setProgress(per_100));
//        mLocalMusicPlayer.setOnBufferListener(per_100 -> mProgressView.setProgress2(per_100));
//        mProgressView.setOnDragListener(per_100 -> mLocalMusicPlayer.seekTo(per_100));
//
//        mIvCtrl.setOnClickListener(v -> {
//            if (mLocalMusicPlayer.isPlaying()) {
//                mLocalMusicPlayer.pause();
//                mIvCtrl.setImageResource(R.drawable.icon_stop_2);//设置图标暂停
//            } else {
//                mLocalMusicPlayer.start(mFilePath + "向天再借五百年 - 韩磊.mp3");
//                mIvCtrl.setImageResource(R.drawable.icon_start_2);//设置图标播放
//            }
//        });
//        mTvMusicName.setText("向天再借五百年");
//        mTvSignerName.setText("韩磊");
        //end

        //网络音乐播放--start
        mNetMusicPlayer = new NetMusicPlayer(this);

        mNetMusicPlayer.setOnSeekListener(per_100 -> mProgressView.setProgress(per_100));
        mNetMusicPlayer.setOnBufferListener(per_100 -> mProgressView.setProgress2(per_100));
        mProgressView.setOnDragListener(per_100 -> mNetMusicPlayer.seekTo(per_100));

        mIvCtrl.setOnClickListener(v -> {
            if (mNetMusicPlayer.isPlaying()) {
                mNetMusicPlayer.pause();
                mIvCtrl.setImageResource(R.drawable.icon_stop_2);//设置图标暂停
            } else {
                mNetMusicPlayer.start(mFileUri);
                mIvCtrl.setImageResource(R.drawable.icon_start_2);//设置图标播放
            }
        });
        mTvMusicName.setText("勇气");
        mTvSignerName.setText("梁静茹");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocalMusicPlayer != null) {
            mLocalMusicPlayer.onDestroy();
        }
        if (mNetMusicPlayer != null) {
            mNetMusicPlayer.onDestroy();
        }
    }
}
