package com.vicpin.kpresenteradapter

import androidx.annotation.LayoutRes
import android.view.View
import android.widget.TextView
import com.vicpin.kpresenteradapter.model.ViewInfo
import kotlin.reflect.KClass

/**
 * Created by Victor on 01/11/2016.
 */
class SingleLinePresenterAdapter<T: Any>(@LayoutRes val layoutResourceId: Int) : PresenterAdapter<T>(){

    override fun getViewInfo(position: Int) = ViewInfo(SingleLineViewHolder::class as KClass<out ViewHolder<T>>, layoutResourceId)

    class SingleLineViewHolder<T: Any>(override val containerView: View): ViewHolder<T>(containerView), SingleLinePresenter.View {

        override var presenter = SingleLinePresenter<T>()

        override fun setText(text: String) {
            if(itemView.findViewById<View>(R.id.text) == null){
                throw IllegalArgumentException("View provided for single line adapter should contain a TextView with id: \"text\"")
            }
            itemView.findViewById<TextView>(R.id.text)?.text = text
        }
    }

    class SingleLinePresenter<T : Any> :  ViewHolderPresenter<T, SingleLinePresenter.View>() {

        override fun onCreate() {
            view?.setText(data.toString())
        }

        interface View {
            fun setText(text: String)
        }
    }

}
