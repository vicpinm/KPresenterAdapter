package com.vicpin.sample.presenter;


import com.vicpin.kpresenteradapter.ViewHolderPresenter;
import com.vicpin.sample.model.Country;

/**
 * Created by Victor on 25/06/2016.
 */
public class HeaderPresenter extends ViewHolderPresenter<Country, HeaderPresenter.View> {

    @Override
    public void onCreate() {
        showNumItems();
    }

    public void showNumItems(){
        getView().setNumItems(getDataCollection().size());
    }

    public interface View {
        void setNumItems(int numItems);
    }
}
