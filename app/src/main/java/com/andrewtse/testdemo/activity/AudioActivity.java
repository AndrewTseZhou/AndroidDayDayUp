package com.andrewtse.testdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import com.andrewtse.testdemo.R;

public class AudioActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        findViewById(R.id.btn_pcm).setOnClickListener(this);
        findViewById(R.id.btn_media_player).setOnClickListener(this);
        findViewById(R.id.btn_media_recorder).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_pcm:
                intent = new Intent(this, PCMActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_media_player:
                intent = new Intent(this, MediaPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_media_recorder:
                intent = new Intent(this, MediaRecorderActivity.class);
                startActivity(intent);
                break;
        }
    }
}
