package krelve.app.kuaihu.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.List;

import krelve.app.kuaihu.R;
import krelve.app.kuaihu.activity.LatestContentActivity;
import krelve.app.kuaihu.activity.MainActivity;
import krelve.app.kuaihu.adapter.MainNewsItemAdapter;
import krelve.app.kuaihu.db.CacheDbHelper;
import krelve.app.kuaihu.model.Before;
import krelve.app.kuaihu.model.Latest;
import krelve.app.kuaihu.model.StoriesEntity;
import krelve.app.kuaihu.util.Constant;
import krelve.app.kuaihu.util.HttpUtils;
import krelve.app.kuaihu.util.PreUtils;
import krelve.app.kuaihu.view.Kanner;

/**
 * Created by wwjun.wang on 2015/8/12.
 */
public class MainFragment extends BaseFragment {//initView() initData
    private ListView lv_news;
    private MainNewsItemAdapter mAdapter;
    private Latest latest;
    private Before before;
    private Kanner kanner;
    private String date;
    private boolean isLoading = false;
    private Handler handler = new Handler();

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //toorbar标题
        ((MainActivity) mActivity).setToolbarTitle("今日热闻");
        //主内容区就一ListView(上面为head)
        View view = inflater.inflate(R.layout.main_news_layout, container, false);
        lv_news = (ListView) view.findViewById(R.id.lv_news);
        //ListView Header(自定义kanner组件)带自动更换图片功能
        View header = inflater.inflate(R.layout.kanner, lv_news, false);
        //Kanner继承FramLayout,实现OnClickListener
        kanner = (Kanner) header.findViewById(R.id.kanner);
        //设置kanner的Itemclick监听内部接口(默认是静态相关)
        //因为接口是不能实例化的，内部接口只有当它是静态的才有意义。因此，
        // 默认情况下，内部接口是静态的，不论你是否手动加了static关键字
        //扩展的方法 监听item click
        kanner.setOnItemClickListener(new Kanner.OnItemClickListener() {//点击header
            @Override
            public void click(View v, Latest.TopStoriesEntity entity) {//处理方法,加载点击位置的内容跳转到LatestContentActivity
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;//获取click位置
                StoriesEntity storiesEntity = new StoriesEntity();
                storiesEntity.setId(entity.getId());
                storiesEntity.setTitle(entity.getTitle());      //设置id/title
                Intent intent = new Intent(mActivity, LatestContentActivity.class);//// TODO: 30/11/2016
                intent.putExtra(Constant.START_LOCATION, startingLocation);
                intent.putExtra("entity", storiesEntity);
                intent.putExtra("isLight", ((MainActivity) mActivity).isLight());
                startActivity(intent);
                mActivity.overridePendingTransition(0, 0);
            }
        });
        //ListView添加Header
        lv_news.addHeaderView(header);
        //listView的 Adapter(BaseAdapter)
        mAdapter = new MainNewsItemAdapter(mActivity);
        lv_news.setAdapter(mAdapter);
        //ListView设置滚动监听
        lv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            //ListView滚动监听,当在顶部时,SwipeRefreshEnable
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //listView,在顶部(firstID==0) firstitem的view Top position of this view relative to its parent
                //firstitem为header header相对ListView父容器的距离为0时enable
                if (lv_news != null && lv_news.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    ((MainActivity) mActivity).setSwipeRefreshEnable(enable);
                //显示到该数据集最后了,加载更多
                    if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                        loadMore(Constant.BEFORE + date);
                    }
                }

            }
        });
        //Listview item点击监听
        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] startingLocation = new int[2];
                //点击组件的位置
                view.getLocationOnScreen(startingLocation);
                //转换为手指实际点击位置
                startingLocation[0] += view.getWidth() / 2;
                //Position of the item whose data we want within the adapter's data set.
                StoriesEntity entity = (StoriesEntity) parent.getAdapter().getItem(position);
                Intent intent = new Intent(mActivity, LatestContentActivity.class);//// TODO: 30/11/2016  
                //点击位置(用于Activity切换效果)
                intent.putExtra(Constant.START_LOCATION, startingLocation);
                intent.putExtra("entity", entity);//数据
                intent.putExtra("isLight", ((MainActivity) mActivity).isLight());//模式
                //获取Preferences id 读过的应该将字体变灰
                String readSequence = PreUtils.getStringFromDefault(mActivity, "read", "");
                //按条统计数量
                String[] splits = readSequence.split(",");
                StringBuffer sb = new StringBuffer();
                //超过200条,保留后100条
                if (splits.length >= 200) {
                    for (int i = 100; i < splits.length; i++) {//多于200将前面100条丢掉

                        sb.append(splits[i] + ",");
                    }
                    readSequence = sb.toString();
                }
                //现在点击的内容id,不存在,则加入preference
                if (!readSequence.contains(entity.getId() + "")) {
                    readSequence = readSequence + entity.getId() + ",";
                }
                PreUtils.putStringToDefault(mActivity, "read", readSequence);//key--value 存入sharedpreference
               //读过将title字体灰色
                TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_title.setTextColor(getResources().getColor(R.color.clicked_tv_textcolor));
                startActivity(intent);
                mActivity.overridePendingTransition(0, 0);
            }
        });
        return view;
    }

    private void loadFirst() {//网络请求,加载一屏数据
        isLoading = true;//加载数据标志
        if (HttpUtils.isNetworkConnected(mActivity)) {
            HttpUtils.get(Constant.LATESTNEWS, new TextHttpResponseHandler() {//news/latest
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //有网
                    //先更新缓存请求响应的数据到sqlite
                    //插入时，某条记录不存在则插入，存在则更新
                    SQLiteDatabase db = ((MainActivity) mActivity).getCacheDbHelper().getWritableDatabase();
                    db.execSQL("replace into CacheList(date,json) values(" + Constant.LATEST_COLUMN + ",' " + responseString + "')");
                    db.close();
                    //再解析响应
                    parseLatestJson(responseString);
                }

            });
        } else {
            SQLiteDatabase db = ((MainActivity) mActivity).getCacheDbHelper().getReadableDatabase();//无网读取SQL
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + Constant.LATEST_COLUMN, null);
            if (cursor.moveToFirst()) {//查询结果不为null 获取json变量的值
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseLatestJson(json);
            } else {
                isLoading = false;
            }
            cursor.close();
            db.close();
        }

    }

    private void parseLatestJson(String responseString) {//解析响应数据
        Gson gson = new Gson();
        latest = gson.fromJson(responseString, Latest.class);//反序列化对象 会调用bean来给对象属性值-->latest对象
        date = latest.getDate();//日期
        kanner.setTopEntities(latest.getTop_stories());//设置ListView Header内容
        //将Runnable加入到MessageQueue  Causes the Runnable r to be added to the message queue.
//        Causes the Runnable r to be added to the message queue.
//        The runnable will be run on the thread to which this handler is attached.
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<StoriesEntity> storiesEntities = latest.getStories();
                //将响应的第一条设置为'今日热闻'
                StoriesEntity topic = new StoriesEntity();
                topic.setType(Constant.TOPIC);
                topic.setTitle("今日热闻");
                storiesEntities.add(0, topic);
                mAdapter.addList(storiesEntities);
                isLoading = false;
            }
        });
    }
//加载更多
    private void loadMore(final String url) {
        isLoading = true;
        if (HttpUtils.isNetworkConnected(mActivity)) {
            HttpUtils.get(url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                    PreUtils.putStringTo(Constant.CACHE, mActivity, url, responseString);
                    SQLiteDatabase db = ((MainActivity) mActivity).getCacheDbHelper().getWritableDatabase();
                    //加载存起来 date
                    db.execSQL("replace into CacheList(date,json) values(" + date + ",' " + responseString + "')");
                    db.close();
                    parseBeforeJson(responseString);

                }

            });
        } else {
            //没网
            SQLiteDatabase db = ((MainActivity) mActivity).getCacheDbHelper().getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from CacheList where date = " + date, null);//查询data=='data'的所有行
            if (cursor.moveToFirst()) {//查询不为null
                //变量json的数据
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseBeforeJson(json);//解析缓存的数据
            } else {//查询为null
                db.delete("CacheList", "date < " + date, null);//删除指定行
                isLoading = false;
                //快餐条
                Snackbar sb = Snackbar.make(lv_news, "没有更多的离线内容了~", Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(getResources().getColor(((MainActivity) mActivity).isLight() ? android.R.color.holo_blue_dark : android.R.color.black));
                sb.show();
            }
            cursor.close();
            db.close();
        }
    }

    private void parseBeforeJson(String responseString) {
        Gson gson = new Gson();
        before = gson.fromJson(responseString, Before.class);
        if (before == null) {
            isLoading = false;
            return;
        }
        date = before.getDate();
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<StoriesEntity> storiesEntities = before.getStories();
                StoriesEntity topic = new StoriesEntity();
                topic.setType(Constant.TOPIC);
                topic.setTitle(convertDate(date));
                storiesEntities.add(0, topic);
                mAdapter.addList(storiesEntities);
                isLoading = false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        loadFirst();
    }

    private String convertDate(String date) {
        String result = date.substring(0, 4);
        result += "年";
        result += date.substring(4, 6);
        result += "月";
        result += date.substring(6, 8);
        result += "日";
        return result;
    }

    public void updateTheme() {
        mAdapter.updateTheme();
    }
}
