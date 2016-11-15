package com.hero.jhon.mvp.data.photo;

import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Supplier;

import java.util.ArrayList;

/**
 * Created by jhon on 2016/11/14.
 */

public interface GetDate<T> {

       Supplier<Result<T>> getDateSupplier();
}
