package com.esquel.epass.schema;

import java.util.ArrayList;
import java.util.List;

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
public class WorkingHoursInfo {

    public static final String STD_WORKING_HOURS = "stdworkinghours";
    public static final String OT_HOURS_RESTDAYS = "ot_hours_restdays";
    public static final String OT_HOURS_NORMAL = "ot_hours_normal";
    public static final String NORMAL_WORKING_HOURS = "normalworkinghours";
    public static final String TOTAL_WORKING_HOURS = "total_working_hours";
    public static final String OT_HOURS_STATUTORY_HOLIDAYS = "ot_hours_statutoryholidays";

    DataElement element;
    List<String[]> list = null;

    public WorkingHoursInfo(DataElement dataElement, String[] filterList) {
    	JsonObjectElement e = new JsonObjectElement();
    	for (String fieldName : filterList) {
    		e.set(fieldName, dataElement.asObjectElement().get(fieldName));
    	}
        element = e;
    }

    public String getTotalWorkingHours(Context context) {
        return context.getString(R.string.total_working_hours)
                + ": "
                + Utility.getStringByKey(element,
                        WorkingHoursInfo.TOTAL_WORKING_HOURS);
    }

    public List<String[]> getData(Context context) {
        if (list != null) {
            return list;
        } else {
            list = new ArrayList<String[]>();
        }
        list.add(new String[] {
                context.getString(R.string.stdworkinghours),
                Utility.getStringFromNumber(element,
                        WorkingHoursInfo.STD_WORKING_HOURS,  context.getString(R.string.hour))

        });

        list.add(new String[] {
                context.getString(R.string.normalworkinghours),
                Utility.getStringFromNumber(element,
                        WorkingHoursInfo.NORMAL_WORKING_HOURS, context.getString(R.string.hour))

        });
        list.add(new String[] {
                context.getString(R.string.normal_ot),
                Utility.getStringFromNumber(element,
                        WorkingHoursInfo.OT_HOURS_NORMAL, context.getString(R.string.hour))

        });
        list.add(new String[] {
                context.getString(R.string.ot_hours_restdays),
                Utility.getStringFromNumber(element,
                        WorkingHoursInfo.OT_HOURS_RESTDAYS, context.getString(R.string.hour)) });
        list.add(new String[] {
                context.getString(R.string.ot_hours_statutoryholidays),
                Utility.getStringFromNumber(element,
                        WorkingHoursInfo.OT_HOURS_STATUTORY_HOLIDAYS,
                        context.getString(R.string.hour))

        });

        list.add(new String[] {
        		context.getString(R.string.total_working_hours),
                Utility.getStringFromNumber(element,
                        WorkingHoursInfo.TOTAL_WORKING_HOURS, context.getString(R.string.hour))

        });

        return list;
    }

}
