package com.tixon.backtothefutureexchange;

public interface OnMoneyWithdrawListener {
    void onMoneyWithdraw(int money, int currencyTo, long timeInMillis);
}
