package com.vicpin.sample.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.vicpin.sample.R
import com.vicpin.sample.view.fragment.AutoBindingFragment

class AutoBindingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, AutoBindingFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}