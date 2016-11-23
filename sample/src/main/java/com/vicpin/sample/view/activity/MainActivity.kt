package com.vicpin.sample.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.vicpin.sample.R
import com.vicpin.sample.view.fragment.MainFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, MainFragment()).commit()
    }
}
