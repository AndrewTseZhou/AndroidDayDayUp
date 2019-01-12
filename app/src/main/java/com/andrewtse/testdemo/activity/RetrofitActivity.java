package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.retrofit.GetRequestInterface;
import com.andrewtse.testdemo.retrofit.Translation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitActivity extends AppCompatActivity {

    private TextView mTvTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        request();
        mTvTranslation = findViewById(R.id.tv_translation);
    }

    public void request() {

        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://fanyi.youdao.com/") // 设置 网络请求 Url
            .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
            .build();

        // 步骤5:创建 网络请求接口 的实例
        GetRequestInterface request = retrofit.create(GetRequestInterface.class);
        //对 发送请求 进行封装
        Call<Translation> call = request.getCall("I love you");

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<Translation>() {
            //请求成功时回调
            @Override
            public void onResponse(@NonNull Call<Translation> call, @NonNull Response<Translation> response) {
                // 步骤7：处理返回的数据结果
                if (response.body() != null) {
                    System.out.println(response.body().getTranslateResult().get(0).get(0).getTgt());
                    mTvTranslation.setText(response.body().getTranslateResult().get(0).get(0).getTgt());
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(@NonNull Call<Translation> call, @NonNull Throwable throwable) {
                System.out.println("连接失败");
            }
        });
    }
}
