package com.tixon.backtothefutureexchange;

/**
 * Created by Tixon
 */
public class Exchange {

    private static volatile Exchange instance;

    public static final int CURRENCY_RUBLES = 0;
    public static final int CURRENCY_DOLLARS = 1;
    public static final int CURRENCY_POUNDS = 2;

    public static final int YEAR_1946 = 0;
    public static final int YEAR_1955 = 1;
    public static final int YEAR_1985 = 2;
    public static final int YEAR_2015 = 3;

    private int currency;

    //значения массивов - сколько рублей дают за конкретную валюту
    private double[] dollars, pounds;
    //индекс массива с определённым годом (см. res/values/currencies.xml)
    private int yearIndex;

    public void setCurrency(int currencyIndex) {
        this.currency = currencyIndex;
    }

    public int getCurrency() {
        return this.currency;
    }

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

    public String getCurrencySymbol() {
        switch (currency) {
            case CURRENCY_DOLLARS:
                return "$";
            case CURRENCY_POUNDS:
                return "P";
            case CURRENCY_RUBLES:
                return "R";
            default: return "";
        }
    }

    public static Exchange getInstance(String[] dollars, String[] pounds) {
        Exchange localInstance = instance;

        if(localInstance == null) {
            synchronized (Exchange.class) {
                localInstance = instance;
                if(localInstance == null) {
                    localInstance = instance = new Exchange(dollars, pounds);
                }
            }
        }
        return localInstance;
    }

    private Exchange(String[] dollars, String[] pounds) {
        this.dollars = stringToDoubleArray(dollars);
        this.pounds = stringToDoubleArray(pounds);
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
    }

    //методы преобразования валют
    private double rublesFromDollars(double currency) {
        return dollars[yearIndex] * currency;
    }

    private double rublesFromPounds(double currency) {
        return pounds[yearIndex] * currency;
    }

    private double dollarsFromRubles(double currency) {
        return (1/dollars[yearIndex]) * currency;
    }

    private double poundsFromRubles(double currency) {
        return (1/pounds[yearIndex]) * currency;
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
        double result = 0;
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
}
