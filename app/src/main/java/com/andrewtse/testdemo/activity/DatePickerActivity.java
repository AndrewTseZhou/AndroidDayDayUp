package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.datepicker.DatePickerDialog;
import com.andrewtse.testdemo.datepicker.DatePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class DatePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        DatePickerView mPickerView = findViewById(R.id.mPickerView);
        List<String> list = new ArrayList<>();
        list.add("2014");
        list.add("2015");
        list.add("2016");
        list.add("2017");
        list.add("2018");
        list.add("2019");
        list.add("2020");
        list.add("2021");
        list.add("2022");
        list.add("2023");
        list.add("2024");
        mPickerView.setText("年");
        mPickerView.setData(list);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public void btnClickDateBottom(View view) {
        DatePickerDialog dialog = new DatePickerDialog.Builder(this)
                .setCanceledTouchOutside(true)
                .setGravity(Gravity.BOTTOM)
                .setSupportTime(false)
                .setTwelveHour(true)
                .setCanceledTouchOutside(false)
                .setOnDateResultListener(new DatePickerDialog.OnDateResultListener() {
                    @Override
                    public void onDateResult(long date) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);
                        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                        dateFormat.applyPattern("yyyy-MM-dd HH:mm");
                        Toast.makeText(DatePickerActivity.this, dateFormat.format(new Date(date)), Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        dialog.show();
    }

    public void btnClickDateCenter(View view) {
        DatePickerDialog dialog = new DatePickerDialog.Builder(this)
                .setCanceledTouchOutside(true)
                .setSupportTime(true)
                .setTwelveHour(true)
                .setCanceledTouchOutside(false)
                .setOnDateResultListener(new DatePickerDialog.OnDateResultListener() {
                    @Override
                    public void onDateResult(long date) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);
                        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                        dateFormat.applyPattern("yyyy-MM-dd HH:mm");
                        Toast.makeText(DatePickerActivity.this, dateFormat.format(new Date(date)), Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        dialog.show();
    }
}
