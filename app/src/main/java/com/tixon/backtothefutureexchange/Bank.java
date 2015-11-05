package com.tixon.backtothefutureexchange;

import android.app.Activity;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by Tixon
 */
public class Bank {

    private static volatile Bank instance;

    public static final int CURRENCY_DOLLARS = 0;
    public static final int CURRENCY_POUNDS = 1;
    public static final int CURRENCY_RUBLES = 2;

    private int currency;
    private double exchangeRate;

    private boolean wasYearChanged = false;
    //private double lastRate = 60;
    private double[] lastRate = new double[3];

    private static ControlPanelActivity controlPanel;

    private Calendar date;

    public static Bank getInstance() {
        Bank localInstance = instance;

        if(localInstance == null) {
            synchronized (Bank.class) {
                localInstance = instance;
                if(localInstance == null) {
                    localInstance = instance = new Bank();
                }
            }
        }
        return localInstance;
    }

    private Bank() {
        date = Calendar.getInstance();
        lastRate[0] = 60; //доллары
        lastRate[1] = 70; //евро
    }

    //используется в ControlPanelActivity для уведомлении о возможности смены курса валют
    public void notifyYearChanged() {
        wasYearChanged = true;
    }

    //задаёт индекс валюты
    public void setCurrency(int currencyIndex) {
        this.currency = currencyIndex;
    }

    public int getCurrency() {
        return this.currency;
    }
    //возвращает текущий курс
    public double getExchangeRate() {
        return exchangeRate;
    }

    //возвращает список доступных для перевода валют
    public int[] getAvailableCurrencies() {
        int[] availableCurrencies = new int[2];
        boolean isPassed = false;
        for(int i = 0; i < availableCurrencies.length; i++) {
            if(i == currency) {
                isPassed = true;
            }
            if(isPassed) {
                availableCurrencies[i] = i+1;
            } else {
                availableCurrencies[i] = i;
            }
        }
        return availableCurrencies;
    }

    //возвращает сокращённое название валюты
    public String getCurrencySymbol() {
        switch (currency) {
            case CURRENCY_DOLLARS:
                return "USD ";
            case CURRENCY_POUNDS:
                return "GBP ";
            case CURRENCY_RUBLES:
                return "RUB ";
            default: return "";
        }
    }

    /*public void setDate(Calendar date) {
        this.date.setTimeInMillis(date.getTimeInMillis());
    }*/

    //возвращает значение валюты в конкретном году
    private double getCurrencyValue() {
        Random decimalRandom = new Random();
        Random fractionalRandom = new Random();
        double minCurrency, maxCurrency, exchangeRate;
        minCurrency = 20;
        maxCurrency = 50;

        //Log.d("myLogs", "minCurrency = " + minCurrency + ", maxCurrency = " + maxCurrency);
        if(wasYearChanged) {
            exchangeRate = decimalRandom.nextInt((int) maxCurrency + 1) + minCurrency;
            exchangeRate += (fractionalRandom.nextInt(1001) / 1000d);
            lastRate[currency] = exchangeRate;
            Log.d("myLogs", "rate = " + exchangeRate);
            wasYearChanged = false;
        } else {
            exchangeRate = lastRate[currency];
        }
        this.exchangeRate = exchangeRate;
        return exchangeRate;
    }

    //методы преобразования валют

    private double rublesFromDollars(double currency) {
        //return dollars[yearIndex] * currency;
        return getCurrencyValue() * currency;
    }

    private double rublesFromPounds(double currency) {
        return getCurrencyValue() * currency;
    }

    private double dollarsFromRubles(double currency) {
        return (1/getCurrencyValue()) * currency;
    }

    private double poundsFromRubles(double currency) {
        return (1/getCurrencyValue()) * currency;
    }

    //доллар - рубль - фунт
    private double poundsFromDollars(double currency) {
        double rubles = rublesFromDollars(currency);
        return poundsFromRubles(rubles);

    }

    //фунт - рубль - доллар
    private double dollarsFromPounds(double currency) {
        double rubles = rublesFromPounds(currency);
        return dollarsFromRubles(rubles);
    }

    /*private double[] stringToDoubleArray(String[] stringArray) {
        double[] array = new double[stringArray.length];
        for(int i = 0; i < stringArray.length; i++) {
            array[i] = Double.parseDouble(stringArray[i]);
        }
        return array;
    }*/

    public double change(int from, int to, double value) {
        double result = value; //чтобы в случае перевода, например, из долларов в доллары,
        //как в случае с покупкой плутония, не получился ноль
        if (from == CURRENCY_DOLLARS && to == CURRENCY_RUBLES) {
            result = rublesFromDollars(value);
        } else if (from == CURRENCY_DOLLARS && to == CURRENCY_POUNDS) {
            result = poundsFromDollars(value);
        } else if (from == CURRENCY_POUNDS && to == CURRENCY_RUBLES) {
            result = rublesFromPounds(value);
        } else if (from == CURRENCY_POUNDS && to == CURRENCY_DOLLARS) {
            result = dollarsFromPounds(value);
        } else if (from == CURRENCY_RUBLES && to == CURRENCY_DOLLARS) {
            result = dollarsFromRubles(value);
        } else if (from == CURRENCY_RUBLES && to == CURRENCY_POUNDS) {
            result = poundsFromRubles(value);
        }
        return result;
    }
}
