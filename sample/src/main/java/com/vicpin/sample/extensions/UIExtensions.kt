package com.vicpin.sample.extensions

import android.support.v4.app.Fragment
import android.widget.Toast

/**
 * Created by Victor on 01/11/2016.
 */

fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(activity, text, duration).show()
}
