package com.hero.jhon.mvp.photo;


import android.support.annotation.NonNull;

import com.google.android.agera.BaseObservable;
import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.Updatable;
import com.hero.jhon.mvp.data.photo.PhotoBean;
import com.hero.jhon.mvp.data.photo.PhotoRepository;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Created by jhon on 2016/11/14.
 */

public class PhotoPresenter extends BaseObservable implements photoContract.Presenter,Updatable,Receiver<ArrayList<PhotoBean>> {

    private photoContract.View mView;
    private Repository<Result<ArrayList<PhotoBean>>> mRepository;
    private Executor executor = Executors.newSingleThreadExecutor();

    public PhotoPresenter(photoContract.View view){
        mView = view;
    }

    @Override
    public void Clear() {
        mView.ClearDate();
    }

    @Override
    public void subscribe() {
        Supplier<Result<ArrayList<PhotoBean>>> supplier = PhotoRepository.getInstance().getDateSupplier();
        mRepository = Repositories.repositoryWithInitialValue(Result.<ArrayList<PhotoBean>>absent())
                .observe(this)
                .onUpdatesPerLoop()
                .goTo(executor)
                .thenGetFrom(supplier).compile();
       mView.ShowLoading();
       mRepository.addUpdatable(this);
    }


    @Override
    public void update() {
        mRepository.get().ifSucceededSendTo(this);
    }

    @Override
    public void unsubscribe() {
        mRepository.removeUpdatable(this);
    }

    @Override
    public void reload() {
        dispatchUpdate();
    }

    @Override
    public void accept(@NonNull ArrayList<PhotoBean> value) {
        mView.HideLoading();
        mView.AdapterNotify(mRepository.get().get());
    }
}
