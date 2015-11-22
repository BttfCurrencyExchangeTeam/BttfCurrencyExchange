package com.tixon.backtothefutureexchange;

public interface OnDepositAddListener {
    void onDepositAdd(double howMuch, int currencyIndex, long timeInMillis);
}
