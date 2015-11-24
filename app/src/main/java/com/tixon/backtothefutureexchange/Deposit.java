package com.tixon.backtothefutureexchange;

import java.util.Calendar;

public class Deposit {
    private String name;
    private int currency;
    private long initTime; //время в мс создания вклада
    private double initValue; //первоначальное значение вклада
    private double interest; //процентная ставка по вкладу

    public Deposit (String name, long initTime, double initValue, double interest, int currencyIndex) {
        this.currency = currencyIndex;
        this.name = name;
        this.initTime = initTime;
        this.initValue = initValue;
        this.interest = interest;
    }

    public String getName() {return name;}

    public int getCurrency() {return currency;}

    public double getInterest() {return interest;}

    public long getInitTime() {
        return initTime;
    }

    public double getInitValue() {
        return initValue;
    }

    public double getValue(long currentTime) {
        double newValue;
        int yearDifference;

        if(currentTime >= initTime) {
            yearDifference = (int) yearDifference(currentTime, initTime);
            newValue = initValue;

            for(int i = 0; i < yearDifference; i++) {
                newValue += newValue * (interest / 100d);
            }
        } else {
            newValue = 0;
        }
        return newValue;
    }

    private long yearDifference(long currentTime, long initTime) {
        return (currentTime - initTime) / Constants.ONE_YEAR;
    }

    private long millisInYear(long time) {
        long millis = 60 * 60 * 24;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        if((year % 4 == 0) && (year % 100 != 0) || year % 400 == 0) {
            millis *= 366; //год високосный
        } else {
            millis *= 365; //год невисокосный
        }
        return millis;
    }
}
