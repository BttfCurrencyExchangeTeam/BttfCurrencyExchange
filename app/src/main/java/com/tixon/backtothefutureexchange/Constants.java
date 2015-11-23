package com.tixon.backtothefutureexchange;

public class Constants {
    public static final String TYPEFACE_DIGITS = "ds_digit_font.ttf";

    public static final String KEY_TIME_DESTINATION = "key_time_destination";
    public static final String KEY_TIME_PRESENT = "key_time_present";
    public static final String KEY_TIME_LAST = "key_time_last";

    public static final String BACK_STACK_ADD_DEPOSIT = "ADD_DEPOSIT_FRAGMENT";
    public static final String BACK_STACK_CHANGE = "CHANGE_FRAGMENT";
    public static final String BACK_STACK_ADD_PLUTONIUM = "ADD_PLUTONIUM_FRAGMENT";
    public static final String BACK_STACK_ADD_FUEL = "ADD_FUEL_FRAGMENT";

    public static final String KEY_NEW_OR_CONTINUE = "key_new_or_continue";
    public static final int KEY_NEW = 1;
    public static final int KEY_CONTINUE = 2;

    public static final String PREFERENCE_CURRENT_TIME = "preference_current_time";

    //bundle
    public static final String BUNDLE_DB_TIME_PRESENT = "bundle_time_present";
    public static final String BUNDLE_DB_TIME_DESTINATION = "bundle_time_destination";
    public static final String BUNDLE_DB_TIME_LAST_DEPARTED = "bundle_time_last_departed";
    public static final String BUNDLE_DB_CURRENCY_SELECTED = "bundle_currency_selected";

    public static final int REQUEST_CODE_TRAVEL = 10;

    public static final int CODE_DEPOSIT_NO_EXISTS = -1828;

    public static final double[] interestValues = new double[] {7, 10, 12, 13.5, 16, 22};

    public static final long JAN_1900_1 = -2208999600000L;
    //                                    -2208999600000
    public static final long DEC_1922_22 = -1484103600000L;
    //                                     -1484103600000
    public static final long JAN_1998_1 = 883602800000L;
    public static final long DEC_2045_31 = 2398280400000L;
    //                                     2398280400000

    public static final String KEY_RESOURCE_TYPE = "key_resource_type";
    public static final String KEY_RESOURCES_PRESENT_TIME = "key_resources_present_time";
    public static final int RESOURCE_TYPE_PLUTONIUM = 1;
    public static final int RESOURCE_TYPE_FUEL = 2;

    public static final int PLUTONIUM_PRICE = 10000; //цена за плутоний в долларах

    //секунды * минуты * часы * дни * 1000 = мс в году
    public static final long ONE_YEAR = 60l * 60l * 24l * 365l * 1000l;
}
