package com.vicpin.sample.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.vicpin.sample.R
import com.vicpin.sample.view.fragment.MultiBindingFragment


class MultiBindingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, MultiBindingFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        (supportFragmentManager.findFragmentById(android.R.id.content) as MultiBindingFragment).toggleAdapter()
        return super.onOptionsItemSelected(item)
    }
}
