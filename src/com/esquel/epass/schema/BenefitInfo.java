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
public class BenefitInfo {
    public static final String PERFORMANCE_INCENTIVE = "performanceincentive";
    public static final String YEARS_OF_SERVICE_AWARD = "yearsofserviceaward";
    public static final String ADJ_FOR_LAST_MONTH = "adjforlastmonth";
    public static final String LEAVE_PAY = "leave_pay";
    public static final String ALLOWANCE_TAXABLE = "allowance_taxable";
    public static final String OTHER_BONUS = "otherbonus";

    DataElement element;
    List<String[]> list = null;

    public BenefitInfo(DataElement dataElement, String[] filterList) {
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
                context.getString(R.string.leave_pay),
                Utility.getStringFromNumber(element, BenefitInfo.LEAVE_PAY,
                		 context.getString(R.string.dollar))

        });

        list.add(new String[] {
                context.getString(R.string.performanceincentive),
                Utility.getStringFromNumber(element, BenefitInfo.PERFORMANCE_INCENTIVE,
                		 context.getString(R.string.dollar))
                });
        list.add(new String[] {
                context.getString(R.string.otherbonus),
                
                Utility.getStringFromNumber(element, BenefitInfo.OTHER_BONUS,
                		 context.getString(R.string.dollar))
             });
        list.add(new String[] {
                context.getString(R.string.allowance_taxable),
                
                Utility.getStringFromNumber(element, BenefitInfo.ALLOWANCE_TAXABLE,
                		 context.getString(R.string.dollar))
                        
                });
        list.add(new String[] {
                context.getString(R.string.adjforlastmonth),
                Utility.getStringFromNumber(element, BenefitInfo.ADJ_FOR_LAST_MONTH,
                		 context.getString(R.string.dollar))
               });
        list.add(new String[] {
                context.getString(R.string.yearsofserviceaward),
                Utility.getStringFromNumber(element,
                        BenefitInfo.YEARS_OF_SERVICE_AWARD,  context.getString(R.string.dollar))
                        
                 });

        return list;
    }

}
