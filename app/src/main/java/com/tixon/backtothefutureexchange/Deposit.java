package com.tixon.backtothefutureexchange;

import java.util.Calendar;

public class Deposit {
    private String name;
    private int currency;
    private long initTime; //время в мс создания вклада
    private double initValue; //первоначальное значение вклада
    private double interest; //процентная ставка по вкладу

    //int currency,
    public Deposit (String name, long initTime, double initValue, double interest) {
        this.currency = currency;
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
        Calendar currentCalendar = Calendar.getInstance();
        Calendar initCalendar = Calendar.getInstance();

        currentCalendar.setTimeInMillis(currentTime);
        initCalendar.setTimeInMillis(initTime);

        int currentYear = currentCalendar.get(Calendar.YEAR);
        int initYear = initCalendar.get(Calendar.YEAR);

        if(currentYear >= initYear) {
            yearDifference = currentYear - initYear;
            newValue = initValue;

            for(int i = 0; i < yearDifference; i++) {
                newValue += newValue * (interest / 100d);
            }
        } else {
            newValue = Constants.CODE_DEPOSIT_NO_EXISTS;
        }
        return newValue;
    }
}
