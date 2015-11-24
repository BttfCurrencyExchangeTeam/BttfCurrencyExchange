package com.tixon.backtothefutureexchange;

import android.util.Log;

public class Purse implements OnDepositAddListener {
    private static volatile Purse instance;

    public static final int IMPERIAL_RUBLES = 0;
    public static final int SOVIET_RUBLES = 1;
    public static final int RF_RUBLES = 2;
    public static final int DOLLARS = 3;
    public static final int POUNDS = 4;

    public static Purse getInstance() {
        Purse localInstance = instance;

        if(localInstance == null) {
            synchronized (Purse.class) {
                localInstance = instance;
                if(localInstance == null) {
                    localInstance = instance = new Purse();
                }
            }
        }
        return localInstance;
    }

    // массив личных сбережений:
    // 0 - рубли от 1900
    // 1 - рубли от 1922
    // 2 - рубли от 1997
    // 3 - доллары
    // 4 - фунты
    private static double[] purse;

    //set

    private Purse() {
        purse = new double[5];
    }

    public void setImperialRubles(double rubles) {
        purse[0] = rubles;
    }

    public void setSovietRubles(double rubles) {
        purse[1] = rubles;
    }

    public void setRussianRubles(double rubles) {
        purse[2] = rubles;
    }

    public void setDollars(double dollars) {
        purse[3] = dollars;
    }

    public void setPounds(double pounds) {
        purse[4] = pounds;
    }

    public void init() {
        setDollars(1000);
        setImperialRubles(0);
        setSovietRubles(0);
        setRussianRubles(0);
        setPounds(0);
    }

    //add

    public void addImperialRubles(double rubles) {
        purse[0] += rubles;
    }

    public void addSovietRubles(double rubles) {
        purse[1] += rubles;
    }

    public void addRussianRubles(double rubles) {
        purse[2] += rubles;
    }

    public void addDollars(double dollars) {
        purse[3] += dollars;
    }

    public void addPounds(double pounds) {
        purse[4] += pounds;
    }

    //automatic add

    public void add(double value, int currencyTo, long timeInMillis) {
        switch(currencyTo) {
            case Bank.CURRENCY_RUBLES:
                if(timeInMillis >= Constants.JAN_1900_1 && timeInMillis < Constants.DEC_1922_22) {
                    addImperialRubles(value);
                } else if(timeInMillis >= Constants.DEC_1922_22 && timeInMillis <= Constants.JAN_1998_1) {
                    addSovietRubles(value);
                } else if(timeInMillis > Constants.JAN_1998_1) {
                    addRussianRubles(value);
                }
                break;

            case Bank.CURRENCY_DOLLARS:
                addDollars(value);
                break;

            case Bank.CURRENCY_POUNDS:
                addPounds(value);
                break;

            default: break;
        }
    }

    //give

    private double giveMoney(int currencyIndex, long timeInMillis, double howMuch) {
        switch(currencyIndex) {
            case Bank.CURRENCY_RUBLES:
                if(timeInMillis >= Constants.JAN_1900_1 && timeInMillis < Constants.DEC_1922_22) {
                    purse[0] -= howMuch;
                } else if(timeInMillis >= Constants.DEC_1922_22 && timeInMillis <= Constants.JAN_1998_1) {
                    purse[1] -= howMuch;
                } else if(timeInMillis > Constants.JAN_1998_1) {
                    purse[2] -= howMuch;
                }
                break;

            case Bank.CURRENCY_DOLLARS:
                purse[3] -= howMuch;
                break;

            case Bank.CURRENCY_POUNDS:
                purse[4] -= howMuch;
                break;
        }
        return howMuch;
    }

    //get

    public double[] getPurse() {
        return purse;
    }

    public double getMoney(int currencyIndex, long timeInMillis) {
        double money = 0;
        switch(currencyIndex) {
            case Bank.CURRENCY_RUBLES:
                if(timeInMillis >= Constants.JAN_1900_1 && timeInMillis < Constants.DEC_1922_22) {
                    money = purse[0];
                } else if(timeInMillis >= Constants.DEC_1922_22 && timeInMillis <= Constants.JAN_1998_1) {
                    money = purse[1];
                } else if(timeInMillis > Constants.JAN_1998_1) {
                    money = purse[2];
                }
                break;

            case Bank.CURRENCY_DOLLARS:
                money = purse[3];
                break;

            case Bank.CURRENCY_POUNDS:
                money = purse[4];
                break;
        }
        return money;
    }

    /**
     * Получить все сбережения в долларах
     * @param bank: объект банка
     * @param timeInMillis: текущее время
     * @return все накопленные сбережения, преобразованные в доллары
     */
    public double getAllCash(Bank bank, long timeInMillis) {
        double cash = 0;
        //получить доллары
        cash += purse[3];
        //получить фунты
        cash += bank.change(Bank.CURRENCY_POUNDS, Bank.CURRENCY_DOLLARS, purse[4], timeInMillis);
        //получить рубли
        if(timeInMillis >= Constants.JAN_1900_1 && timeInMillis < Constants.DEC_1922_22) {
            cash += bank.change(Bank.CURRENCY_RUBLES, Bank.CURRENCY_DOLLARS, purse[0], timeInMillis);
        } else if(timeInMillis >= Constants.DEC_1922_22 && timeInMillis <= Constants.JAN_1998_1) {
            cash += bank.change(Bank.CURRENCY_RUBLES, Bank.CURRENCY_DOLLARS, purse[1], timeInMillis);
        } else if(timeInMillis > Constants.JAN_1998_1) {
            cash += bank.change(Bank.CURRENCY_RUBLES, Bank.CURRENCY_DOLLARS, purse[2], timeInMillis);
        }
        return cash;
    }

    /**
     * Выдать наличные с приоритетами доллар -> фунт -> рубль
     * @param value: сколько долларов нужно выдать
     * @param bank: объект банка
     * @param timeInMillis: текущее время
     */
    public void giveCash(double value, Bank bank, long timeInMillis) {
        //сначала взять все доллары
        double dollars = getMoney(Bank.CURRENCY_DOLLARS, timeInMillis);
        double poundsDollars = bank.change(Bank.CURRENCY_POUNDS, Bank.CURRENCY_DOLLARS,
                getMoney(Bank.CURRENCY_POUNDS, timeInMillis), timeInMillis);
        /*double rublesDollars = bank.change(Bank.CURRENCY_RUBLES, Bank.CURRENCY_DOLLARS,
                getMoney(Bank.CURRENCY_RUBLES, timeInMillis), timeInMillis);*/
        //если требуется меньше, чем есть долларов (или столько же)
        if(value <= dollars) {
            //отдать сколько нужно
            giveMoney(Bank.CURRENCY_DOLLARS, timeInMillis, value);
        } else {
            //если требуется больше, чем есть долларов
            giveMoney(Bank.CURRENCY_DOLLARS, timeInMillis, dollars);
            value -= dollars;

            //если что-то осталось, переходим к фунтам
            if(value > 0) {
                if(value <= poundsDollars) {
                    //отдать сколько нужно (переводим долларовое value в фунты и отдаём)
                    giveMoney(Bank.CURRENCY_POUNDS, timeInMillis, bank.change(Bank.CURRENCY_DOLLARS,
                            Bank.CURRENCY_POUNDS, value, timeInMillis));
                } else {
                    //если требуется больше, чем есть фунтов
                    giveMoney(Bank.CURRENCY_POUNDS, timeInMillis, getMoney(Bank.CURRENCY_POUNDS,
                            timeInMillis));
                    value -= poundsDollars;

                    //если что-то осталось, переходим к рублям
                    if(value > 0) {
                        giveMoney(Bank.CURRENCY_RUBLES, timeInMillis,
                                bank.change(Bank.CURRENCY_DOLLARS, Bank.CURRENCY_RUBLES, value,
                                        timeInMillis));
                    }
                }
            }
        }
    }

    //change

    public void change(Bank bank, int currencyTo, long timeInMillis, double howMuch) {
        int currencyFrom = bank.getCurrency();
        double changedMoney = bank.change(currencyFrom, currencyTo, giveMoney(currencyFrom,
                timeInMillis, howMuch), timeInMillis);
        add(changedMoney, currencyTo, timeInMillis);
    }

    //снимает деньги со счёта
    @Override
    public void onDepositAdd(double howMuch, int currencyIndex, long timeInMillis) {
        double money = giveMoney(currencyIndex, timeInMillis, howMuch);
        Log.d("myLogs", "deposit add caught in purse: " + money);
    }
}
