package com.tixon.backtothefutureexchange;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "bttf_data_base";
    private static final int DB_VERSION = 1;

    //tables
    private static final String TABLE_DEPOSITS = "table_deposits";
    private static final String TABLE_PURSE = "table_purse";
    private static final String TABLE_DELOREAN = "table_delorean";
    private static final String TABLE_TIME = "table_time";

    //table deposits fields
    private static final String DEPOSIT_NAME = "deposit_name_field";
    private static final String DEPOSIT_CURRENCY = "deposit_currency_field";
    private static final String DEPOSIT_INIT_TIME = "deposit_init_time_field";
    private static final String DEPOSIT_INIT_VALUE = "deposit_init_value_field";
    private static final String DEPOSIT_INTEREST = "deposit_interest_field";

    //table purse fields
    private static final String PURSE_RUBLES_IMPERIAL = "purse_imperial_rubles_field";
    private static final String PURSE_RUBLES_SOVIET = "purse_soviet_rubles_field";
    private static final String PURSE_RUBLES_RF = "purse_rf_rubles_field";
    private static final String PURSE_DOLLARS = "purse_imperial_dollars_field";
    private static final String PURSE_POUNDS = "purse_imperial_pounds_field";

    //table delorean fields
    private static final String DELOREAN_PLUTONIUM = "delorean_plutonium_field";
    private static final String DELOREAN_FUEL = "delorean_fuel_field";

    //table time fields
    private static final String TIME_PRESENT = "time_present_field";
    private static final String TIME_LAST_DEPARTED = "time_last_departed_field";
    private static final String TIME_DESTINATION = "time_destination_field";
    private static final String CURRENCY_SELECTED = "currency_selected_field";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
