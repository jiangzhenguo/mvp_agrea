package com.hero.jhon.mvp.data.photo;

/**
 * Created by jhon on 2016/11/14.
 */

public class PhotoBean {

    public  String path;

    public String title;

    public long time;

    public PhotoBean(String title,String path,long time){
        this.title = title;
        this.path = path;
        this.time = time;
    }

}
