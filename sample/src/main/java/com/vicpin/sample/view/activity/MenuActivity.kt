package com.vicpin.sample.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vicpin.sample.R
import kotlinx.android.synthetic.main.activity_menu.*

/**
 * Created by victor on 27/12/17.
 */
public class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        autoBinding.setOnClickListener {
            startActivity(Intent(this, AutoBindingActivity::class.java))
        }

        manualBinding.setOnClickListener {
            startActivity(Intent(this, ManualBindingActivity::class.java))
        }




    }
}