package com.vicpin.sample.model

import android.view.View
import com.vicpin.butcherknife.annotation.BindImage
import com.vicpin.butcherknife.annotation.BindText
import com.vicpin.kpa.annotation.AutoViewHolder
import com.vicpin.sample.R

/**
 * Created by Victor on 25/06/2016.
 */
@AutoViewHolder
data class Town(@BindText(id = R.id.name, visibilityIfEmpty = View.GONE, isHtml = true, template = R.string.townName)
                val name: String,
                @BindImage(id = R.id.img)
                val url: String)