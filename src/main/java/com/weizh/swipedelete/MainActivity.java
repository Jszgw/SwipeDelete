package com.weizh.swipedelete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.weizh.swipedelete.manager.SwipeLayoutManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lvListView;
    private ArrayList<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        //1.设置数据
        strings = new ArrayList<>();
        for (int i=0;i<30;i++) {
            strings.add("name - "+i);
        }
        lvListView.setAdapter(new MyAdapter());
        lvListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if(scrollState==SCROLL_STATE_TOUCH_SCROLL){
                    //垂直滑动则需要关闭已经打开的swipelayout
                    SwipeLayoutManager.getInstance().closeSwipeLayout();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void initView() {
         lvListView=(ListView) findViewById(R.id.lv_list);
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder=null;
            if (convertView==null){
                convertView=View.inflate(getApplicationContext(),R.layout.list_item,null);
                holder=new ViewHolder();
                holder.tvName= (TextView) convertView.findViewById(R.id.tv_name);
                holder.tvDelete= (TextView) convertView.findViewById(R.id.tv_delete);
                convertView.setTag(holder);
            }else {
                holder= (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(strings.get(position));
            //给delete textview设置点击事件
            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //恢复swipelayout至初始位置
                    SwipeLayoutManager.getInstance().resumeSwipeLayout();
                    //清除swipelayout打开记录
                    SwipeLayoutManager.getInstance().clearCurrentLayout();
                    strings.remove(position);//从集合中移除对应的条目数据
                    notifyDataSetChanged();//通知数据更新
                }
            });
            return convertView;
        }
    }

    class ViewHolder{

        public TextView tvName;
        public TextView tvDelete;
    }
}
