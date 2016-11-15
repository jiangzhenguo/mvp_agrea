package com.hero.jhon.mvp.photo;

import android.content.ContentResolver;

import com.google.android.agera.Observable;
import com.hero.jhon.mvp.data.photo.PhotoBean;
import com.hero.jhon.mvp.mvp.BasePresenter;
import com.hero.jhon.mvp.mvp.BaseView;

import java.util.ArrayList;

/**
 * Created by jhon on 2016/11/14.
 */

public interface photoContract {

    interface  View extends BaseView{

        void AdapterNotify(ArrayList<PhotoBean> lists);

        void ClearDate();

        void ShowLoading();

        void HideLoading();


    }

    interface Presenter extends BasePresenter{
        void Clear();

        void reload();
    }
}
