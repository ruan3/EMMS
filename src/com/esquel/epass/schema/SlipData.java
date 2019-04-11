package com.esquel.epass.schema;

import java.text.NumberFormat;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;

import com.esquel.epass.R;
import com.esquel.epass.utils.Utility;
import com.joyaether.datastore.DataElement;

/**
 * 
 * @author hung
 * 
 */
public class SlipData {

    public static final String NET_PAY = "netpay";
    public static final String KEY_USER = "user";
    public static final String KEY_WORKING_HOUR = "working_hour";
    public static final String KEY_BENEFIT = "benefit";
    public static final String KEY_INCOME = "income";
    public static final String KEY_DEDUCTION = "deduction";

    // // 次
    // private Long fputno;
    // // 停待补贴
    // private Float g_factoryleavepay;
    // // 组织全名
    // private String forgfullname;
    // // 假补工资
    // private Float examleavepay;
    // @MyAnnotation(name = "实发工资")
    // private Float netpay;
    // // 假补工资
    // private Float marriageleavepay;
    // // aaa
    // private Float paternityleavepay;
    // // 假补工资
    // private Float injuryleavepay;
    // // 年假天数
    // private Float annualleavehours;
    // // 社保
    // private Float unemploymentinsurance;
    // // 社保
    // private Float medicalinsurance;
    // // 假补工资
    // private Float extraannualleavepay;
    // private String fsocialinsuranceaccount;
    // // 期
    // private Long fperiod;
    //
    // // 假补工资
    // private Float shortworkingpay;
    // // 假补工资
    // private Float sickleavepay;
    // // 假补工资
    // private Float annualleavepay;
    // private Float statutoryholidayhours;
    // // 假补工资
    // private Float unspentannualleavepay;
    // // 假补工资
    // private Float compassionateleavepay;
    // // 假补工资
    // private Float otherleavepay;
    // private Float statutoryholidaypay;
    // // 假补工资
    // private Float nopayleavepay;
    // // 工资级别ID
    // private Long fclsid;
    // private Float jcc_ee_deduction;
    // private Float webread;
    // // 假补工资
    // private Float leavededuction;
    // // 假补工资
    // private Float compensationleavepay;
    // // 职位
    // private String fpositionname;
    // // 全勤奖
    // private Float attendancebonus;
    // // 中夜津贴
    // private Float g_mealallowance;
    // // 社保
    // private Float socialinsurance;
    // // 身份证号码
    // private Long fidcardid;
    // // 假补工资
    // private Float juryserviceleavepay;
    // // 假补工资
    // private Float maternityleavepay;
    // // 含国定假
    // private Float g_statutoryholidayspay;
    // // 年
    // private Long fyear;

    private UserInfo userInfo;
    private WorkingHoursInfo workingHoursInfo;
    private DeductionInfo deductionInfo;
    private BenefitInfo benefitInfo;
    private IncomeInfo incomeInfo;
    private DataElement dataElement;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public WorkingHoursInfo getWorkingHoursInfo() {
        return workingHoursInfo;
    }

    public void setWorkingHoursInfo(WorkingHoursInfo workingHoursInfo) {
        this.workingHoursInfo = workingHoursInfo;
    }

    public DeductionInfo getDeductionInfo() {
        return deductionInfo;
    }

    public void setDeductionInfo(DeductionInfo deductionInfo) {
        this.deductionInfo = deductionInfo;
    }

    public BenefitInfo getBenefitInfo() {
        return benefitInfo;
    }

    public void setBenefitInfo(BenefitInfo benefitInfo) {
        this.benefitInfo = benefitInfo;
    }

    public IncomeInfo getIncomeInfo() {
        return incomeInfo;
    }

    public void setIncomeInfo(IncomeInfo incomeInfo) {
        this.incomeInfo = incomeInfo;
    }

    public DataElement getDataElement() {
        return dataElement;
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElement = dataElement;
    }

    public SlipData(DataElement dataElement, HashMap<String, String[]> filterListMap) {
    	if (dataElement == null || !dataElement.isObject()) {
    		return;
    	}
        this.dataElement = dataElement;
        userInfo = new UserInfo(dataElement.asObjectElement().get("user_info"),
        		filterListMap.get(KEY_USER));
        workingHoursInfo = new WorkingHoursInfo(dataElement.asObjectElement().get("working_hours_info"),
        		filterListMap.get(KEY_WORKING_HOUR));
        deductionInfo = new DeductionInfo(dataElement.asObjectElement().get("deduction_info"),
        		filterListMap.get(KEY_DEDUCTION));
        setBenefitInfo(new BenefitInfo(dataElement.asObjectElement().get("benefit_info"),
        		filterListMap.get(KEY_BENEFIT)));
        incomeInfo = new IncomeInfo(dataElement.asObjectElement().get("income_info"),
        		filterListMap.get(KEY_INCOME));
    }

    public String getNetpay(Context context) {

        String netPay = Utility.getStringFromNumber(dataElement,
                SlipData.NET_PAY, context.getString(R.string.dollar));
        if (netPay == null) {
            return null;
        } else {
            return context.getString(R.string.netpay) + ": " + netPay;
        }
    }

    public String getTotalWorkingHours(Context context) {
        if (workingHoursInfo.getTotalWorkingHours(context) == null) {
            return null;
        }
        return workingHoursInfo.getTotalWorkingHours(context) + context.getString(R.string.hour);
    }

    public String getTotalIncomeInfo(Context context) {
        if (incomeInfo.getTotal(context) == null) {
            return null;
        }
        return incomeInfo.getTotal(context) + context.getString(R.string.dollar);
    }

    public String getGrossPay(Context context) {

        if (incomeInfo.getTotal(context) == null) {
            return null;
        }

        return incomeInfo.getGrossPay(context) + context.getString(R.string.dollar);
    }

    public String getFPayTypeName(Context context) {

        return userInfo.getFpayTypeName(context);
    }

    public String getProductionLineCode(Context context) {
        return userInfo.getProductionLineCode(context);
    }

    public String getMonthlyRate(Context context) {
        return userInfo.getMonthlyRate(context);
    }

    public String getHourlyRate(Context context) {
        return context.getString(R.string.hourlyrate);
    }

    public String getTotalDeduction(Context context) {
    	NumberFormat formatter = NumberFormat.getNumberInstance();
    	formatter.setMaximumFractionDigits(2);
    	formatter.setMinimumFractionDigits(2);
    	String s = formatter.format(deductionInfo.getTotalDeductionInNumber());
        return context.getString(R.string.sum_deduction)
        		+ ": " + s + context.getString(R.string.dollar);
    }

    public String getYearAndMonth(Context context) {
        DataElement e = dataElement.asObjectElement().get("fyear");
        String dateString = "";
        if (e != null && e.isPrimitive()) {
            dateString = e.asPrimitiveElement().valueAsInt()
                    + context.getString(R.string.year);
        }

        e = dataElement.asObjectElement().get("fperiod");
        if (e != null && e.isPrimitive()) {
            dateString = dateString + e.asPrimitiveElement().valueAsInt()
                    + context.getString(R.string.month);
        }

        return dateString;
    }

}
