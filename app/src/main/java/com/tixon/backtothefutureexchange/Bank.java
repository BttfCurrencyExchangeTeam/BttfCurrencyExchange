package com.tixon.backtothefutureexchange;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Tixon
 */
public class Bank {

    private static volatile Bank instance;

    public static final int CURRENCY_RUBLES = 0;
    public static final int CURRENCY_DOLLARS = 1;
    public static final int CURRENCY_POUNDS = 2;

    public static final int YEAR_1946 = 0;
    public static final int YEAR_1955 = 1;
    public static final int YEAR_1985 = 2;
    public static final int YEAR_2015 = 3;

    private int currency;
    private double exchangeRate;

    private boolean wasYearChanged = false;
    private double savedDollarsRate, savedPoundsRate;

    private Calendar date;

    //депозиты
    private ArrayList<Deposit> deposits;
    //значения массивов - сколько рублей дают за конкретную валюту
    private double[] dollars, pounds;
    //индекс массива с определённым годом (см. res/values/currencies.xml)
    private int yearIndex;
    //указывает необходимость точного курса в конкретный год
    private boolean isExactRate = false;

    public static Bank getInstance(String[] dollars, String[] pounds) {
        Bank localInstance = instance;

        if(localInstance == null) {
            synchronized (Bank.class) {
                localInstance = instance;
                if(localInstance == null) {
                    localInstance = instance = new Bank(dollars, pounds);
                }
            }
        }
        return localInstance;
    }

    private Bank(String[] dollars, String[] pounds) {
        this.dollars = stringToDoubleArray(dollars);
        this.pounds = stringToDoubleArray(pounds);
        date = Calendar.getInstance();
        deposits = new ArrayList<>();
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

    //задаёт индекс массива для выбора определённого года
    //из табло выбора временного интервала
    public void setYearIndex(int year) {
        if(year <= 1946) {
            this.yearIndex = YEAR_1946;
        } else if(year > 1946 && year <= 1955) {
            this.yearIndex = YEAR_1955;
        } else if(year > 1955 && year <= 1985) {
            this.yearIndex = YEAR_1985;
        } else if(year > 1985) {
            this.yearIndex = YEAR_2015;
        }
        //точный курс валют в определённые года,
        //в остальные - случайный в пределах точных значений
        isExactRate = year == 1946 || year == 1955 || year == 1985 || year == 2015;
    }

    public void setDate(Calendar date) {
        this.date.setTimeInMillis(date.getTimeInMillis());
    }

    //возвращает значение валюты в конкретном году
    private double getCurrencyValue(int currencyIndex) {
        Random decimalRandom = new Random();
        Random fractionalRandom = new Random();
        double minCurrency, maxCurrency, exchangeRate;
        minCurrency = 0;
        maxCurrency = 0;

        switch(yearIndex) {
            case YEAR_1946:
                break;
            case YEAR_1955:
                minCurrency = getCurrencyByIndexAndYear(currencyIndex, YEAR_1946);
                maxCurrency = getCurrencyByIndexAndYear(currencyIndex, YEAR_1955);
                break;
            case YEAR_1985:
                minCurrency = getCurrencyByIndexAndYear(currencyIndex, YEAR_1955);
                maxCurrency = getCurrencyByIndexAndYear(currencyIndex, YEAR_1985);
                break;
            case YEAR_2015:
                minCurrency = getCurrencyByIndexAndYear(currencyIndex, YEAR_1985);
                maxCurrency = getCurrencyByIndexAndYear(currencyIndex, YEAR_2015);
                break;
            default: break;
        }
        //select min currency
        if(minCurrency > maxCurrency) {
            exchangeRate = minCurrency;
            minCurrency = maxCurrency;
            maxCurrency = exchangeRate;
        }
        Log.d("myLogs", "minCurrency = " + minCurrency + ", maxCurrency = " + maxCurrency);
        if(isExactRate) {
            exchangeRate = maxCurrency;
        } else {
            exchangeRate = decimalRandom.nextInt((int) maxCurrency + 1) + minCurrency;
            exchangeRate += (fractionalRandom.nextInt(1001) / 1000d);
        }
        Log.d("myLogs", "course = " + exchangeRate);
        this.exchangeRate = exchangeRate;
        return exchangeRate;
    }

    private double getCurrencyByIndexAndYear(int currencyIndex, int yearIndex) {
        double result = 0;
        switch(currencyIndex) {
            case CURRENCY_DOLLARS:
                result = dollars[yearIndex];
                break;
            case CURRENCY_POUNDS:
                result = pounds[yearIndex];
                break;
            default: break;
        }
        return result;
    }

    private boolean canChangeWithRubles() {
        boolean result = false;
        return result;
    }

    //методы преобразования валют

    private double rublesFromDollars(double currency) {
        //return dollars[yearIndex] * currency;
        return getCurrencyValue(CURRENCY_DOLLARS) * currency;
    }

    private double rublesFromPounds(double currency) {
        return getCurrencyValue(CURRENCY_POUNDS) * currency;
    }

    private double dollarsFromRubles(double currency) {
        return (1/getCurrencyValue(CURRENCY_DOLLARS)) * currency;
    }

    private double poundsFromRubles(double currency) {
        return (1/getCurrencyValue(CURRENCY_POUNDS)) * currency;
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

    private double[] stringToDoubleArray(String[] stringArray) {
        double[] array = new double[stringArray.length];
        for(int i = 0; i < stringArray.length; i++) {
            array[i] = Double.parseDouble(stringArray[i]);
        }
        return array;
    }

    public double change(int from, int to, double value) {
        double result = value; //чтобы в случае перевода, например, из долларов в доллары,
        //как в случае с покупкой плутония, не получился ноль
        if(from == CURRENCY_DOLLARS && to == CURRENCY_RUBLES) {
            result = rublesFromDollars(value);
        } else if(from == CURRENCY_DOLLARS && to == CURRENCY_POUNDS) {
            result = poundsFromDollars(value);
        } else if(from == CURRENCY_POUNDS && to == CURRENCY_RUBLES) {
            result = rublesFromPounds(value);
        } else if(from == CURRENCY_POUNDS && to == CURRENCY_DOLLARS) {
            result = dollarsFromPounds(value);
        } else if(from == CURRENCY_RUBLES && to == CURRENCY_DOLLARS) {
            result = dollarsFromRubles(value);
        } else if(from == CURRENCY_RUBLES && to == CURRENCY_POUNDS) {
            result = poundsFromRubles(value);
        }
        return result;
    }

    //deposits

    public void addDeposit(Deposit deposit) {
        deposits.add(deposit);
    }

    public Deposit getDeposit(int position) {
        return deposits.get(position);
    }

    //long currentTime
    public ArrayList<Deposit> getDeposits() {
        /*ArrayList<Deposit> resultList = new ArrayList<>();
        for(Deposit deposit: deposits) {
            //если время создания вклада меньше настоящего времени currentTime
            if(deposit.getInitTime() <= currentTime) {
                resultList.add(deposit);
            }
            return resultList;
        }*/
        return deposits;
    }
}
