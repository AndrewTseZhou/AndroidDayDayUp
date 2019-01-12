package com.andrewtse.testdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.andrewtse.testdemo.R;

public class JniActivity extends AppCompatActivity {

    private EditText mEtFirst;
    private EditText mEtSecond;
    private EditText mEtOp;
    private TextView mTvResult;
    private Button mBtnCal;

    static {
        System.loadLibrary("native-cal");
    }

    //注意：如果cpp代码中含有java中没有的方法，会编译错误
    private static native int nativeCal(int a, int b, char op);

    private static native String nativeToast();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni);

        mEtFirst = findViewById(R.id.et_first);
        mEtSecond = findViewById(R.id.et_second);
        mEtOp = findViewById(R.id.et_operator);
        mTvResult = findViewById(R.id.tv_result);
        mBtnCal = findViewById(R.id.btn_cal);

        mBtnCal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtFirst.getText().length() > 0 && mEtSecond.getText().length() > 0 && mEtOp.getText().length() > 0) {
                    int num1 = Integer.valueOf(mEtFirst.getText().toString());
                    int num2 = Integer.valueOf(mEtSecond.getText().toString());
                    char op = mEtOp.getText().toString().charAt(0);
                    mTvResult.setText("结果：" + nativeCal(num1, num2, op) + ", " + nativeToast());
                }
            }
        });
    }
}
