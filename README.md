# android 上 mvp框架

## 1. mvp 和 mvc 
   mvc这个东西大家都非常熟悉了，在android项目中mvp大多的实现是，View层就是我买写的xml中的代码，Controller是我们的activity和fragment，model就是各种用来储存数据的bean。然而在真正开发中界面会随着数据的改变而改变，所以在activity中就会融合了ui的控制代码，又融合了数据的请求。这让activity变的十分臃肿，并且增加了耦合度，所以有人提出了更好的模式来改变这一现状那就是mvp模式。
![image](http://img.blog.csdn.net/20160120100717863)
如上图，这个是mvp和mvc流程的对比。mvc模式中的Model和view层都和Controller有联系，这个反应在代码中就是Activity会持有各种的bean,而Activity可以直接对bean进行处理并直接更改ui这些都是通过Activity来进行的，这样必会带来耦合。
而mvp模式就不一样这里view和model是被presenter隔离开的，View层只和Presenter有交互不能对model进行直接的修改，这样大量的逻辑代码就放到了presenter中，ui就真的被单独的隔离了出来。在mvp模式中我们熟悉的Activity 和 fragment只是代表View层，他们中不能含有数据，而且只和Presenter进行交互。Presenter层能调数据层接口对数据进行控制。这样view层和model就是完全解耦的了。而且我们还可以编写测试用的view来模拟用户的操作，从而实现对presenter的单元测试问题（比如大量的点击，批量的操作等问题都可以模拟出来）
## 2.mvp实践

关于mvp的形态google已经在今年早些时候给出了官方的实例。
这个是google官方mvp框架项目的地址：[google官方mvp](https://github.com/googlesamples/android-architecture)
google大神们推荐了多种mvp的架构的搭建采用了不同的库而产生了不同的方式。而今天我要将google最新的响应式编程库Agera融入到mvp框架中去。来看代码：
我写了一个获取手机中照片的小程序先看xml很很简单就是一个recyclerview和几个button
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_photo"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.hero.jhon.mvp.photo.photoActivity">
    <android.support.v7.widget.RecyclerView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id = "@+id/recycler"></android.support.v7.widget.RecyclerView>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text = "clear"
        android:id = "@+id/button_clear"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@+id/button_clear"
        android:id = "@+id/button_reload"
        android:text="reload"/>
    <ProgressBar
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_centerInParent="true"
        android:id = "@+id/load_bar"
        />

</RelativeLayout>
~~~
为了实现mvp参照google的框架我先写了这样了个基本的接口
BaseView 和 BasePresenter
~~~java

public interface BasePresenter  {

    void subscribe();

    void unsubscribe();

}



public interface BaseView {
 
     void setPresenter(BasePresenter basePresenter);
}

~~~
这俩个接口的写法基本招办google 的demo中mvp-rxjava的样式，这里一个基本的BasePresenter中附带的subscribe()订阅和一个unsubscribe()解除订阅的方法这样写更符号Rxjava和Agera这种响应式编程的用法。
我们来看具体的presenter 和 view 的实现，google的demo和其它mvp框架的区别在于，它将每个有具体功能的Presenter 和
View放在一个Contract中来统一管理，在设计这个photoContract就要考验你对这个设计的把握能力了好的工程师能将你要实现的方法快速的展现在这里。
~~java
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

~~
通过这里我们应该能很快的知道我们的view能做些什么，我们的Presenter又能做些什么。比如view中的showlong一看就是要展现processbar的方法，ClearDate()就是清除数据的方法。
好，接下来我们看具体View 和 Presenter的实现方式，首先是View就是我们的Activity。
~~~java

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
~~~
这里可以看到在view层（就是我们的Activity）我们只是负责ui的控制和调用Presenter中的方法并没有获得任何的数据甚至没有定义关于数据的任何变量，这样就可以做到完美的数据和ui的隔离，当ui不变数据改变的时候我们不需要对Activity做出任何的修改。
我们的Presenter是在onResume和onPause中调用了注册和解注册的方法。那数据是怎么获得，逻辑是怎么实现得来看看Presenter
~~~java
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
~~~
为了能使用Agera框架我们得Presenter要继承BaseObservable 来表示它是一个被观察者然后又实现了photoContract.Presenter,Updatable,Receiver<ArrayList<PhotoBean>>接口来表明它是一个presenter 并有观察者的特性。这里有大家可能要糊涂了为什么自己要来观察自己呢。很简单我们要保证逻辑的统一性我们只要关系数据的获取和获取数据的操作，而其中一切的生命周期都交给Agera来处理，我们只甚至数据源和订阅数据源。这些我会在Agera中具体讲解。我们只需要知道通过设置数据源和订阅我们能得到我们要的数据在合适的时机。通过agera框架我们能在accept中得到我们要的数据。在这里我们调用了view的AdapterNotify(mRepository.get().get())方法来让activity来实现ui的展示。
那在subscribe 和 unsubscribe中我们做了些什么呢？在subscribe我们创建了一个数据仓库并绑定了我们的presenter做为被观察者和观察者。数据仓库主要是进行数据的获取和处理的，这里我们的数据源是我已经封装好的单例类（这个类从
ContentResolver得到我们想要的照片的数据）它返回一个Supplier做为Agera的数据源。当数据请求完毕观察者会得到通知调用 update() 方法。
下面把PhotoRepository的数据获取贴出
~~~java
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
   ~~~
  我们使用mvp的优势相对mvc来说，逻辑代码被放到了presenter中model和view层完全解耦。而对于逻辑代码的处理通过使用Agera框架让我们从复杂的线程和生命周期的控制中解放出来。mvp更适合单元测试，更加条理清晰。
  当然mvp的缺点也是很明显的为了实现分离要写很多接口相关的代码，我们的学习成本会有所增加。
 github地址：[超级链接](https://github.com/jiangzhenguo/mvp_agrea)

