package com.esquel.epass.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.esquel.epass.R;
import com.esquel.epass.adapter.EditBookMarkAdapter;
import com.esquel.epass.item.ItemBookMark;

/**
 * 
 * @author joyaether
 * 
 */
public class EditBookMarkActivity extends BaseActivity {

    LinearLayout content2;
    RelativeLayout title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bookmark);
        List<ItemBookMark> list = new ArrayList<ItemBookMark>();
        list.add(new ItemBookMark("新闻1", R.drawable.anh1));
        list.add(new ItemBookMark("新闻1", R.drawable.anh2));
        list.add(new ItemBookMark("新闻1", R.drawable.anh3));
        list.add(new ItemBookMark("新闻1", R.drawable.anh1));
        list.add(new ItemBookMark("新闻1", R.drawable.anh2));
        list.add(new ItemBookMark("新闻1", R.drawable.anh3));
        list.add(new ItemBookMark("新闻1", R.drawable.anh1));
        list.add(new ItemBookMark("新闻1", R.drawable.anh2));
        list.add(new ItemBookMark("新闻1", R.drawable.anh3));
        list.add(new ItemBookMark("新闻1", R.drawable.anh1));
        list.add(new ItemBookMark("新闻1", R.drawable.anh2));
        list.add(new ItemBookMark("新闻1", R.drawable.anh3));
        list.add(new ItemBookMark("新闻1", R.drawable.anh1));
        list.add(new ItemBookMark("新闻1", R.drawable.anh2));
        list.add(new ItemBookMark("新闻1", R.drawable.anh3));
        list.add(new ItemBookMark("新闻1", R.drawable.anh1));
        list.add(new ItemBookMark("新闻1", R.drawable.anh2));
        list.add(new ItemBookMark("新闻1", R.drawable.anh3));
        GridView gridview = (GridView) findViewById(R.id.gridview);
        final EditBookMarkAdapter adapter = new EditBookMarkAdapter(this, list);
        gridview.setAdapter(adapter);
        findViewById(R.id.edit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.toggleEditMode();
            }

        });

        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });
    }

    public Dialog onCreateDialogSingleChoice() {

        // Initialize the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Source of the data in the DIalog
        CharSequence[] array = { "1", "2", "3", "4", "5", "6" };

        // Set the dialog title
        builder.setTitle("Select")
                // Specify the list array, the items to be selected by default
                // (null for none),
                // and the listener through which to receive callbacks when
                // items are selected
                .setSingleChoiceItems(array, 1,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method stub

                            }
                        })

                // Set the action buttons
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the result somewhere
                        // or return them to the component that opened the
                        // dialog

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

        return builder.create();
    }
}
