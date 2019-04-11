package com.esquel.epass.schema;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;

import com.esquel.epass.R;
import com.esquel.epass.utils.Utility;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.rest.JsonObjectElement;

/**
 * 
 * @author hung
 * 
 */
public class IncomeInfo {

    public static final String OT_PAY_RESTDAYS = "ot_pay_restdays";
    public static final String OT_PAY_NORMAL = "ot_pay_normal";
    public static final String BASIC_PAY = "basicpay";
    public static final String GROSS_PAY = "grosspay";
    public static final String OT_PAY_STATUTORY_HOLIDAYS = "ot_pay_statutoryholidays";

    DataElement element;
    List<String[]> list = null;

    public IncomeInfo(DataElement dataElement, String[] filterList) {
        JsonObjectElement e = new JsonObjectElement();
        for (String fieldName : filterList) {
            e.set(fieldName, dataElement.asObjectElement().get(fieldName));
        }
        element = e;
    }

    public String getGrossPay(Context context) {

        String grossPay = "0";
        try {
            grossPay = element.asObjectElement().get(IncomeInfo.GROSS_PAY)
                    .valueAsString();
        } catch (Exception e) {
            grossPay = "0";
        }
        return context.getString(R.string.total_salary_pay) + ": " + grossPay;
    }

    public String getTotal(Context context) {
        return context.getString(R.string.sum_salary) + ": " + getTotalIncomeInNumber(element);
    }

    private float getTotalIncomeInNumber(DataElement dataElement) {
        float totalIncome = 0;
        for (String key : dataElement.asObjectElement().allKeys()) {
            if (key.equals(IncomeInfo.GROSS_PAY)) {
                break;
            }
            DataElement e = dataElement.asObjectElement().get(key);
            if (e != null && e.isPrimitive()
                    && e.asPrimitiveElement().isNumber()) {
                totalIncome = totalIncome
                        + e.asPrimitiveElement().valueAsFloat();
            }
        }
        DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(
                Locale.US));
        String formate = df.format(totalIncome);
        totalIncome = Float.parseFloat(formate);
        return totalIncome;
    }

    public List<String[]> getData(Context context) {
        if (list != null) {
            return list;
        } else {
            list = new ArrayList<String[]>();
        }
        list.add(new String[] {
                context.getString(R.string.ot_pay_normal),
                Utility.getStringFromNumber(element, IncomeInfo.BASIC_PAY,
                		 context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.normal_working_hour) + "(150%)",

                Utility.getStringFromNumber(element, IncomeInfo.OT_PAY_NORMAL,
                		 context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.ot_hours_restdays) + "(200%)",

                Utility.getStringFromNumber(element,
                        IncomeInfo.OT_PAY_RESTDAYS,  context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.ot_hours_statutoryholidays) + "(300%)",

                Utility.getStringFromNumber(element,
                        IncomeInfo.OT_PAY_STATUTORY_HOLIDAYS,  context.getString(R.string.dollar))

        });
        list.add(new String[] { context.getString(R.string.sum_salary),
                getTotalIncomeInNumber(element) +  context.getString(R.string.dollar) });
        return list;
    }

}
