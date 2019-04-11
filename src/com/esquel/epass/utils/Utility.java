package com.esquel.epass.utils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.esquel.epass.activity.HomeActivity;
import com.esquel.epass.item.DisplayItem;
import com.esquel.epass.schema.ApplicationVersion;
import com.esquel.epass.schema.Article;
import com.esquel.epass.schema.Channel;
import com.google.gson.Gson;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;

/**
 * 
 * @author joyaether
 * 
 */
public final class Utility {

    private Utility() {

    }

    public String readAssetFile(Context context, String inFile) {
        String tContents = "";
        try {
            InputStream stream = context.getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (Exception e) {
        }

        return tContents;

    }

    public static int getTypeShow(Context context) {
        final String add = "TYPE_SHOW";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = pref.getString(HomeActivity.KEY_USER_ID, null);
        int value = pref.getInt(add + userId, 1);
        if (value == 0) {
            value = 1;
        }
        return value;
    }

    public static void setTypeShow(Context context, int value) {
        final String add = "TYPE_SHOW";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = pref.getString(HomeActivity.KEY_USER_ID, null);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(add + userId, value);

        editor.commit();

    }

    public static Date stringToDate(String dateTimeStr) {

        try {

            Date date = new SimpleDateFormat("E MMM d HH:mm:ss z yyyy", Locale.US).parse(dateTimeStr);
            return date;
        } catch (Exception e) {
        }
        return new Date();
    }

    public static String getStringByKey(DataElement e, String key) {
        try {
            DataElement fiels = e.asObjectElement().get(key);
            return fiels.asPrimitiveElement().valueAsString();
        } catch (Exception error) {
        }
        return "0";
    }

    public static boolean getBooleanByKey(DataElement e, String key) {
        try {
            DataElement fiels = e.asObjectElement().get(key);
            return fiels.asPrimitiveElement().valueAsBoolean();
        } catch (Exception error) {
        }
        return false;
    }

    public static String getStringFromNumber(DataElement e, String key, String extension) {
        if (e == null) {
            return null;
        }
        DataElement element = e.asObjectElement().get(key);
        if (element != null && element.isPrimitive()) {
            return element.asPrimitiveElement().valueAsString() + extension;
        }
        return null;

    }

    public static long getStringTimeKey(DataElement e, String key) {
        try {
            DataElement fiels = e.asObjectElement().get(key);
            return stringToDate(fiels.asPrimitiveElement().valueAsString()).getTime();
        } catch (Exception error) {
        }
        return System.currentTimeMillis();
    }

    public static long getLongByKey(DataElement e, String key) {
        try {
            DataElement fiels = e.asObjectElement().get(key);
            return fiels.asPrimitiveElement().valueAsLong();

        } catch (Exception error) {

        }
        return -1;

    }

    public static Channel convertDataElementToDisplayChannel(DataElement object) {
        String[] fields = new String[] { Channel.ID_FIELD_NAME, Channel.NAME_FIELD_NAME };
        Channel channel = new Channel();
        for (int i = 0; i < fields.length; i++) {
            DataElement e = object.asObjectElement().get(fields[i]);
            if (e != null && e.isPrimitive()) {
                if (fields[i].equals(Channel.ID_FIELD_NAME)) {
                    long id = e.asPrimitiveElement().valueAsLong();
                    channel.setId(id);
                } else if (fields[i].equals(Channel.NAME_FIELD_NAME)) {
                    String name = e.asPrimitiveElement().valueAsString();
                    channel.setName(name);
                }
            }
        }
        return channel;

    }

    /**
     * 
     * @param list1
     * @param list2
     * @return true if is silimar
     */
    public static boolean isEqualChannelList(DisplayItem[] list1, DisplayItem[] list2) {
        if (list1.length != list2.length) {
            return false;
        }
        int size = list1.length;
        for (int i = 0; i < size; i++) {
            // @formatter:off
			boolean silimar = !list1[i].getArticleName().equals(
					list2[i].getArticleName())
					|| list1[i].getChannelId() != list2[i].getChannelId()
					|| !list1[i].getChannelName().equals(
							list2[i].getChannelName());
			// @formatter:on
            if (silimar) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareItemJsonArticleList(List<Article> list1, List<Article> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        int size = list1.size();
        for (int i = 0; i < size; i++) {
            if (!list1.get(i).getId().equals(list2.get(i).getId())) {
                return false;
            }
        }
        return true;
    }

    // public static List<Article> stringToArticles(String str) {
    // Type listType = new TypeToken<ArrayList<Article>>() {
    // }.getType();
    // Gson gson = new Gson();
    // return gson.fromJson(str, listType);
    // }

    // public static List<Channel> stringToChannels(String str) {
    // Type listType = new TypeToken<ArrayList<Channel>>() {
    // }.getType();
    // Gson gson = new Gson();
    // return gson.fromJson(str, listType);
    // }

    public static void showAllProgressBar(final Context context, ViewGroup mContainer) {

        if (mContainer == null) {
            return;
        }

        final int mCount = mContainer.getChildCount();

        for (int i = 0; i < mCount; ++i) {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof ProgressBar) {
                Object tag = mChild.getTag();
                String compareTag = "view_refresh_download";
                if (tag != null && tag.toString().equals(compareTag)) {
                    mChild.setVisibility(View.VISIBLE);
                }
            } else if (mChild instanceof ViewGroup) {

                showAllProgressBar(context, (ViewGroup) mChild);
            }
        }
    }

    public static boolean isJSONValid(String jsonString) {
        try {
            Gson gson = new Gson();
            gson.fromJson(jsonString, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    public static ArrayList<TextView> getTextViewsByTag(ViewGroup root, String tag) {
        ArrayList<TextView> views = new ArrayList<TextView>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getTextViewsByTag((ViewGroup) child, tag));
            }
            final Object tagObj = child.getTag();
            if (child instanceof TextView && tagObj != null && tagObj.equals(tag)) {
                views.add((TextView) child);
            }
        }
        return views;
    }

    public static ArrayList<ImageView> getImageViewsByTag(ViewGroup root, String tag) {
        ArrayList<ImageView> views = new ArrayList<ImageView>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getImageViewsByTag((ViewGroup) child, tag));
            }
            final Object tagObj = child.getTag();
            if (child instanceof ImageView && tagObj != null && tagObj.equals(tag)) {
                views.add((ImageView) child);
            }
        }
        return views;
    }

    public static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }
            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }

    public static void hideAllProgressBar(Context context, ViewGroup mContainer) {

        if (mContainer == null) {
            return;
        }

        final int mCount = mContainer.getChildCount();

        for (int i = 0; i < mCount; ++i) {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof ProgressBar) {
                if (mChild.getTag() != null && mChild.getTag().toString().equals("view_refresh_download")) {
                    mChild.setVisibility(View.GONE);
                }
            } else if (mChild instanceof ViewGroup) {

                hideAllProgressBar(context, (ViewGroup) mChild);
            }
        }
    }
    
    public static boolean isPackageInstalled(Context context, String packagename) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
    
    public static boolean isNeedUpdate(Context context, ObjectElement element) {
    	int versionCode = 0;
		String packageName = "";
		
		DataElement e = null;
		if (element != null) {			
			DataElement versionCodeElement = element.get(ApplicationVersion.BUILD_NUMBER_FIELD_NAME);
			if (versionCodeElement != null && versionCodeElement.isPrimitive()) {
				versionCode = versionCodeElement.asPrimitiveElement().valueAsInt();
			}
		}
		e = element.get(ApplicationVersion.APPLICATION_FIELD_NAME + "/" + "identifier");
		if (e != null && e.isPrimitive()) {
			packageName = e.asPrimitiveElement().valueAsString();
		}
		int currentVersion = PackageUtils.getPackageVersionCode(context, packageName);
		return currentVersion < versionCode;
    }
}
