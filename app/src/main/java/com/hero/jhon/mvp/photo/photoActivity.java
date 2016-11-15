package com.hero.jhon.mvp.photo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.agera.Observable;
import com.hero.jhon.mvp.R;
import com.hero.jhon.mvp.data.photo.PhotoBean;
import com.hero.jhon.mvp.mvp.BasePresenter;
import com.hero.jhon.mvp.photo.Adapter.RecyclerAdapter;

import java.util.ArrayList;

public class photoActivity extends AppCompatActivity implements photoContract.View,View.OnClickListener {
    private RecyclerView mRecycler;
    private photoContract.Presenter mPhotoPresenter;
    private RecyclerAdapter mAdapter;
    private GridLayoutManager mManager;
    private Button mButtonClear;
    private ProgressBar mBar;
    private Button mButtonReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mManager = new GridLayoutManager(this,4);
        mRecycler.setLayoutManager(mManager);

        mAdapter = new RecyclerAdapter(this);
        mRecycler.setAdapter(mAdapter);
        mButtonClear = (Button)findViewById(R.id.button_clear);
        mButtonClear.setOnClickListener(this);
        mButtonReload = (Button)findViewById(R.id.button_reload);
        mButtonReload.setOnClickListener(this);
        mBar = (ProgressBar)findViewById(R.id.load_bar);
        mPhotoPresenter = new PhotoPresenter(this);
    }

    @Override
    public void setPresenter(BasePresenter basePresenter) {
         mPhotoPresenter = (photoContract.Presenter)basePresenter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_clear:
                mPhotoPresenter.Clear();
                break;
            case R.id.button_reload:
                mPhotoPresenter.reload();
                break;
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        mPhotoPresenter.unsubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPhotoPresenter.subscribe();
    }

    @Override
    public void AdapterNotify(ArrayList<PhotoBean> lists) {
        mAdapter.setPhotoLists(lists);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void ClearDate() {
        ArrayList<PhotoBean> lists = new ArrayList<>();
        mAdapter.setPhotoLists(lists);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void ShowLoading() {
        mBar.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
    }

    @Override
    public void HideLoading() {
        mBar.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
    }
}
