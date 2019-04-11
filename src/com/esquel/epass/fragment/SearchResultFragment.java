package com.esquel.epass.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esquel.epass.R;
import com.esquel.epass.activity.AppApplication;
import com.esquel.epass.activity.ArticleViewActivity;
import com.esquel.epass.activity.UserInfoActivity;
import com.esquel.epass.datastore.EPassSqliteStoreOpenHelper;
import com.esquel.epass.schema.Article;
import com.esquel.epass.utils.Constants;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.schema.Query.Ordering;
import com.joyaether.datastore.widget.DataAdapter;

/**
 * 
 * @author joyaether
 * 
 */
public class SearchResultFragment extends ListFragment {

    private static final int SAMPLE_INTERVAL = 500;
    // private static final int COUNT = 10;
    public static final String KEY_ID = "id";
    public static final String KEY_IS_EMPTY = "empty";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean isEmpty = getArguments().getBoolean(KEY_IS_EMPTY);
        if (isEmpty) {
            resetSearchResult();
            return;
        }

        final String[] ids = getArguments().getStringArray(KEY_ID);

        if (ids == null || ids.length == 0) {
            getListView().setVisibility(View.GONE);
            if (getView() != null && getView().findViewById(R.id.progress_bar) != null) {
                getView().findViewById(R.id.progress_bar).setVisibility(View.GONE);
            }
            return;
        }

        List<String> channelIds = new ArrayList<String>();
        for (int i = 0; i < ids.length; i++) {
            channelIds.add(ids[i]);
        }
        search(channelIds);
    }

    public void resetSearchResult() {
        getView().findViewById(R.id.no_result).setVisibility(View.GONE);
        getListView().setVisibility(View.GONE);
    }

    public void search(final List<String> ids) {
        if (ids == null || ids.size() == 0) {
            getView().findViewById(R.id.no_result).setVisibility(View.VISIBLE);
            getListView().setVisibility(View.GONE);
            return;
        }
        getView().post(new Runnable() {

            @Override
            public void run() {
                getView().findViewById(R.id.no_result).setVisibility(View.GONE);
                getListView().setVisibility(View.GONE);
                getListView()
                        .setAdapter(
                                new ResultAdapter(
                                        ((AppApplication) getActivity()
                                                .getApplication())
                                                .getSqliteStore(),
                                        EPassSqliteStoreOpenHelper.SCHEMA_ARTICLE,
                                        new Query()
                                                .fieldIsIn(
                                                        Article.ID_FIELD_NAME,
                                                        ids)
                                                .resultIsGroupBy(Article.ID_FIELD_NAME)
                                                .resultIsDistinct(true)
                                                .fieldIsOrderedBy(Article.TITLE_FIELD_NAME,
                                                        Ordering.ASCENDING)
                                                .resultIsDistinct(true)));
                getListView().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        getListView().setVisibility(View.VISIBLE);
                    }

                }, SAMPLE_INTERVAL);
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof UserInfoActivity) {
            ((UserInfoActivity) getActivity()).cancelSearch();
        }
    }

    /**
     * 
     * @author joyaether
     * 
     */
    private class ResultAdapter extends DataAdapter {

        public ResultAdapter(Store store, String schema, Query query) {
            super(store, schema, query);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        protected void onDataAvailable(final DataElement data, final View view) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    handleLayout(data, view);
                }

            });
        }

        private void handleLayout(final DataElement data, final View view) {
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            TextView textView = (TextView) view.findViewById(R.id.title);

            DataElement e = data.asObjectElement().get(
                    Article.TITLE_FIELD_NAME);
            String name = "";
            if (e != null && e.isPrimitive()) {             
                name = e.asPrimitiveElement().valueAsString();
                textView.setText(name);
            }

            e = data.asObjectElement().get(Article.IMAGE_FIELD_NAME);
            if (e != null && e.isPrimitive()) {              
                String image = e.asPrimitiveElement()
                       .valueAsString();
                loadImage(image, imageView);

            }

            e = data.asObjectElement().get(Article.ID_FIELD_NAME);
            if (e != null && e.isPrimitive()) {
                final String id = e.asPrimitiveElement().valueAsString();
                    view.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(),
                                    ArticleViewActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("id_article", id);
                            getActivity().startActivity(intent);
                        }

                    });
               
            } else {
                view.setOnClickListener(null);
            }
        }

        @Override
        public View getInflatedView(int position, View convertView,
                ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = View.inflate(getActivity(),
                        R.layout.adapter_catalog_cover, null);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            imageView.setImageBitmap(null);

            TextView textView = (TextView) view.findViewById(R.id.title);
            textView.setText("");
            return view;
        }

		protected void onError(Throwable t) {
			//do nothing
		}

    }

    private void loadImage(final String url,
            final ImageView imageView) {
        if (((AppApplication) (getActivity()).getApplication())
                .getImageDownloader() != null) {
            ((AppApplication) (getActivity()).getApplication())
                    .getImageDownloader().download(url,
                            R.drawable.icon_loading_image,
                            Constants.DEFAULT_IMAGE_WIDTH,
                            Constants.DEFAULT_IMAGE_HEIGHT, imageView);
        }
    }
    /**
     * 
     * @author joyaether
     * 
     */
    // private class SimpleSearchAdapter extends BaseAdapter {
    //
    // @Override
    // public int getCount() {
    // return COUNT;
    // }
    //
    // @Override
    // public Object getItem(int position) {
    // return null;
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // return 0;
    // }
    //
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // View view = convertView;
    // if (view == null) {
    // view = View.inflate(getActivity(), R.layout.search_item, null);
    // }
    //
    // return view;
    // }
    //
    // }
}
