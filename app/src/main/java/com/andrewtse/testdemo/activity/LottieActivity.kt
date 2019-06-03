package com.andrewtse.testdemo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrewtse.testdemo.R
import kotlinx.android.synthetic.main.activity_lottie.*

class LottieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottie)

        lottie_animation_view.repeatCount = -1
        lottie_animation_view.setAnimation("scanning_nearby.json")
        lottie_animation_view.playAnimation()
    }
}
