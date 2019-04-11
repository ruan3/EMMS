package com.esquel.epass.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esquel.epass.R;
import com.esquel.epass.lib.flipview.Log;
import com.esquel.epass.schema.DeductionInfo;
import com.esquel.epass.schema.IncomeInfo;
import com.esquel.epass.schema.UserInfo;
import com.esquel.epass.ui.LoadingDialog;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.rest.JsonObjectElement;

/**
 * 
 * @author joyaether
 * 
 */
public class ContentActivity extends BaseGestureActivity {
    private static final int COMPRESS_QUALITY = 100;
    LinearLayout content;
    RelativeLayout title;
    public static final String KEY_CONTENT_OBJECT_JSON = "content";
    private static final String PAYSLIP_COPY_PATH = "Esquel/SalaryHistory/";
    private String userId;
    private String paySlipDate;
    private LoadingDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        content = (LinearLayout) findViewById(R.id.llayout_content);
        content.setDrawingCacheEnabled(true);
        title = (RelativeLayout) findViewById(R.id.rlayout_content_title);

        // include title bar
        View child = getLayoutInflater().inflate(R.layout.titlebar, null);
        ImageView img = (ImageView) child.findViewById(R.id.ibtn_title_center);
        img.setVisibility(View.INVISIBLE);
        title.addView(child);
        TextView saveButton = (TextView) child.findViewById(R.id.tv_tile_right);
        saveButton.setText(R.string.save);
        dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        child.findViewById(R.id.ibtn_title_left).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });

        child.findViewById(R.id.tv_tile_right).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show();
                final Bitmap bitmap = loadBitmapFromView(content);
                if (bitmap == null) {
                    dialog.dismiss();
                    return;
                }
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(Environment.getExternalStorageDirectory(), PAYSLIP_COPY_PATH + getUserId().replace("\"", "") + "_" + getPaySlipDate() + ".jpg");
                        savePaySlip(file, bitmap);

                    }

                };
                ExecutorService executor = Executors.newCachedThreadPool();
                executor.submit(runnable);
            }

        });

        String jsonContent = getIntent().getStringExtra(KEY_CONTENT_OBJECT_JSON);
        if (jsonContent == null) {
            return;
        }

        JsonObjectElement object = new JsonObjectElement(jsonContent);
        setLayout(object);
    }

    private void setLayout(DataElement element) {
        if (element == null) {
            return;
        }
        // add user segment title
        View userSegment = getLayoutInflater().inflate(R.layout.item_title, null);
        content.addView(userSegment);
        // add segment with user data
        addSegment(element.asObjectElement().get("user_info").asObjectElement(), getResources().getStringArray(R.array.user_field));

        // add epay slip segment
        addEPaySlipSegment(element.asObjectElement());

        // add working hour segment title
        View workingHoursSegment = getLayoutInflater().inflate(R.layout.item_title, null);
        TextView titleView = (TextView) workingHoursSegment.findViewById(R.id.tvtt2);
        titleView.setText(R.string.epay_slip_working_hour);
        content.addView(workingHoursSegment);
        // add segment with working hour data
        addSegment(element.asObjectElement().get("working_hours_info").asObjectElement(), getResources().getStringArray(R.array.working_hours_field));

        // add income segment title
        View incomeSegment = getLayoutInflater().inflate(R.layout.item_title, null);
        TextView titleView2 = (TextView) incomeSegment.findViewById(R.id.tvtt2);
        titleView2.setText(R.string.epay_slip_income);
        content.addView(incomeSegment);
        // add segment with income data
        addSegment(element.asObjectElement().get("income_info").asObjectElement(), getResources().getStringArray(R.array.income_field));
        // add segment with allowance
        addSegment(element.asObjectElement().get("benefit_info").asObjectElement(), getResources().getStringArray(R.array.benefit));
        addField(element.asObjectElement().get("income_info").asObjectElement().get("grosspay").asPrimitiveElement().valueAsString(), getItemName("grosspay"));

        // add deduction segment title
        View deductionSegment = getLayoutInflater().inflate(R.layout.item_title, null);
        TextView titleView3 = (TextView) deductionSegment.findViewById(R.id.tvtt2);
        titleView3.setText(R.string.epay_slip_deduction);
        content.addView(deductionSegment);
        // add segment with deduction data
        addSegment(element.asObjectElement().get("deduction_info").asObjectElement(), getResources().getStringArray(R.array.deduction_field));
        float totalDeduction = getTotalDeduction(element.asObjectElement().get("deduction_info").asObjectElement());
        addField(getValueInNumber(totalDeduction) + "", getItemName(DeductionInfo.TOTAL_DEDUCTION));
        // add print date
        addPrintTime();

    }

    private float getTotalDeduction(DataElement element) {
        float total = 0;
        ObjectElement object = element.asObjectElement();
        for (String key : object.allKeys()) {
            if (!key.equals(DeductionInfo.TOTAL_DEDUCTION)) {
                DataElement e = object.get(key);
                if (e != null && e.isPrimitive() && e.asPrimitiveElement().isNumber()) {
                    total = total + e.asPrimitiveElement().valueAsFloat();
                }
            }

        }
        return total;
    }

    /**
	 * 
	 */
    private void addPrintTime() {
        View segment = getLayoutInflater().inflate(R.layout.item_title, null);
        segment.findViewById(R.id.tvtt2).setVisibility(View.GONE);
        content.addView(segment);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        String printTime = getString(R.string.print_time);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeZone(TimeZone.getTimeZone(((AppApplication) getApplication()).getDefaultTimeZone()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(((AppApplication) getApplication()).getDefaultTimeZone()));
        String time = formatter.format(calendar.getTime());
        printTime = printTime + " " + time;
        textView.setText(printTime);
        content.addView(textView);
        View v = getLayoutInflater().inflate(R.layout.item_line, null);
        content.addView(v);
    }

    private void addEPaySlipSegment(ObjectElement element) {
        // add pay slip segment
        View segment = getLayoutInflater().inflate(R.layout.item_title, null);
        TextView titleView = (TextView) segment.findViewById(R.id.tvtt2);
        titleView.setVisibility(View.GONE);
        content.addView(segment);
        // get pay slip date
        DataElement e = element.asObjectElement().get("fyear");
        String dateString = "";
        if (e != null && e.isPrimitive()) {
            dateString = e.asPrimitiveElement().valueAsInt() + getString(R.string.year);
        }

        e = element.asObjectElement().get("fperiod");
        if (e != null && e.isPrimitive()) {
            dateString = dateString + e.asPrimitiveElement().valueAsInt() + getString(R.string.month);
        }

        if (dateString.length() > 0) {
            String titleString = getString(R.string.year) + getString(R.string.month);
            addField(dateString, titleString);
            setPaySlipDate(dateString);
        }

        e = element.asObjectElement().get("fputno");
        String itemName = getItemName("fputno");
        if (e != null && e.isPrimitive() && itemName != null) {
            addField(e.asPrimitiveElement().toString(), itemName);
        }

        e = element.asObjectElement().get("netpay");
        String itemName2 = getItemName("netpay");
        if (e != null && e.isPrimitive() && itemName2 != null) {
            addField(e.asPrimitiveElement().toString() + " " + getString(R.string.dollar), itemName2);
        }

    }

    private void addSegment(ObjectElement element, String[] sequenceList) {
        for (String key : sequenceList) {
            DataElement e = element.get(key);
            String itemName = getItemName(key);
            if (e != null && e.isPrimitive() && itemName != null) {
                if (!key.equals(DeductionInfo.TOTAL_DEDUCTION) && !key.equals(IncomeInfo.GROSS_PAY)) {
                    String result = e.asPrimitiveElement().toString();
                    if (key.equals(UserInfo.MINIMUM_WAGE)) {
                        result = result + " " + getString(R.string.dollar);
                    }
                    addField(result, itemName);
                }

                if (key.equals("fempcode")) {
                    setUserId(e.asPrimitiveElement().toString());
                }

            }
        }
    }

    /**
     * @param e
     * @param titleName
     */
    private void addField(String valueString, String titleName) {
        View child = getLayoutInflater().inflate(R.layout.item_content, null);
        TextView titleView = (TextView) child.findViewById(R.id.tv1);
        titleView.setText(titleName);
        TextView value = (TextView) child.findViewById(R.id.tv2);
        if (valueString.contains("\"")) {
            valueString = valueString.replaceAll("\"", "");
        }
        value.setText(valueString);
        content.addView(child);
        View v = getLayoutInflater().inflate(R.layout.item_line, null);
        content.addView(v);
    }

    /**
     * Use the fieldName to get back the item name to show on ui.
     * 
     * @param fieldName
     * @return
     */
    private String getItemName(String fieldName) {
        Resources res = getResources();
        int resourceId = res.getIdentifier(fieldName, "string", "com.esquel.epass");
        if (resourceId == 0) {
            return null;
        }
        return getString(resourceId);
    }

    private void savePaySlip(final File destination, Bitmap bitmap) {
        if (!destination.exists()) {
            destination.getParentFile().mkdirs();
        }
        if (bitmap != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, COMPRESS_QUALITY, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(destination);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
                bitmap.recycle();
            } catch (Exception e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dialog.dismiss();
                    }

                });
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(ContentActivity.this, getString(R.string.save_epay_slip) + destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    dialog.dismiss();
                }

            });
        }
    }

    public Bitmap loadBitmapFromView(View view) {
        long t = System.currentTimeMillis();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        Log.log("tttt==" + (System.currentTimeMillis() - t));
        return returnedBitmap;

    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the paySlipDate
     */
    public String getPaySlipDate() {
        return paySlipDate;
    }

    /**
     * @param paySlipDate
     *            the paySlipDate to set
     */
    public void setPaySlipDate(String paySlipDate) {
        this.paySlipDate = paySlipDate;
    }

    private float getValueInNumber(float value) {
        DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
        String formate = df.format(value);

        return Float.parseFloat(formate);
    }

}
