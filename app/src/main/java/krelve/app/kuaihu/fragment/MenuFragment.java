package krelve.app.kuaihu.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import krelve.app.kuaihu.R;
import krelve.app.kuaihu.activity.MainActivity;
import krelve.app.kuaihu.model.NewsListItem;
import krelve.app.kuaihu.util.Constant;
import krelve.app.kuaihu.util.HttpUtils;
import krelve.app.kuaihu.util.PreUtils;
//抽屉打开页面...
public class MenuFragment extends BaseFragment implements OnClickListener {
    private ListView lv_item;
    private TextView tv_download, tv_main, tv_backup, tv_login;
    private LinearLayout ll_menu;
    // private static String[] ITEMS = { "日常心理学", "用户推荐日报", "电影日报", "不许无聊",
    // "设计日报", "大公司日报", "财经日报", "互联网安全", "开始游戏", "音乐日报", "动漫日报", "体育日报" };
    private List<NewsListItem> items;
    private Handler handler = new Handler();
    private boolean isLight;
    private NewsTypeAdapter mAdapter;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);//抽屉打开显示的Fragment
        ll_menu = (LinearLayout) view.findViewById(R.id.ll_menu);
        tv_login = (TextView) view.findViewById(R.id.tv_login);
        tv_backup = (TextView) view.findViewById(R.id.tv_backup);
        tv_download = (TextView) view.findViewById(R.id.tv_download);
        tv_download.setOnClickListener(this);
        tv_main = (TextView) view.findViewById(R.id.tv_main);//tv_main有加响应 closeMenu/loadLatest
        tv_main.setOnClickListener(this);
        lv_item = (ListView) view.findViewById(R.id.lv_item);
        //点击ListView Item 切换NewFragment到主内容区
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {//主要是这个ListView

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getFragmentManager()
                        .beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)//切换时的动画效果
                        .replace(
                                R.id.fl_content,
                                new NewsFragment(items.get(position)
                                        .getId(), items.get(position).getTitle()), "news").commit();//切换Fragment    tag
                ((MainActivity) mActivity).setCurId(items.get(position).getId());
                ((MainActivity) mActivity).closeMenu();
            }
        });
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        isLight = ((MainActivity) mActivity).isLight();
        items = new ArrayList<NewsListItem>();
        if (HttpUtils.isNetworkConnected(mActivity)) {
            HttpUtils.get(Constant.THEMES, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {//获取主题列表内容响应
                    super.onSuccess(statusCode, headers, response);
                    String json = response.toString();
                    PreUtils.putStringToDefault(mActivity, Constant.THEMES, json);//列表数据存入preference,以便无网时可加载列表
                    parseJson(response);
                }
            });
        } else {
            String json = PreUtils.getStringFromDefault(mActivity, Constant.THEMES, "");
            try {
                JSONObject jsonObject = new JSONObject(json);
                parseJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void parseJson(JSONObject response) {
        try {
            JSONArray itemsArray = response.getJSONArray("others");//Java本身的Json支持
            for (int i = 0; i < itemsArray.length(); i++) {
                NewsListItem newsListItem = new NewsListItem();
                JSONObject itemObject = itemsArray.getJSONObject(i);
                newsListItem.setTitle(itemObject.getString("name"));
                newsListItem.setId(itemObject.getString("id"));
                items.add(newsListItem);//List集合数据 newsListItem(id/title)
            }
            mAdapter = new NewsTypeAdapter();
            lv_item.setAdapter(mAdapter);//列表项内容为(title)
            updateTheme();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class NewsTypeAdapter extends BaseAdapter {//lv_item的Adapter

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.menu_item, parent, false);//TextView
            }
            TextView tv_item = (TextView) convertView
                    .findViewById(R.id.tv_item);
            tv_item.setTextColor(getResources().getColor(isLight ? R.color.light_menu_listview_textcolor : R.color.dark_menu_listview_textcolor));
            tv_item.setText(items.get(position).getTitle());//内容为title
            return convertView;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main:
                ((MainActivity) mActivity).loadLatest();
                ((MainActivity) mActivity).closeMenu();
                break;
        }
    }

    public void updateTheme() {
        isLight = ((MainActivity) mActivity).isLight();
        ll_menu.setBackgroundColor(getResources().getColor(isLight ? R.color.light_menu_header : R.color.dark_menu_header));
        tv_login.setTextColor(getResources().getColor(isLight ? R.color.light_menu_header_tv : R.color.dark_menu_header_tv));
        tv_backup.setTextColor(getResources().getColor(isLight ? R.color.light_menu_header_tv : R.color.dark_menu_header_tv));
        tv_download.setTextColor(getResources().getColor(isLight ? R.color.light_menu_header_tv : R.color.dark_menu_header_tv));
        tv_main.setBackgroundColor(getResources().getColor(isLight ? R.color.light_menu_index_background : R.color.dark_menu_index_background));
        lv_item.setBackgroundColor(getResources().getColor(isLight ? R.color.light_menu_listview_background : R.color.dark_menu_listview_background));
        mAdapter.notifyDataSetChanged();
    }
}
