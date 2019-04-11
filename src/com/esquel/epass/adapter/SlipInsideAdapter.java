package com.esquel.epass.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esquel.epass.R;

/**
 * 
 * @author joyaether
 * 
 */
public class SlipInsideAdapter extends BaseAdapter {

    Context activity;
    List<String[]> list;

    public SlipInsideAdapter(Context activity, List<String[]> list) {
        this.activity = activity;
        this.list = list;

    }

    List<String[]> removeNullElement(List<String[]> mList) {
        List<String[]> out = new ArrayList<String[]>();
        for (int i = 0; i < mList.size(); i++) {
            boolean add = true;
            for (int j = 0; j < mList.get(i).length; j++) {
                add = add && mList.get(i)[j] == null;
            }
            if (add) {
                out.add(mList.get(i));
            }
        }

        return out;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        String[] row = list.get(position);
        View convertView = view;
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.adapter_slip_inside, parent, false);
            holder = new ViewHolder();
            holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);

            holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv1.setText(row[0]);
        holder.tv2.setText(row[1]);
        if (position == getCount() - 1) {
            final int size = 11;
            holder.tv1.setTextSize(size);
            holder.tv2.setTextSize(size);
            holder.tv1.setTextColor(Color.GRAY);
            holder.tv2.setTextColor(Color.GRAY);
        }
        return convertView;
    }

    /**
     * 
     * @author hung
     * 
     */
    private class ViewHolder {
        TextView tv1;
        TextView tv2;

    }
}
