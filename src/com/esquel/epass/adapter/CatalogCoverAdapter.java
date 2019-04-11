package com.esquel.epass.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.ArticleListActivity;
import com.esquel.epass.activity.ChannelListFlipActivity;
import com.esquel.epass.item.DisplayItem;
import com.esquel.epass.utils.Constants;
import com.joyaether.datastore.util.ImageDownloader;

/**
 * 
 * @author joyaether
 * 
 */
public class CatalogCoverAdapter extends BaseAdapter {

    Activity activity;
    List<DisplayItem> list;
    ViewHolderHeader viewHolderHeader;
    ViewHolder viewHolder;
    static final int MORE_POSITION = 3;
    private ImageDownloader imageDownloader;

    public CatalogCoverAdapter(Activity activity, List<DisplayItem> list) {
        this.activity = activity;
        this.list = list;
        if (this.list.size() > MORE_POSITION) {
            DisplayItem displayItem = new DisplayItem();
            displayItem.setChannelName("更多");
            list.add(MORE_POSITION, displayItem);
        }

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final DisplayItem item = list.get(position);

        if (position < MORE_POSITION) {
            // if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_catalog_cover_2, parent, false);
            viewHolderHeader = new ViewHolderHeader();
            viewHolderHeader.title = (TextView) convertView.findViewById(R.id.title);
            viewHolderHeader.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolderHeader);
            // } else {
            // viewHolderHeader = (ViewHolderHeader) convertView.getTag();
            // }
            viewHolderHeader.title.setText(item.getChannelName());
            displayImage(item.getImage(), viewHolderHeader.icon);
            View view = convertView.findViewById(R.id.filter_image);
            if (view != null) {
                setFilterImage(view, item.getFilterImageName());
            }
        } else {
            // if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_catalog_cover, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
            // } else {
            // viewHolder = (ViewHolder) convertView.getTag();
            // }
            if (position == MORE_POSITION) {
                viewHolder.icon.setVisibility(View.GONE);
                viewHolder.title.setTextColor(Color.GRAY);
                if (convertView.findViewById(R.id.arrow) != null) {
                    convertView.findViewById(R.id.arrow).setVisibility(View.GONE);
                }
            } else {
                viewHolder.icon.setVisibility(View.VISIBLE);
                viewHolder.title.setTextColor(Color.BLACK);
                if (convertView.findViewById(R.id.arrow) != null) {
                    convertView.findViewById(R.id.arrow).setVisibility(View.VISIBLE);
                }
            }
            viewHolder.title.setText(item.getChannelName());
            displayImage(item.getImage(), viewHolder.icon);
        }

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (position == MORE_POSITION) {
                    return;
                }
                Intent intent = new Intent(activity, ArticleListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("id", item.getChannelId());
                intent.putExtra(ChannelListFlipActivity.KEY_TITLE, item.getChannelName());
                activity.startActivity(intent);
            }

        });

        return convertView;
    }

    private void displayImage(String url, ImageView imageView) {
        if (url == null || url.length() == 0) {
            imageView.setImageResource(0);
            return;
        }
        if (getImageDownloader() != null) {
            getImageDownloader().download(url, R.drawable.icon_loading_image, Constants.DEFAULT_IMAGE_WIDTH, Constants.DEFAULT_IMAGE_HEIGHT, imageView);
        }
    }

    private void setFilterImage(View view, String filterName) {
        int resourceId = 0;
        if (filterName == null) {
            resourceId = R.drawable.f_000000;
        } else {
            resourceId = activity.getResources().getIdentifier("f_" + filterName, "drawable", "com.esquel.epass");
        }

        if (resourceId == 0) {
            resourceId = R.drawable.f_000000;
        }
        view.setBackgroundResource(resourceId);

    }

    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    public void setImageDownloader(ImageDownloader imageDownloader) {
        this.imageDownloader = imageDownloader;
    }

    /**
     * 
     * @author joyaether
     * 
     */
    private class ViewHolderHeader {
        private TextView title;
        private ImageView icon;

    }

    /**
     * 
     * @author hung
     * 
     */
    private class ViewHolder {
        private TextView title;
        private ImageView icon;

    }

}
