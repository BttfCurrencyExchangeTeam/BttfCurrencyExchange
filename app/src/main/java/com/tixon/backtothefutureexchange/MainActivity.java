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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvCurrentYear, tvMoney;
    Button bTravel, bExchange;

    private int currentYear, lastYear;
    private double cash;

    Exchange exchange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exchange = Exchange.getInstance(getResources().getStringArray(R.array.dollars),
                getResources().getStringArray(R.array.pounds));
        exchange.setYearIndex(2015);

        currentYear = 2015;
        initViews();
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

        tvCurrentYear.setText(String.valueOf(currentYear));
        cash = 1000;
        exchange.setCurrency(Exchange.CURRENCY_DOLLARS);
        tvMoney.setText(exchange.getCurrencySymbol() + String.valueOf(cash));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_TRAVEL:
                    currentYear = data
                            .getIntExtra(Constants.KEY_YEAR_DESTINATION, 2015);
                    exchange.setYearIndex(currentYear);
                    tvCurrentYear.setText(String.valueOf(currentYear));
                    Log.d("myLogs", "currentYear = " + currentYear + ", lastYear = " + lastYear);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_button_travel:
                Intent startTravelActivity = new Intent(MainActivity.this, ControlPanelActivity.class);
                startTravelActivity.putExtra(Constants.KEY_YEAR_PRESENT, currentYear);
                startTravelActivity.putExtra(Constants.KEY_YEAR_LAST, lastYear);
                lastYear = currentYear;
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
        myBuilder.setTitle("Выберите валюту");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        setArrayAdapter(arrayAdapter);

        myBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        myBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                String name = arrayAdapter.getItem(which);
                final int currencyIndex = exchange.getAvailableCurrencies()[which];
                AlertDialog.Builder innerBuilder = new AlertDialog.Builder(MainActivity.this);
                Log.d("myLogs", "from exchange = " + exchange.getCurrency() + ", currencyIndex = " + currencyIndex);
                cash = exchange.change(exchange.getCurrency(), currencyIndex, cash);
                exchange.setCurrency(currencyIndex);

                innerBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d("myLogs", "message = " + exchange.getCurrencySymbol() + String.valueOf(cash));
                        dialog.dismiss();
                    }
                });
                innerBuilder.setMessage(exchange.getCurrencySymbol() + String.valueOf(cash));
                innerBuilder.show();
            }
        });
        myBuilder.show();
    }

    private void setArrayAdapter(ArrayAdapter<String> arrayAdapter) {
        int[] currencies = exchange.getAvailableCurrencies();
        for (int currency : currencies) {
            switch (currency) {
                case Exchange.CURRENCY_DOLLARS:
                    arrayAdapter.add("Доллары");
                    break;
                case Exchange.CURRENCY_RUBLES:
                    arrayAdapter.add("Рубли");
                    break;
                case Exchange.CURRENCY_POUNDS:
                    arrayAdapter.add("Фунты");
                    break;
            }
        }
    }
}
