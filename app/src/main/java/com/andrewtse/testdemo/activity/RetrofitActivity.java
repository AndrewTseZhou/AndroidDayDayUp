package com.andrewtse.testdemo.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.andrewtse.testdemo.R;
import com.andrewtse.testdemo.retrofit.GetRequestInterface;
import com.andrewtse.testdemo.retrofit.Translation;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitActivity extends AppCompatActivity {

    private static final String TAG = "RetrofitActivity";

    @BindView(R.id.btn_tradition)
    public Button mBtnTradition;
    @BindView(R.id.btn_rx_java)
    public Button mBtnRxJava;
    @BindView(R.id.tv_translation)
    public TextView mTvTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        ButterKnife.bind(this);

        mBtnTradition.setOnClickListener(v -> requestTradition());
        mBtnRxJava.setOnClickListener(v -> requestRxJava());
    }

    private void requestTradition() {
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

    private void requestRxJava() {
        //步骤4：创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://fanyi.youdao.com/") // 设置 网络请求 Url
            .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
            .build();

        // 步骤5:创建 网络请求接口 的实例
        GetRequestInterface request = retrofit.create(GetRequestInterface.class);

        // 步骤6：采用Observable<...>形式 对 网络请求 进行封装
        Observable<Translation> observable = request.getRxJavaCall("I love you");

        // 步骤7：发送网络请求
        observable.subscribeOn(Schedulers.io())               // 在IO线程进行网络请求
                  .observeOn(AndroidSchedulers.mainThread())  // 回到主线程 处理请求结果
                  .subscribe(new Observer<Translation>() {
                      @Override
                      public void onSubscribe(Disposable d) {
                          Log.d(TAG, "开始采用subscribe连接");
                      }

                      @Override
                      public void onNext(Translation result) {
                          // 步骤8：对返回的数据进行处理
                          mTvTranslation.setText(result.getTranslateResult().get(0).get(0).getTgt());
                      }

                      @Override
                      public void onError(Throwable e) {
                          Log.d(TAG, "请求失败");
                      }

                      @Override
                      public void onComplete() {
                          Log.d(TAG, "请求成功");
                      }
                  });
    }
}
