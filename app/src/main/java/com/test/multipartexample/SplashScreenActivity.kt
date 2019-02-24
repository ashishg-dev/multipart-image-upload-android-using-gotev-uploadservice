package com.test.multipartexample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {

    val TAG = SplashScreenActivity::class.java.simpleName
    val SPLASH_TIME_OUT = 2000L
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        showSplashScreen()
    }


    fun showSplashScreen() {
        Handler().postDelayed(Runnable {

            val i = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(i)
            //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            finish()
        }, SPLASH_TIME_OUT)
    }
}
