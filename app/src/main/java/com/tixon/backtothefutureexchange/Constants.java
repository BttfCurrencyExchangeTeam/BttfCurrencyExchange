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

    public static final double[] interestValues = new double[] {7, 10, 12};

    public static final long JAN_1900_1 = -2208999600000L;
    //                                    -2208999600000
    public static final long DEC_1922_22 = -1484103600000L;
    //                                     -1484103600000
    public static final long JAN_1998_1 = 883602800000L;
    public static final long DEC_2045_31 = 2398280400000L;
    //                                     2398280400000

    public static final long SAVED_TIME_DEFAULT = -2209089600000l;

    public static final long[] TIMES = new long[] {
            //-2209003200000l, //1900 01.01
            //http://currentmillis.com
            -2209089600000l, //1899 31.12
            -1704164400000l, //1916 01.01
            -1641006000000l, //1918 01.01
            -1451703600000l, //1924 01.01
            -1104548400000l, //1935 01.01
            -1065150000000l, //1936 01.04
            -757393200000l, //1946 01.01
            -473396400000l, //1955 01.01
            -284094000000l, //1960 31.12
            62370000000l, //1971 24.12
            473371200000l, //1985 01.01
            709333200000l, //1992 24.06
            709938000000l, //1992 01.07
            883601999000l, //1997 31.12
            883602000000l, //1998 01.01
            1042059600000l, //2003 09.01
            1216155600000l, //2008 16.07
            1234990800000l, //2009 19.02
            1259096400000l, //2009 25.11
            1378324800000l, //2013 05.09
            1417640400000l, //2014 04.12
            1436907600000l, //2015 15.07
            1735678800000l, //2025 01.01
            2061579600000l, //2025 01.05
            2398280400000l //2045 31.12
    };

    public static final double[] DOLLARS = new double[] {

    };

    public static final double[] POUNDS = new double[] {

    };

    public static final int GAME_START = 1;
    public static final int MONEY_LEVEL_1 = 100000;
    public static final int MONEY_LEVEL_2 = 1000000;
    public static final int MONEY_LEVEL_3 = 1000000000;

    public static final String KEY_INTEREST = "key_interest";
    public static final String KEY_CURRENT_TIME = "key_current_time";


    public static final String KEY_RESOURCE_TYPE = "key_resource_type";
    public static final String KEY_RESOURCES_PRESENT_TIME = "key_resources_present_time";
    public static final int RESOURCE_TYPE_PLUTONIUM = 1;
    public static final int RESOURCE_TYPE_FUEL = 2;

    public static final double PLUTONIUM_COEFFICIENT = 1.3;
    public static final int PLUTONIUM_PRICE = 10000; //цена за единицу плутония в долларах
    public static final int FUEL_PRICE = 1; //цена за литр топлива в долларах

    //секунды * минуты * часы * дни * 1000 = мс в году
    public static final long ONE_YEAR = 60l * 60l * 24l * 365l * 1000l;
}
