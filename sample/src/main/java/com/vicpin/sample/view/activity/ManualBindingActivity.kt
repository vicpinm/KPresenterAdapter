package com.vicpin.sample.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.vicpin.sample.R
import com.vicpin.sample.view.fragment.ManualBindingFragment


class ManualBindingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, ManualBindingFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        (supportFragmentManager.findFragmentById(android.R.id.content) as ManualBindingFragment).toggleAdapter()
        return super.onOptionsItemSelected(item)
    }
}
