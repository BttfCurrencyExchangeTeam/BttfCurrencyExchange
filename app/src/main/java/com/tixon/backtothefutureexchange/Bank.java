package com.tixon.backtothefutureexchange;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tixon
 */
public class Bank implements OnCurrencyChangedListener {

    private static final String LOG_TAG = "myLogs_bank";

    private static volatile Bank instance;

    public static final int CURRENCY_DOLLARS = 0;
    public static final int CURRENCY_POUNDS = 1;
    public static final int CURRENCY_RUBLES = 2;

    private int currency;

    //вклады
    private ArrayList<Deposit> deposits;
    //значения массивов - сколько рублей дают за конкретную валюту
    private double[] dollars, pounds;

    //массив из двух элементов, описывающий временной участок
    private int timeRange[];

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

        this.timeRange = new int[2];
        deposits = new ArrayList<>();
    }

    /**
     *
     * @param t1: время начала периода
     * @param t2: время конца периода
     * @param c1: валюта в начале периода
     * @param c2: валюта в конце периода
     * @param timeArg: текущий момент времени
     * @return валюта в текущий момент времени
     */
    private double interpolate(long t1, long t2,
                               double c1, double c2, long timeArg) {
        return (timeArg * (c2 - c1) + (c1 * t2 - t1 * c2)) / (t2 - t1);
    }

    //задаёт индекс валюты
    public void setCurrency(int currencyIndex) {
        this.currency = currencyIndex;
    }

    public int getCurrency() {
        return this.currency;
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

    /*public int getTimeIndex(long timeInMillis) {
        int index = 0;
        for(int i = 1; i < Constants.TIMES.length; i++) {
            if((timeInMillis > Constants.TIMES[i-1]) && (timeInMillis <= Constants.TIMES[i])) {
                index = i;
                break;
            }
        }
        return index;
    }*/

    /**
     * Задаёт временной диапазон
     * @param timeInMillis: текущее время
     * @return диапазон с границами из двух дат, ближайших к текущему времени
     */
    public int[] getTimeRange(long timeInMillis) {
        int[] range = new int[2]; //[0]: время слева; [1]: время справа от timeInMillis
        for(int i = 1; i < Constants.TIMES.length; i++) {
            if((timeInMillis > Constants.TIMES[i-1]) && (timeInMillis <= Constants.TIMES[i])) {
                range[0] = i-1;
                range[1] = i;
                break;
            }
        }
        return range;
    }

    /**
     * По текущему времени возвращает ближайшие к числу времена
     * @param timeInMillis: текущее время
     * @return массив границ временного диапазона для вычисления курса валют
     */
    private long[] getTimes(long timeInMillis) {
        long[] times = new long[2];
        for(int i = 1; i < Constants.TIMES.length; i++) {
            if((timeInMillis > Constants.TIMES[i-1]) && (timeInMillis <= Constants.TIMES[i])) {
                times[0] = Constants.TIMES[i-1];
                times[1] = Constants.TIMES[i];
                break;
            }
        }
        return times;
    }

    /**
     * По текущему времени задаёт временной диапазон
     * @param timeInMillis: текущее время
     */
    private void setTimeRange(long timeInMillis) {
        this.timeRange = getTimeRange(timeInMillis);
    }

/*    private long[] getTimes() {
        long[] times = new long[2];
        times[0] = Constants.TIMES[timeRange[0]];
        times[1] = Constants.TIMES[timeRange[1]];
        return times;
    }*/

    /**
     * Возвращает курс валют для текущего времени
     * @param currencyIndex: индекс валюты
     * @param timeInMillis: текущее время
     * @return курс валют
     */
    private double getExchangeRate(int currencyIndex, long timeInMillis) {
        setTimeRange(timeInMillis);
        double[] currencies = getCurrencyByIndexAndTimeRange(currencyIndex);
        long[] times = getTimes(timeInMillis);
        double exchangeRate = interpolate(times[0], times[1], currencies[0], currencies[1], timeInMillis);
        Log.d(LOG_TAG, "exchangeRate = " + exchangeRate);
        return exchangeRate;
    }

    /**
     * Возвращает диапазон валют с учётом временного диапазона
     * @param currencyIndex: индекс валюты
     * @return диапазон валют
     */
    private double[] getCurrencyByIndexAndTimeRange(int currencyIndex) {
        double[] result = new double[2];
        switch (currencyIndex) {
            case CURRENCY_DOLLARS:
                result[0] = dollars[timeRange[0]];
                result[1] = dollars[timeRange[1]];
                break;
            case CURRENCY_POUNDS:
                result[0] = pounds[timeRange[0]];
                result[1] = pounds[timeRange[1]];
                break;
            default: break;
        }
        return result;
    }

    private double[] stringToDoubleArray(String[] stringArray) {
        double[] array = new double[stringArray.length];
        for(int i = 0; i < stringArray.length; i++) {
            array[i] = Double.parseDouble(stringArray[i]);
        }
        return array;
    }

    private double rublesFromDollars(double value, long timeInMillis) {
        return getExchangeRate(CURRENCY_DOLLARS, timeInMillis) * value;
    }

    private double rublesFromPounds(double value, long timeInMillis) {
        return getExchangeRate(CURRENCY_POUNDS, timeInMillis) * value;
    }

    private double dollarsFromRubles(double value, long timeInMillis) {
        return (1 / getExchangeRate(CURRENCY_DOLLARS, timeInMillis)) * value;
    }

    private double poundsFromRubles(double value, long timeInMillis) {
        return (1 / getExchangeRate(CURRENCY_POUNDS, timeInMillis)) * value;
    }

    private double poundsFromDollars(double value, long timeInMillis) {
        double rubles = rublesFromDollars(value, timeInMillis);
        return poundsFromRubles(rubles, timeInMillis);
    }

    private double dollarsFromPounds(double value, long timeInMillis) {
        double rubles = rublesFromPounds(value, timeInMillis);
        return dollarsFromRubles(rubles, timeInMillis);
    }

    /**
     * Автоматический обмен валют
     * @param currencyFrom: индекс валюты, которую нужно поменять
     * @param currencyTo: индекс валюты, на которую нужно поменять
     * @param value: количество денег, которые нужно поменять
     * @param timeInMillis: текущее время
     * @return количество денег после обмена
     */
    public double change(int currencyFrom, int currencyTo, double value, long timeInMillis) {
        double result = value;
        if(currencyFrom == CURRENCY_DOLLARS && currencyTo == CURRENCY_RUBLES) {
            result = rublesFromDollars(value, timeInMillis);
        } else if(currencyFrom == CURRENCY_DOLLARS && currencyTo == CURRENCY_POUNDS) {
            result = poundsFromDollars(value, timeInMillis);
        } else if(currencyFrom == CURRENCY_POUNDS && currencyTo == CURRENCY_RUBLES) {
            result = rublesFromPounds(value, timeInMillis);
        } else if(currencyFrom == CURRENCY_POUNDS && currencyTo == CURRENCY_DOLLARS) {
            result = dollarsFromPounds(value, timeInMillis);
        } else if(currencyFrom == CURRENCY_RUBLES && currencyTo == CURRENCY_DOLLARS) {
            result = dollarsFromRubles(value, timeInMillis);
        } else if(currencyFrom == CURRENCY_RUBLES && currencyTo == CURRENCY_POUNDS) {
            result = poundsFromRubles(value, timeInMillis);
        }
        return result;
    }

    //вклады

    public void addDeposit(Deposit deposit) {
        deposits.add(deposit);
    }

    public void setDeposits(ArrayList<Deposit> deposits) {
        this.deposits = new ArrayList<>();
        for(Deposit d: deposits) {
            this.deposits.add(d);
        }
    }

    //long currentTime
    public ArrayList<Deposit> getDeposits(long timeInMillis) {
        ArrayList<Deposit> resultList = new ArrayList<>();
        for(int i = 0; i < this.deposits.size(); i++) {
            if(timeInMillis >= this.deposits.get(i).getInitTime()) {
                resultList.add(this.deposits.get(i));
            }
        }
        this.deposits.clear();
        this.deposits.addAll(resultList);
        return resultList;
    }

    public void clearDeposits() {
        this.deposits = new ArrayList<>();
    }

    @Override
    public void onCurrencyChanged(int currency) {
        setCurrency(currency);
    }
}
