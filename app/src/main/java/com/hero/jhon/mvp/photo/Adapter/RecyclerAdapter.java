package com.hero.jhon.mvp.photo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hero.jhon.mvp.R;
import com.hero.jhon.mvp.data.photo.PhotoBean;

import java.util.ArrayList;

/**
 * Created by jhon on 2016/11/14.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageHolder> {

    @NonNull
    private Context context;
    private ArrayList<PhotoBean> PhotoLists;
    private LayoutInflater mInflater;
    private int width;

    public RecyclerAdapter(@NonNull  Context context){
       this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
    }

    public void setPhotoLists( @NonNull  ArrayList<PhotoBean> lists){
        this.PhotoLists = lists;
    }

    @Override
    public RecyclerAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.itme_photo,parent,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = width / 4;
        view.setLayoutParams(layoutParams);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ImageHolder holder, int position) {
        PhotoBean bean = PhotoLists.get(position);
        Glide.with((Activity)context).load(bean.path).into(holder.mImagePic);
    }

    @Override
    public int getItemCount() {
        if(PhotoLists != null){
            return PhotoLists.size();
        } else {
            return 0;
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder{
        public ImageView mImagePic;
        public ImageHolder(View itemView) {
            super(itemView);
            mImagePic = (ImageView)itemView.findViewById(R.id.image_test);
        }
    }
}
