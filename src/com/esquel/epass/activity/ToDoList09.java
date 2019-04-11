package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.schema.Request;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.schema.Query;

public class ToDoList09 extends BaseGestureActivity {
    ListView listview;
    TextView selectedTab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        initUi();
    }

    private void initUi() {
        listview = (ListView) findViewById(R.id.listview);
        selectedTab = (TextView) findViewById(R.id.tab1);
        findViewById(R.id.right_first).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.title_menu)).setText("我的申请");
        List<Item> list = new ArrayList<Item>();
        list.add(new Item("未享受住房优惠证明", "2014年8月10日", "已完成", 1));
        list.add(new Item("离职证明", "2014年8月10日", "已完成", 2));
        list.add(new Item("休假证明", "2014年8月10日", "已完成", 3));
        list.add(new Item("未享受住房优惠证明", "2014年8月10日", "已完成", 1));
        list.add(new Item("离职证明", "2014年8月10日", "已完成", 2));
        list.add(new Item("休假证明", "2014年8月10日", "已完成", 3));
        listview.setAdapter(new SampleAdapter(this, list));
    }

    public void selectTab(View view) {
        selectedTab.setBackgroundColor(Color.TRANSPARENT);
        selectedTab.setTextColor(Color.parseColor("#a62740"));
        view.setBackgroundColor(Color.parseColor("#a62740"));
        ((TextView) view).setTextColor(Color.WHITE);
        selectedTab = (TextView) view;
        String tag = view.getTag().toString();
        if (tag.equals("1")) {
            List<Item> list = new ArrayList<Item>();
            list.add(new Item("未享受住房优惠证明", "2014年8月10日", "已完成", 1));
            list.add(new Item("离职证明", "2014年8月10日", "已完成", 2));
            list.add(new Item("休假证明", "2014年8月10日", "已完成", 3));
            list.add(new Item("未享受住房优惠证明", "2014年8月10日", "已完成", 1));
            list.add(new Item("离职证明", "2014年8月10日", "已完成", 2));
            list.add(new Item("休假证明", "2014年8月10日", "已完成", 3));
            listview.setAdapter(new SampleAdapter(this, list));
        } else if (tag.equals("4")) {
            List<Item> list = new ArrayList<Item>();
            list.add(new Item("未享受住房优惠证明", "2014年8月10日", "已完成", 1));
            list.add(new Item("离职证明", "2014年8月10日", "已完成", 2));
            list.add(new Item("休假证明", "2014年8月10日", "已完成", 3));
            listview.setAdapter(new SampleAdapter(this, list));
        }
    }

    class Item {
        private String title;
        private String date;
        private String des;
        private int status;

        public Item(String title, String date, String des, int status) {
            this.setTitle(title);
            this.setDate(date);
            this.setDes(des);
            this.setStatus(status);
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public class SampleAdapter extends BaseAdapter {

        List<Item> list;
        Activity activity;
        int positionFocus = -1;

        public SampleAdapter(Activity activity, List<Item> list) {
            this.activity = activity;
            this.list = list;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder = null;
            Item item = list.get(position);
            View convertView = view;
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.item_to_do, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.text1);
                holder.date = (TextView) convertView.findViewById(R.id.text2);
                holder.des = (TextView) convertView.findViewById(R.id.text3);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.des.setText(item.getDes());
            if (item.getStatus() == 3) {
                holder.icon.setVisibility(View.VISIBLE);
                holder.des.setTextColor(Color.parseColor("#fd3d3d"));
            } else if (item.getStatus() == 2) {
                holder.icon.setVisibility(View.INVISIBLE);
                holder.des.setTextColor(Color.parseColor("#669900"));
            } else {
                holder.icon.setVisibility(View.INVISIBLE);
                holder.des.setTextColor(Color.parseColor("#137ef8"));
            }
            holder.title.setText(item.getTitle());

            holder.date.setText(item.getDate());
            return convertView;
        }

        /**
         * 
         * @author joyaether
         * 
         */
        private class ViewHolder {
            private TextView title, date, des;

            private ImageView icon;
        }

    }
    
    private void getRequest() {
    	Query query = new Query();
    	//I think I would cache the user id in the app with using SharedPerferences
    	//so you just get it from SharedPerferences. I will notice you when I done this code.
    	query.fieldIsEqualTo(Request.USER_FIELD_NAME, "user_id");

    	//in mockup of 09, for the tab of "全部" is all so do not need to add this code
    	//for the tab of "待处理" is mean in progress so use 0
    	//for the tab of "已完成" is mean completed so use 1
    	//for the tab of "被退回" is mean rejected so use 2
    	//different status will have different text color in the tab of "全部"
    	//the status of rejected will have a bubble on the right of the text
    	query.fieldIsEqualTo(Request.STATUS_FIELD_NAME, 0);
    	
    	//need to show the task name so need to expand the task object
    	query.expandField(Request.TASK_FIELD_NAME);


    	((AppApplication) getApplication()).getRestStore().performQuery(query, "requests", new StoreCallback() {

    						@Override
    	                    public void success(DataElement element, String resource) {  
    							// the element suppose is ArrayElement
    							// the sdk expected the datastore call is putting to other thread, so must need to 
    							// put the ui stuff to UI Thread
    							runOnUiThread(new Runnable() {
    							
    								@Override
    								public void run() {
    									// do the ui stuff
    								}
    							});
    	                    }

    	                    @Override
    	                    public void failure(DatastoreException ex, String resource) {
    	                        // the sdk expected the datastore call is putting to other thread, so must need to 
    							// put the ui stuff to UI Thread
    							runOnUiThread(new Runnable() {
    							
    								@Override
    								public void run() {
    									// do the ui stuff
    								}
    							});
    	                    }

    	});
    }
}
