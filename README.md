 <p align="center">
  <img width="80%" src ="/kpresenteradapter.png" />
</p>
KPresenterAdapter is a lighweight Android library to implement adapters for your RecyclerViews in a clean way, using the MVP pattern. The main purpose of this library is to organize your adapter related code so that you will only have to focus on writing the pieces of code that really matters: view classes and presenter classes for that views, like you would do with your fragments or activities. Additionally, this library provides other useful features you might find useful.

## Features
  * Simple framework to write viewholder and presenter classes, following the MVP pattern, and avoiding writting boilerplate code related with adapters. 
  * View representation and view logic code decoupled from adapter framework classes, so that they are much easier to test.
  * Easy management of different types of views in the same collection.
  * Lifecycle callbacks in presenter clases. You can control view creation and destroy for each adapter position. Presenters are notified when they are destroyed to perform clear and unsubscribe operations if needed.
  * Custom presenter creation. You are responsible for creating presenter instance the same way yo usually do in your Activities or Fragments, which allows you to use tools like Dagger to inject your dependencies (see description below for details).
  * Easy scroll management

## Architecture overview
The diagram below shows how this library is built in order to apply MVP pattern to adapter classes. As you can see, you are only responsible to implement the classes with yellow background: viewholder class and its presenter class. 
<p align="center">
  <img src ="/uml_diagram.png" />
</p>


## Usage
Sample project is provided with this library. Next you will find detailed instructions to create adapters with a single type of object and adapters with collections of different types of objects.

### Single view type adapter

For adapters with a unique type of view, there is no need to create any adapter class. SimplePresenterAdapter is provided for this kind of collections.
 
##### Adapter creation sample for a list of countries. 
Extracted from the sample project, CountryView.kt is the class which implements the view layer for each adapter position, the same way Activities or Fragment does. You have to pass a reference to this class and the layout resource file to create an instance of SimplePresenterAdapter
  
    val adapter = SimplePresenterAdapter(CountryView::class, R.layout.adapter_country)
    recyclerView.adapter = adapter

### View class
Your view class inherits from ViewHolder<Model> class. As mentioned earlier, this class is responsible for implementing the view layer in MPV pattern, and provide a presenter instance. **This presenter instance will be used only for this viewholder instance. As your viewholder is reused for other adapter positions when you scroll, this presenter instance will be reused to, so you don't have to worry about performance or memory issues.**

 ```kotlin
 class CountryView(itemView: View) : ViewHolder<Country>(itemView), CountryPresenter.View {

    override var presenter = CountryPresenter()

    init {
        deleteButton.setOnClickListener { presenter.onDeleteItem() }
    }

    override fun setCountryName(text: String) {
        countryName.text = text
    }

    override fun setImage(resourceId: Int) {
        imageView.setImageResource(resourceId)
    }
}
 ```
 
As you can see, this class is very similar to a fragment or activity class. It creates a presenter instance, sets the listeners for the view, and implements the interface for its presenter. 

### Presenter class
Class responsible for implementing the presenter layer in MPV pattern, equivalent to any other presenter. It inherits from ViewHolderPresenter<Model, View> class. 
This class is generic and you need to indicate two types, your adapter model class (<Country> in this sample) and your presenter view interface class. So following our sample, the declaration of our CountryPresenter class will be like like:
 ```kotlin
    class CountryPresenter : ViewHolderPresenter<Country, CountryPresenter.View>() { ... }
 ```
**ViewHolderPresenter receives the following lifecycle events:**
 
 <p align="center">
  <img src ="/lifecycle.png" />
</p>


**onCreate method is mandatory and the rest are optional.**

Inside your presenter class, you have access to a "data" parameter, in order to get the data instance to be bound to that adapter position. Also, you have access to a "dataCollection" parameter if you need to perform other operations with your entire collection. 

Also, inside your presenter class, you have access to the "view" parameter, in order to interact with your view class as you normally do in presenter classes. Below you can see a simplified version of the CountryPresenter class:

```kotlin
class CountryPresenter : ViewHolderPresenter<Country, CountryPresenter.View>() {

    override fun onCreate() {
        view?.setCountryName(data.name)
        view?.setImage(data.imageResourceId)
    }

    fun onDeleteItem() {
        deleteItemFromCollection()
    }

    interface View {
        fun setCountryName(name: String)
        fun setImage(resourceId: Int)
    }
}
```

In this sample, you can see another utility method called ```deleteItemFromCollection()```, which allows you to delete the item from your current collection loaded in the adapter with a smooth animation.

Presenter classes also provide a callback method that you can override called onScrollStoped(). This method is invoked every time you perform scroll in your list and it reach the IDLE status. Also, in your presenter class, you have access to a parameter called "scrollStatus", which is always updated with tha last scroll status reported by your recyclerView. It can contains one of the three possible exiting statuses: ```OnScrollListener.SCROLL_STATE_IDLE```, ```OnScrollListener.SCROLL_STATE_TOUCH_SCROLL```, ```OnScrollListener.SCROLL_STATE_FLING```.

### Multiple view type adapter

It is very easy to implement an adapter with multiple view types. Instead of use SimpleAdapterPresenter, you have to create a class which extends from PresenterAdapter class, in order to implement your mapping logic between your models and your views. 

PresenterAdapter class has only one abstract method you have to implement, ```getViewInfo(position: Int)``` method. This method returns an instance of ViewInfo class, which holds an association between your view class and your layour resource for a given position.


##### Example of different types of views based on item position, using the same view class and differents layouts:
```kotlin
class MultipleAdapter: PresenterAdapter<Country> {

    override fun getViewInfo(position: Int): ViewInfo<Country> {
        return if(position % 2 == 0)
            ViewInfo(CountryView::class, R.layout.adapter_country_even)
        else
            ViewInfo(CountryView::class, R.layout.adapter_country_odd)
    }
}
```
##### Example of different types of views based on item properties, using diferent view clases and layouts:
```kotlin
class MultipleAdapter: PresenterAdapter<Country> {

    override fun getViewInfo(position: Int): ViewInfo<Country> {
        return if((getItem(position).isFavourite())
            ViewInfo(FavouriteItemView::class, R.layout.adapter_favourite_item)
        else
            ViewInfo(NormalItemView::class, R.layout.adapter_normal_item)
    }
```
### Event listeners

Click and long click listeners methods are provided to be invoked when users interacts with your views. Also, you can set a custom object listener to be manually invoked from your view class when you want. You can see an example of these listeners in ManualBindingFragment.kt from sample module:

```kotlin
 private fun appendListeners() {
        adapter.apply {
            itemClickListener = { item, view -> showToast("Country clicked ${item.name}) }
            itemLongClickListener = { item, view -> showToast("Country long pressed ${item.name}) }
            customListener = this@ManualBindingFragment
        }
    }
```


## Proguard

    -keepclassmembers public class * extends com.vicpin.presenteradapter.ViewHolder {
        public <init>(...);
    }


## Download

Grab via Gradle:
```groovy
repositories {
    mavenCentral()
}

compile 'com.github.vicpinm:kpresenteradapter:2.0.2'
```

## Author

Víctor Manuel Pineda Murcia | http://vicpinm.github.io/PresenterAdapter/

