/*
 * Copyright 2018 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2018-04-16 13:23:16
 *
 * GitHub: https://github.com/GcsSloop
 * WeiBo: http://weibo.com/GcsSloop
 * WebSite: http://www.gcssloop.com
 */

package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.rc_view.view.RcRelativeLayout;
import com.andrewtse.testdemo.rc_view.widget.RcHelper;

public class RcActivity extends AppCompatActivity {

    private RcRelativeLayout layout;
    private CheckBox mCbClipBackground;
    private CheckBox mCbCircle;
    private SeekBar mSeekBarStrokeWidth;
    private SeekBar mSeekBarRadiusTopLeft;
    private SeekBar mSeekBarRadiusTopRight;
    private SeekBar mSeekBarRadiusBottomLeft;
    private SeekBar mSeekBarRadiusBottomRight;
    private SeekBar mSeekBarRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rc);
        layout = findViewById(R.id.rc_layout);
        mCbCircle = findViewById(R.id.cb_circle);
        mCbClipBackground = findViewById(R.id.cb_clip_background);
        mSeekBarStrokeWidth = findViewById(R.id.seekbar_stroke_width);
        mSeekBarRadiusTopLeft = findViewById(R.id.seekbar_radius_top_left);
        mSeekBarRadiusTopRight = findViewById(R.id.seekbar_radius_top_right);
        mSeekBarRadiusBottomLeft = findViewById(R.id.seekbar_radius_bottom_left);
        mSeekBarRadiusBottomRight = findViewById(R.id.seekbar_radius_bottom_right);
        mSeekBarRadius = findViewById(R.id.seekbar_radius);

        //checked状态
        final Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        layout.setOnCheckedChangeListener(new RcHelper.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View v, boolean isChecked) {
                toast.setText("checked = " + isChecked);
                toast.show();
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.toggle();
            }
        });
        //剪裁背景
        mCbClipBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layout.setClipBackground(isChecked);
            }
        });
        //圆形
        mCbCircle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layout.setRoundAsCircle(isChecked);
            }
        });
        //边框粗细
        mSeekBarStrokeWidth.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                layout.setStrokeWidth(progress);
            }
        });
        //左上角半径
        mSeekBarRadiusTopLeft.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                layout.setTopLeftRadius(getProgressRadius(progress));
            }
        });
        //右上角半径
        mSeekBarRadiusTopRight.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                layout.setTopRightRadius(getProgressRadius(progress));
            }
        });
        //左下角半径
        mSeekBarRadiusBottomLeft.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                layout.setBottomLeftRadius(getProgressRadius(progress));
            }
        });
        //右下角半径
        mSeekBarRadiusBottomRight.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                layout.setBottomRightRadius(getProgressRadius(progress));
            }
        });
        //所有半径
        mSeekBarRadius.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                layout.setRadius(getProgressRadius(progress));
            }
        });

        mSeekBarStrokeWidth.setProgress(getResources().getDimensionPixelSize(R.dimen.default_stroke_width));
        mCbClipBackground.setChecked(true);

    }

    private int getProgressRadius(int progress) {
        int size = getResources().getDimensionPixelOffset(R.dimen.size_example_image);
        return (int) ((float) progress / 100 * size) / 2;
    }


    public static abstract class SimpleSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
