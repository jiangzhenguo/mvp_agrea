package com.hero.jhon.mvp.data.photo;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.hero.jhon.mvp.BaseApplication;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jhon on 2016/11/14.
 */

public class PhotoRepository implements GetDate<ArrayList<PhotoBean>> {

    private Executor executor = Executors.newSingleThreadExecutor();

    public ContentResolver mContentResolver;

    Repository<Result<ArrayList<PhotoBean>>> repository;

    private PhotoRepository() {
        mContentResolver = BaseApplication.getContext().getContentResolver();
    }

    public static PhotoRepository getInstance() {
        return SingleInstance.repository;
    }

    private static class SingleInstance {
       private static PhotoRepository repository = new PhotoRepository();
    }


    @Override
    public Supplier<Result<ArrayList<PhotoBean>>> getDateSupplier() {
        return new Supplier<Result<ArrayList<PhotoBean>>>() {
            @NonNull
            @Override
            public Result<ArrayList<PhotoBean>> get() {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    Log.d("ddd", "这是主线程");
                }
                ArrayList<PhotoBean> list = new ArrayList<PhotoBean>();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] select = new String[]{
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.TITLE
                };
                Cursor cursor = mContentResolver.query(uri, select, null, null, null);
                try {
                    if (cursor != null) {
                        int titlenum = cursor.getColumnIndex(MediaStore.Images.Media.TITLE);
                        int datanum = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        int timenum = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            String title = cursor.getString(titlenum);
                            String date = cursor.getString(datanum);
                            long time = cursor.getLong(timenum);
                            PhotoBean bean = new PhotoBean(title, date, time);
                            list.add(bean);
                        }
                    }
                } catch (Exception e) {

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                return Result.present(list);
            }
        };
    }
}
