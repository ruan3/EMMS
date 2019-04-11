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
 * @author joyaether
 * 
 */
public class UserInfo {
    // @formatter:off
	public static final String FPAY_TYPE_NAME = "fpaytypename";
	public static final String FCLS_NAME = "fclsname";
	public static final String FEMP_CODE = "fempcode";
	public static final String HOURLY_RATE = "hourlyrate";
	public static final String MINIMUM_WAGE = "minimumwage";
	public static final String FLOCAL_NAME = "flocalname";
	public static final String PRODUCTION_LINE_CODE = "productionlinecode";
	public static final String MONTHLY_RATE = "monthlyrate";
	public static final String FDEPARTMENT_NAME = "fdepartmentname";
	public static final String FCOMPANY_NAME = "fcompanyname";

	// @formatter:on
    DataElement element;
    List<String[]> list = null;
    static final String EMPTY = "";

    public UserInfo(DataElement dataElement, String[] filterList) {
    	JsonObjectElement e = new JsonObjectElement();
    	for (String fieldName : filterList) {
    		e.set(fieldName, dataElement.asObjectElement().get(fieldName));
    	}
        element = e;
    }

    public String getFempCode() {
        return Utility.getStringByKey(element, UserInfo.FEMP_CODE);

    }

    public String getFpayTypeName(Context context) {
        return context.getString(R.string.salary_type) + ": "
                + Utility.getStringByKey(element, UserInfo.FPAY_TYPE_NAME);
    }

    public String getProductionLineCode(Context context) {
        return context.getString(R.string.productionlinecode)
                + ": "
                + Utility
                        .getStringByKey(element, UserInfo.PRODUCTION_LINE_CODE);
    }

    public String getMonthlyRate(Context context) {
        return context.getString(R.string.monthlyrate) + ": "
                + Utility.getStringByKey(element, UserInfo.MONTHLY_RATE);

    }

    public String getHourlyRate(Context context) {
        return context.getString(R.string.hourlyrate)+ ": "
                + Utility.getStringByKey(element, UserInfo.HOURLY_RATE);
    }

    public List<String[]> getData(Context context) {
        if (list != null) {
            return list;
        } else {
            list = new ArrayList<String[]>();
        }

        list.add(new String[] {
        		context.getString(R.string.fempcode),
                Utility.getStringFromNumber(element, UserInfo.FCLS_NAME,
                        UserInfo.EMPTY)

        });
        list.add(new String[] {
                context.getString(R.string.fcompanyname),
                Utility.getStringFromNumber(element, UserInfo.FCOMPANY_NAME,
                        UserInfo.EMPTY)

        });
        list.add(new String[] {
                context.getString(R.string.fdepartmentname),
                Utility.getStringFromNumber(element, UserInfo.FDEPARTMENT_NAME,
                        context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.fempcode),
                Utility.getStringFromNumber(element, UserInfo.FEMP_CODE,
                        UserInfo.EMPTY)

        });
        list.add(new String[] {
                context.getString(R.string.flocalname),
                Utility.getStringFromNumber(element, UserInfo.FLOCAL_NAME,
                        UserInfo.EMPTY)

        });
        list.add(new String[] {
        		context.getString(R.string.salary_type),
                Utility.getStringFromNumber(element, UserInfo.FPAY_TYPE_NAME,
                        UserInfo.EMPTY)

        });
        list.add(new String[] {
        		context.getString(R.string.hourlyrate),
                Utility.getStringFromNumber(element, UserInfo.HOURLY_RATE,
                        UserInfo.EMPTY) });
        list.add(new String[] {
                context.getString(R.string.minimumwage),
                Utility.getStringFromNumber(element, UserInfo.MINIMUM_WAGE,
                        UserInfo.EMPTY)

        });
        list.add(new String[] {
        		context.getString(R.string.monthlyrate),
                Utility.getStringFromNumber(element, UserInfo.MONTHLY_RATE,
                        UserInfo.EMPTY)

        });
        list.add(new String[] {
                context.getString(R.string.productionlinecode),
                Utility.getStringFromNumber(element, UserInfo.MONTHLY_RATE,
                        UserInfo.EMPTY)

        });

        return list;
    }
}
