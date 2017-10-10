package com.vicpin.sample.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.vicpin.sample.R
import com.vicpin.sample.view.fragment.MainFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, MainFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        (supportFragmentManager.findFragmentById(android.R.id.content) as MainFragment).toggleAdapter()
        return super.onOptionsItemSelected(item)
    }
}
