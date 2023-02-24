package com.hahnemanntechnology.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.hahnemanntechnology.R
import com.hahnemanntechnology.firestore.FirestoreClass
import kotlinx.android.synthetic.main.activity_splash_acticity.*

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_acticity)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler().postDelayed(
            {

                // If the user is logged in once and did not logged out manually from the app.
                // So, next time when the user is coming into the app user will be redirected to MainScreen.
                // If user is not logged in or logout manually then user will  be redirected to the Login screen as usual.

                // Get the current logged in user id
             //   val currentUserID = FirestoreClass().getCurrentUserID()

             //   if (currentUserID.isNotEmpty()) {
                    // Launch dashboard screen.
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
             //   } else {
                    // Launch the Login Activity
              //      startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
              //  }
              //  finish() // Call this when your activity is done and should be closed.
            },
            2500
        )
    // Here we pass the delay time in milliSeconds after which the splash activity will disappear.
        val myanim: Animation = AnimationUtils.loadAnimation(this ,R.anim.my_splash_animator)
        logo!!.startAnimation(myanim)
    }
}