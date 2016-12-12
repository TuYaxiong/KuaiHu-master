package krelve.app.kuaihu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import krelve.app.kuaihu.R;
import krelve.app.kuaihu.activity.MainActivity;
import krelve.app.kuaihu.model.StoriesEntity;
import krelve.app.kuaihu.util.Constant;
import krelve.app.kuaihu.util.PreUtils;

/**
 * Created by wwjun.wang on 2015/8/13.
 */
public class MainNewsItemAdapter extends BaseAdapter {
    private List<StoriesEntity> entities;
    private Context context;
    private ImageLoader mImageloader;
    private DisplayImageOptions options;
    private boolean isLight;

    public MainNewsItemAdapter(Context context) {
        this.context = context;
        this.entities = new ArrayList<>();
        mImageloader = ImageLoader.getInstance();//displayImage设置
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        isLight = ((MainActivity) context).isLight();
    }

    public void addList(List<StoriesEntity> items) {
        this.entities.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //1 定义静态内部类ViewHolder
    //2 Adapter的getView中实现功能
    //3 convertView为null(首次)加载view,且创建viewHolder对象
    //4 为viewHolder对象成员变量赋值
    //5 setTag()方法view与viewHolder绑定
    //6 否则重用 getTag()
    //7 给组件加数据.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //用到ViewHolder封装getView()每次返回的view setTag()缓存下次直接使用无须到布局中取
        ViewHolder viewHolder = null;
        if (convertView == null) {//为null 创建view
            //创建viewHolder
            viewHolder = new ViewHolder();
            //Textview(今日热闻)/TextView/ImageView
            convertView = LayoutInflater.from(context).inflate(R.layout.main_news_item, parent, false);
            //为viewHolder成员变量赋值
            viewHolder.tv_topic = (TextView) convertView.findViewById(R.id.tv_topic);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
            //view与viewHolder绑定
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String readSeq = PreUtils.getStringFromDefault(context, "read", "");//Preference取内容 设置标题文本颜色
        if (readSeq.contains(entities.get(position).getId() + "")) {
            viewHolder.tv_title.setTextColor(context.getResources().getColor(R.color.clicked_tv_textcolor));
        } else {
            viewHolder.tv_title.setTextColor(context.getResources().getColor(isLight ? android.R.color.black : android.R.color.white));
        }

        //Imageview布局设置颜色
        ((LinearLayout) viewHolder.iv_title.getParent().getParent().getParent()).setBackgroundColor(context.getResources().getColor(isLight ? R.color.light_news_item : R.color.dark_news_item));
        viewHolder.tv_topic.setTextColor(context.getResources().getColor(isLight ? R.color.light_news_topic : R.color.dark_news_topic));
        StoriesEntity entity = entities.get(position);
        if (entity.getType() == Constant.TOPIC) {//为指定类型内容 第一栏只显示topic(今日热闻)
            ((FrameLayout) viewHolder.tv_topic.getParent()).setBackgroundColor(Color.TRANSPARENT);
            viewHolder.tv_title.setVisibility(View.GONE);//不可见,其它view会占用它位置
            viewHolder.iv_title.setVisibility(View.GONE);
            viewHolder.tv_topic.setVisibility(View.VISIBLE);
            viewHolder.tv_topic.setText(entity.getTitle());//设置主题内容
        } else {//其余栏没有topic
            ((FrameLayout) viewHolder.tv_topic.getParent()).setBackgroundResource(isLight ? R.drawable.item_background_selector_light : R.drawable.item_background_selector_dark);
            viewHolder.tv_topic.setVisibility(View.GONE);//去掉topic位置
            viewHolder.tv_title.setVisibility(View.VISIBLE);
            viewHolder.iv_title.setVisibility(View.VISIBLE);
            viewHolder.tv_title.setText(entity.getTitle());
            mImageloader.displayImage(entity.getImages().get(0), viewHolder.iv_title, options);
        }
        return convertView;
    }

    public void updateTheme() {
        isLight = ((MainActivity) context).isLight();
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        TextView tv_topic;
        TextView tv_title;
        ImageView iv_title;
    }

}
