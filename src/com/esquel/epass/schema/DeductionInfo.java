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
import com.joyaether.datastore.ObjectElement;
import com.joyaether.datastore.rest.JsonObjectElement;

/**
 * 
 * @author hung
 * 
 */
public class DeductionInfo {
    public static final String OTHER_DEDUCTIONS_TAXABLE = "otherdeductions_taxable";
    public static final String INCOME_TAX_WITH_ID = "incometaxwithheld";
    public static final String COMMERCIAL_INSURANCE_DEDUCTION = "commercialinsurancededuction";
    public static final String SOCIAL_INSURANCE = "social_insurance";
    public static final String ADMIN_DEDUCTION = "admindeduction";
    public static final String OTHER_DEDUCTION_NONTAXABLE = "otherdeductions_nontaxable";
    public static final String TOTAL_DEDUCTION = "total_deduction";
    public static final String HOUSING_PROVIDENT_FUND = "housingprovidentfund";

    DataElement element;
    List<String[]> list = null;

    public DeductionInfo(DataElement dataElement, String[] filterList) {
        JsonObjectElement e = new JsonObjectElement();
        for (String fieldName : filterList) {
            e.set(fieldName, dataElement.asObjectElement().get(fieldName));
        }
        element = e;
    }

    public List<String[]> getData(Context context) {
        if (list != null) {
            return list;
        } else {
            list = new ArrayList<String[]>();
        }
        list.add(new String[] {
                context.getString(R.string.social_insurance),
                Utility.getStringFromNumber(element,
                        DeductionInfo.SOCIAL_INSURANCE,  context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.housingprovidentfund),
                Utility.getStringFromNumber(element,
                        DeductionInfo.HOUSING_PROVIDENT_FUND,  context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.commercialinsurancededuction),
                Utility.getStringFromNumber(element,
                        DeductionInfo.COMMERCIAL_INSURANCE_DEDUCTION,
                        context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.admindeduction),
                Utility.getStringFromNumber(element,
                        DeductionInfo.ADMIN_DEDUCTION,  context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.otherdeductions_nontaxable),
                Utility.getStringFromNumber(element,
                        DeductionInfo.OTHER_DEDUCTION_NONTAXABLE,
                        context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.otherdeductions_taxable),
                Utility.getStringFromNumber(element,
                        DeductionInfo.OTHER_DEDUCTIONS_TAXABLE,  context.getString(R.string.dollar))

        });
        list.add(new String[] {
                context.getString(R.string.incometaxwithheld),

                Utility.getStringFromNumber(element,
                        DeductionInfo.INCOME_TAX_WITH_ID,  context.getString(R.string.dollar))

        });
        list.add(new String[] { context.getString(R.string.total_deduction),
                getTotalDeductionInNumber() +  context.getString(R.string.dollar)

        });

        return list;
    }

    public String getTotalDeduction(Context context) {
        float total = getTotalDeductionInNumber();

        return context.getString(R.string.sum_deduction) + ": " + total;
    }

    public float getTotalDeductionInNumber() {
        float total = 0;
        ObjectElement object = element.asObjectElement();
        for (String key : object.allKeys()) {
            if (!key.equals(TOTAL_DEDUCTION)) {
                DataElement e = object.get(key);
                if (e != null && e.isPrimitive()
                        && e.asPrimitiveElement().isNumber()) {
                    total = total + e.asPrimitiveElement().valueAsFloat();
                }
            }

        }

        DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(
                Locale.US));
        String formate = df.format(total);

        total = Float.parseFloat(formate);

        return total;
    }
}
