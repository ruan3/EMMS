package com.esquel.epass.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.CatalogCoverActivity;
import com.esquel.epass.schema.Category;
import com.esquel.epass.schema.CategoryChannel;
import com.joyaether.datastore.DataElement;

/**
 * 
 * @author joyaether
 * 
 */
public class MenuAdapter extends BaseAdapter {

    private final LayoutInflater vi;

    ViewHolderName holderName;
    Context mContext;
    DataElement dataElements;

    public MenuAdapter(Context context, DataElement dataElements) {
        this.mContext = context;
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataElements = dataElements;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View convertView = view;
        if (convertView == null || !convertView.getTag().equals(holderName)) {
            convertView = vi.inflate(R.layout.adapter_menu, null);
            holderName = new ViewHolderName();
            holderName.setName((TextView) convertView.findViewById(R.id.title));
            holderName.setNumber((TextView) convertView
                    .findViewById(R.id.number));
            holderName.setCover((RelativeLayout) convertView
                    .findViewById(R.id.cover));
            convertView.setTag(holderName);
        } else {
            holderName = (ViewHolderName) convertView.getTag();
        }

        if (position == 0) {
            holderName.getNumber().setVisibility(View.GONE);
            holderName.getName().setText(mContext.getString(R.string.more));
            holderName.getName().setTextColor(Color.GRAY);
            holderName.getCover().setEnabled(false);
        } else {
            holderName.getNumber().setVisibility(View.VISIBLE);
            final DataElement dataElement = dataElements.asArrayElement().get(
                    position - 1);
            DataElement nameElement = dataElement.asObjectElement().get(
                    Category.NAME_FIELD_NAME);

            if (nameElement != null && nameElement.isPrimitive()) {
                holderName.getName().setText(
                        nameElement.asPrimitiveElement().valueAsString());
            }
            holderName.getName().setTextColor(Color.BLACK);

            DataElement channelElements = dataElement.asObjectElement().get(
                    Category.CATEGORY_CHANNEL_FIELD_NAME);
            int channelNumber = 0;
            if (channelElements != null && channelElements.isArray()) {
            	channelNumber = channelElements.asArrayElement().size();
                
            }
            holderName.getNumber().setText("" + channelNumber);
            if (channelNumber > 0) {
                holderName.getCover().setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataElement categoryChannels = dataElement
                                .asObjectElement().get(
                                        Category.CATEGORY_CHANNEL_FIELD_NAME);
                        long[] list = new long[categoryChannels.asArrayElement()
                                .size()];
                        for (int i = 0; i < categoryChannels.asArrayElement()
                                .size(); i++) {
                            DataElement d = categoryChannels.asArrayElement()
                                    .get(i);
                            DataElement c = d.asObjectElement().get(
                                    CategoryChannel.CHANNEL_FIELD_NAME);
                            DataElement channelId = c.asObjectElement().get(
                                    Category.ID_FIELD_NAME);
                            list[i] = channelId.asPrimitiveElement().valueAsLong();
                        }

                        Intent intent = new Intent(mContext,
                                CatalogCoverActivity.class);
                        DataElement nameElement = dataElement.asObjectElement().get(
                                Category.NAME_FIELD_NAME);
                        if (nameElement != null) {
                            intent.putExtra(CatalogCoverActivity.KEY_TITLE, nameElement
                            		.asPrimitiveElement().valueAsString());
                        }
                        DataElement idElement = dataElement.asObjectElement().get(
                                Category.ID_FIELD_NAME);
                        if (idElement != null) {
                        	 intent.putExtra(CatalogCoverActivity.KEY_CATEGORY_ID, idElement
                        			 .asPrimitiveElement().valueAsLong());
                        }
                        intent.putExtra("list", list);
                        mContext.startActivity(intent);
                    }

                });
            } else {
            	holderName.getCover().setOnClickListener(null);
            }


        }

        return convertView;
    }

    /**
     * 
     * @author joyaether
     * 
     */
    public class ViewHolderName {
        private TextView name;
        private TextView number;
        private RelativeLayout cover;

        /**
         * @return the name
         */
        public TextView getName() {
            return name;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(TextView name) {
            this.name = name;
        }

        public TextView getNumber() {
            return number;
        }

        public void setNumber(TextView number) {
            this.number = number;
        }

        public RelativeLayout getCover() {
            return cover;
        }

        public void setCover(RelativeLayout cover) {
            this.cover = cover;
        }
    }

    @Override
    public int getCount() {
        return dataElements.asArrayElement().size() + 1;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }
}
