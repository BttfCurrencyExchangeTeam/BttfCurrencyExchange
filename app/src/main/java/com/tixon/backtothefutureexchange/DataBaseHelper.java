package com.tixon.backtothefutureexchange;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "myLogs";

    private static final String DB_NAME = "bttf_data_base";
    private static final int DB_VERSION = 1;

    //tables
    private static final String TABLE_DEPOSITS = "table_deposits";
    private static final String TABLE_PURSE = "table_purse";
    private static final String TABLE_DELOREAN = "table_delorean";
    private static final String TABLE_GENERAL = "table_general";
    private static final String TABLE_SAVED_GAMES = "table_saved_games";

    //common fields
    private static final String UID = "_id";
    private static final String GAME_SAVED_TIME = "game_saved_time_field";
    private static final String GAME_SAVED_NAME = "game_saved_name_field";

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

    //create tables
    private static final String CREATE_TABLE_DEPOSITS = "create table " + TABLE_DEPOSITS + " (" +
            UID + " integer primary key autoincrement, " + GAME_SAVED_TIME + " integer, " +
            GAME_SAVED_NAME + " text, " + DEPOSIT_NAME + " text, " +
            DEPOSIT_CURRENCY + " integer, " + DEPOSIT_INIT_TIME + " integer, " +
            DEPOSIT_INIT_VALUE + " real, " + DEPOSIT_INTEREST + " real" + ");";
    private static final String CREATE_TABLE_PURSE = "create table " + TABLE_PURSE + " (" +
            UID + " integer primary key autoincrement, " + GAME_SAVED_TIME + " integer, " +
            GAME_SAVED_NAME + " text, " + PURSE_RUBLES_IMPERIAL + " real, " +
            PURSE_RUBLES_SOVIET + " real, " + PURSE_RUBLES_RF + " real, " +
            PURSE_DOLLARS + " real, " + PURSE_POUNDS + " real" + ");";
    private static final String CREATE_TABLE_DELOREAN = "create table " + TABLE_DELOREAN + " (" +
            UID + " integer primary key autoincrement, " + GAME_SAVED_TIME + " integer, " +
            GAME_SAVED_NAME + " text, " + DELOREAN_PLUTONIUM + " integer, " +
            DELOREAN_FUEL + " real" + ");";
    private static final String CREATE_TABLE_GENERAL = "create table " + TABLE_GENERAL + " (" +
            UID + " integer primary key autoincrement, " + GAME_SAVED_TIME + " integer, " +
            GAME_SAVED_NAME + " text, " + TIME_PRESENT + " integer, " +
            TIME_DESTINATION + " integer, " + TIME_LAST_DEPARTED + " integer, " +
            CURRENCY_SELECTED + " integer" + ");";
    private static final String CREATE_TABLE_SAVED_GAMES = "create table " + " (" +
            UID + " integer primary key autoincrement, " + GAME_SAVED_TIME + " integer, " +
            GAME_SAVED_NAME + " text" + ");";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GENERAL);
        db.execSQL(CREATE_TABLE_DEPOSITS);
        db.execSQL(CREATE_TABLE_PURSE);
        db.execSQL(CREATE_TABLE_DELOREAN);
        db.execSQL(CREATE_TABLE_SAVED_GAMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //add deposits
    public void addDeposits(SQLiteDatabase db, ArrayList<Deposit> deposits, long gameSavedTime) {
        ContentValues cv;
        for(int i = 0; i < deposits.size(); i++) {
            cv = new ContentValues();
            cv.put(DEPOSIT_NAME, deposits.get(i).getName());
            cv.put(DEPOSIT_INTEREST, deposits.get(i).getInterest());
            cv.put(DEPOSIT_CURRENCY, deposits.get(i).getCurrency());
            cv.put(DEPOSIT_INIT_TIME, deposits.get(i).getInitTime());
            cv.put(DEPOSIT_INIT_VALUE, deposits.get(i).getInitValue());

            cv.put(GAME_SAVED_TIME, gameSavedTime);

            db.insert(TABLE_DEPOSITS, null, cv);
        }
    }

    //add purse
    public void addPurse(SQLiteDatabase db, double[] purse, long gameSavedTime) {
        ContentValues cv = new ContentValues();
        cv.put(PURSE_RUBLES_IMPERIAL, purse[0]);
        cv.put(PURSE_RUBLES_SOVIET, purse[1]);
        cv.put(PURSE_RUBLES_RF, purse[2]);
        cv.put(PURSE_DOLLARS, purse[3]);
        cv.put(PURSE_POUNDS, purse[4]);

        cv.put(GAME_SAVED_TIME, gameSavedTime);

        db.insert(TABLE_PURSE, null, cv);
    }

    //add delorean info
    public void addDelorean(SQLiteDatabase db, Delorean delorean, long gameSavedTime) {
        ContentValues cv = new ContentValues();
        cv.put(DELOREAN_PLUTONIUM, delorean.getPlutonium());
        cv.put(DELOREAN_FUEL, delorean.getFuel());

        cv.put(GAME_SAVED_TIME, gameSavedTime);

        db.insert(TABLE_DELOREAN, null, cv);
    }

    //add general info
    public void addGeneralData(SQLiteDatabase db, long presentTime, long destinationTime,
                               long lastTimeDeparted, int currencySelected, long gameSavedTime) {
        ContentValues cv = new ContentValues();
        cv.put(TIME_PRESENT, presentTime);
        cv.put(TIME_DESTINATION, destinationTime);
        cv.put(TIME_LAST_DEPARTED, lastTimeDeparted);
        cv.put(CURRENCY_SELECTED, currencySelected);

        cv.put(GAME_SAVED_TIME, gameSavedTime);

        db.insert(TABLE_GENERAL, null, cv);
    }

    //add saved game info
    public void addSavedGame(SQLiteDatabase db, long gameSavedTime, String gameSavedName) {
        ContentValues cv = new ContentValues();
        cv.put(GAME_SAVED_TIME, gameSavedTime);
        cv.put(GAME_SAVED_NAME, gameSavedName);
        db.insert(TABLE_SAVED_GAMES, null, cv);
    }

    //READ

    //read deposits
    public ArrayList<Deposit> readDeposits(SQLiteDatabase db, long gameSavedTime) {
        ArrayList<Deposit> deposits = new ArrayList<>();
        String selection = GAME_SAVED_TIME + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(gameSavedTime)};
        Cursor c = db.query(TABLE_DEPOSITS, null, selection, selectionArgs, null, null, null);
        if(c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex(DEPOSIT_NAME);
            int currencyColIndex = c.getColumnIndex(DEPOSIT_CURRENCY);
            int initTimeColIndex = c.getColumnIndex(DEPOSIT_INIT_TIME);
            int initValueColIndex = c.getColumnIndex(DEPOSIT_INIT_VALUE);
            int interestColIndex = c.getColumnIndex(DEPOSIT_INTEREST);

            do {
                deposits.add(new Deposit(c.getString(nameColIndex), c.getLong(initTimeColIndex), c.getDouble(initValueColIndex),
                        c.getDouble(interestColIndex), c.getInt(currencyColIndex)));
            } while(c.moveToNext());
        } else {
            Log.d(LOG_TAG, "read deposits: no rows found");
        }
        c.close();
        return deposits;
    }

    //read purse
    public boolean readPurse(SQLiteDatabase db, Purse purse, long gameSavedTime) {
        String selection = GAME_SAVED_TIME + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(gameSavedTime)};
        Cursor c = db.query(TABLE_PURSE, null, selection, selectionArgs, null, null, null);
        if(!c.moveToFirst()) {
            return false;
        } else {
            int rublesImperialColIndex = c.getColumnIndex(PURSE_RUBLES_IMPERIAL);
            int rublesSovietColIndex = c.getColumnIndex(PURSE_RUBLES_SOVIET);
            int rublesRfColIndex = c.getColumnIndex(PURSE_RUBLES_SOVIET);
            int dollarsColIndex = c.getColumnIndex(PURSE_DOLLARS);
            int poundsColIndex = c.getColumnIndex(PURSE_POUNDS);

            do {
                purse.setImperialRubles(c.getDouble(rublesImperialColIndex));
                purse.setSovietRubles(c.getDouble(rublesSovietColIndex));
                purse.setRussianRubles(c.getDouble(rublesRfColIndex));
                purse.setDollars(c.getDouble(dollarsColIndex));
                purse.setPounds(c.getDouble(poundsColIndex));
            } while (c.moveToNext());
        }
        c.close();
        return true;
    }

    //read general info
    public Bundle readGeneralInfo(SQLiteDatabase db, long gameSavedTime) {
        String selection = GAME_SAVED_TIME + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(gameSavedTime)};
        Bundle bundle = new Bundle();
        Cursor c = db.query(TABLE_PURSE, null, selection, selectionArgs, null, null, null);
        if(c.moveToFirst()) {
            int timePresentColIndex = c.getColumnIndex(TIME_PRESENT);
            int timeDestinationColIndex = c.getColumnIndex(TIME_DESTINATION);
            int timeLastDepartedColIndex = c.getColumnIndex(TIME_LAST_DEPARTED);
            int currencyColIndex = c.getColumnIndex(CURRENCY_SELECTED);

            do {
                bundle.putLong(Constants.BUNDLE_TIME_PRESENT, c.getLong(timePresentColIndex));
                bundle.putLong(Constants.BUNDLE_TIME_DESTINATION, c.getLong(timeDestinationColIndex));
                bundle.putLong(Constants.BUNDLE_TIME_LAST_DEPARTED, c.getLong(timeLastDepartedColIndex));
                bundle.putInt(Constants.BUNDLE_CURRENCY_SELECTED, c.getInt(currencyColIndex));
            } while(c.moveToNext());
        } else {
            Log.d(LOG_TAG, "read general info: no rows found");
        }
        c.close();
        return bundle;
    }

    //UPDATE

    //update purse
    public void updatePurse(SQLiteDatabase db, double[] purse, long gameSavedTime) {
        String selection = GAME_SAVED_TIME + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(gameSavedTime)};
        ContentValues cv = new ContentValues();
        cv.put(PURSE_RUBLES_IMPERIAL, purse[0]);
        cv.put(PURSE_RUBLES_SOVIET, purse[1]);
        cv.put(PURSE_RUBLES_RF, purse[2]);
        cv.put(PURSE_DOLLARS, purse[3]);
        cv.put(PURSE_POUNDS, purse[4]);

        long updateCount = db.update(TABLE_PURSE, cv, selection, selectionArgs);
        Log.d(LOG_TAG, "update purse: rows count = " + updateCount);
    }

    //update general info
    public void updateGeneralInfo(SQLiteDatabase db, long presentTime, long destinationTime,
                                  long lastTimeDeparted, int currencySelected, long gameSavedTime) {
        String selection = GAME_SAVED_TIME + " = ?";
        String[] selectionArgs = new String[] {String.valueOf(gameSavedTime)};
        ContentValues cv = new ContentValues();
        cv.put(TIME_PRESENT, presentTime);
        cv.put(TIME_DESTINATION, destinationTime);
        cv.put(TIME_LAST_DEPARTED, lastTimeDeparted);
        cv.put(CURRENCY_SELECTED, currencySelected);

        long updateCount = db.update(TABLE_GENERAL, cv, selection, selectionArgs);
        Log.d(LOG_TAG, "update general info: rows count = " + updateCount);
    }

    //DELETE

    //delete deposits
    public void deleteDeposits(SQLiteDatabase db) {
        long deleteCount = db.delete(TABLE_DEPOSITS, null, null);
        Log.d(LOG_TAG, "delete deposits: rows count = " + deleteCount);
    }
}
