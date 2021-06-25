package com.github.dnaka91.drinkup.alarm

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.github.dnaka91.drinkup.R
import com.github.dnaka91.drinkup.databinding.DrinkAlarmActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DrinkAlarmActivity : AppCompatActivity() {
    private lateinit var binding: DrinkAlarmActivityBinding
    private val viewModel by viewModels<DrinkAlarmViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DrinkAlarmActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DrinkAlarmFragment.newInstance())
                .commitNow()
        }

        showScreen()
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.vibrate()) {
            val vibrator = getSystemService<Vibrator>() ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(300, 100), 0)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(300, 100), 0)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val vibrator = getSystemService<Vibrator>() ?: return
        vibrator.cancel()
    }

    private fun showScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        fun newInstance(context: Context): Intent = Intent(context, DrinkAlarmActivity::class.java)
    }
}
