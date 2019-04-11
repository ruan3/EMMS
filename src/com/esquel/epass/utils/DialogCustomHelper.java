package com.esquel.epass.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.esquel.epass.R;
import com.esquel.epass.adapter.DialogChoiceListAdapter;

public class DialogCustomHelper {
	public interface DialogListChoiceListener {
		void onCancel();

		void onSelect(int pos, String value);
	}

	public static Dialog createDialogListChoice(Activity activity,
			final String[] list, final DialogListChoiceListener listener) {
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_list_choice_);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		Button btnCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);
		ListView listView = (ListView) dialog
				.findViewById(R.id.lvDialogChoices);

		/**
		 * To add header use below code.
		 */
		// LayoutInflater inflater = activity.getLayoutInflater();
		// ViewGroup header = (ViewGroup)
		// inflater.inflate(R.layout.header_pending_order_sort, listView,
		// false);
		// listView.addHeaderView(header, null, false);
		
		DialogChoiceListAdapter adapter = new DialogChoiceListAdapter(activity,
				list);
		listView.setAdapter(adapter);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onCancel();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listener != null) {
					listener.onSelect(position - 1, list[position - 1]);
				}
			}
		});
		return dialog;
	}

	public static Dialog createDialogChoice(Activity activity,
			final String[] list, final DialogListChoiceListener listener) {
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_list_choice_);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		Button btnCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);
		ListView listView = (ListView) dialog
				.findViewById(R.id.lvDialogChoices);

		LayoutInflater inflater = activity.getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(
				R.layout.header_list_choice, listView, false);
		listView.addHeaderView(header, null, false);

		DialogChoiceListAdapter adapter = new DialogChoiceListAdapter(activity,
				list);
		listView.setAdapter(adapter);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onCancel();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listener != null) {
					listener.onSelect(position - 1, list[position - 1]);
				}
			}
		});
		return dialog;
	}
}
