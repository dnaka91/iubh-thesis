package com.github.dnaka91.bender.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.dnaka91.bender.app.databinding.ActivityMainBinding
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BenchmarkFragment.newInstance())
                .commitNow()
        }
    }
}