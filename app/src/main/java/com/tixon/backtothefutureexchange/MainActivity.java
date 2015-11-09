package com.tixon.backtothefutureexchange;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    TextView tvCurrentYear, tvMoney;
    Button bTravel, bExchange;

    Calendar calendar;

    private Calendar calendarPresent, calendarLast;

    private OnAddPlutoniumListener onAddPlutoniumListener;

    private double cash;
    Purse purse;

    private Delorean delorean;
    private Bank bank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();

        calendarPresent = Calendar.getInstance();
        calendarLast = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

        delorean = Delorean.getDelorean();

        bank = Bank.getInstance(getResources().getStringArray(R.array.dollars),
                getResources().getStringArray(R.array.pounds));
        bank.setYearIndex(calendar.get(Calendar.YEAR));

        calendarPresent.setTimeInMillis(calendar.getTimeInMillis());
        initViews();

        cash = 1000;

        purse = Purse.getInstance();
        purse.setDollars(1000);

        bank.setCurrency(Bank.CURRENCY_DOLLARS);
        tvMoney.setText(bank.getCurrencySymbol() + String.valueOf(cash));
    }

    public void setOnAddPlutoniumListener(OnAddPlutoniumListener onAddPlutoniumListener) {
        this.onAddPlutoniumListener = onAddPlutoniumListener;
    }

    private void initViews() {
        bTravel = (Button) findViewById(R.id.main_activity_button_travel);
        bExchange = (Button) findViewById(R.id.main_activity_button_exchange);

        bTravel.setOnClickListener(this);
        bExchange.setOnClickListener(this);

        tvCurrentYear = (TextView) findViewById(R.id.main_activity_tv_year);
        tvMoney = (TextView) findViewById(R.id.main_activity_tv_money);
        tvCurrentYear.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                Constants.TYPEFACE_DIGITS));
        tvMoney.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                Constants.TYPEFACE_DIGITS));

        tvCurrentYear.setText(String.valueOf(calendarPresent.get(Calendar.YEAR)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_TRAVEL:
                    calendarPresent.setTimeInMillis(data.getLongExtra(Constants.KEY_TIME_DESTINATION, System.currentTimeMillis()));
                    bank.setYearIndex(calendarPresent.get(Calendar.YEAR));
                    tvCurrentYear.setText(String.valueOf(calendarPresent.get(Calendar.YEAR)));
                    Log.d("myLogs", "currentYear = " + calendarPresent.get(Calendar.YEAR) +
                            ", lastYear = " + calendarLast.get(Calendar.YEAR));
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_button_travel:
                Intent startTravelActivity = new Intent(MainActivity.this, ControlPanelActivity.class);
                startTravelActivity.putExtra(Constants.KEY_TIME_PRESENT, calendarPresent.getTimeInMillis());
                startTravelActivity.putExtra(Constants.KEY_TIME_LAST, calendarLast.getTimeInMillis());
                calendarLast.setTimeInMillis(calendarPresent.getTimeInMillis());

                startActivityForResult(startTravelActivity,
                        Constants.REQUEST_CODE_TRAVEL);
                break;
            case R.id.main_activity_button_exchange:
                showExchangeDialog();
                break;
            default:
                break;
        }
    }

    public void showExchangeDialog() {
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(MainActivity.this);
        myBuilder.setTitle(getResources().getString(R.string.dialog_choose_currency_title));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        setArrayAdapter(arrayAdapter);

        myBuilder.setNegativeButton(getResources()
                .getString(R.string.dialog_choose_currency_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        myBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int currencyIndex = bank.getAvailableCurrencies()[which];
                AlertDialog.Builder innerBuilder = new AlertDialog.Builder(MainActivity.this);
                Log.d("myLogs", "from bank = " + bank.getCurrency() + ", currencyIndex = " + currencyIndex);
                //cash = bank.change(bank.getCurrency(), currencyIndex, cash);
                //purse.add(bank.change(bank.getCurrency(), currencyIndex, cash),
                //        currencyIndex, calendarPresent.get(Calendar.YEAR));
                purse.change(bank, currencyIndex, calendarPresent.get(Calendar.YEAR), 900);

                bank.setCurrency(currencyIndex);

                innerBuilder.setPositiveButton(getResources()
                        .getString(R.string.dialog_choose_currency_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        tvMoney.setText(bank.getCurrencySymbol() + String.valueOf((int) purse
                                .getMoney(bank.getCurrency(), calendarPresent.get(Calendar.YEAR))));


                        Log.d("myLogs", "message = " + bank.getCurrencySymbol() +
                                String.valueOf(purse.getMoney(bank.getCurrency(),
                                        calendarPresent.get(Calendar.YEAR))));

                        dialog.dismiss();
                    }
                });
                innerBuilder.setMessage(bank.getCurrencySymbol() + String.valueOf(bank.getExchangeRate()));
                innerBuilder.show();
            }
        });
        myBuilder.setCancelable(false);
        myBuilder.show();
    }

    private void setArrayAdapter(ArrayAdapter<String> arrayAdapter) {
        int[] currencies = bank.getAvailableCurrencies();
        for (int currency : currencies) {
            switch (currency) {
                case Bank.CURRENCY_DOLLARS:
                    arrayAdapter.add(getResources().getString(R.string.currency_dollars));
                    break;
                case Bank.CURRENCY_RUBLES:
                    arrayAdapter.add(getResources().getString(R.string.currency_rubles));
                    break;
                case Bank.CURRENCY_POUNDS:
                    arrayAdapter.add(getResources().getString(R.string.currency_pounds));
                    break;
            }
        }
    }
    //вычесть из баланса долларовый эквивалент цены за количество плутония
    //cash -= count * bank.change(Bank.CURRENCY_DOLLARS, bank.getCurrency(), 10000);
}
