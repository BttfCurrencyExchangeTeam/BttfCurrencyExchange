package com.tixon.backtothefutureexchange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class AddDepositFragment extends Fragment {

    EditText etName;
    TextView tvValue;
    Button bAdd;
    SeekBar seekBar;
    Spinner spinnerInterests, spinnerCurrencies;

    private static Calendar calendar = Calendar.getInstance();

    ArrayAdapter<?> interestsAdapter, currenciesAdapter;

    String[] interests, currencies;

    private double moneyToAdd = 0;

    private int interestSelectedPosition = 0, currencySelectedPosition = 0;
    private String selectedCurrencyName;

    private static Bank bank;
    private static Purse purse;
    private static long currentTime;

    private OnDepositAddListener onDepositAddListener;

    public void setOnDepositAddListener(OnDepositAddListener listener) {
        this.onDepositAddListener = listener;
    }

    public static AddDepositFragment newInstance(Bank mBank, Purse mPurse, long mCurrentTime) {
        bank = mBank;
        purse = mPurse;
        currentTime = mCurrentTime;
        calendar.setTimeInMillis(mCurrentTime);
        return new AddDepositFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_deposit_fragment, container, false);
        tvValue = (TextView) v.findViewById(R.id.add_deposit_fragment_tv_value);
        etName = (EditText) v.findViewById(R.id.add_deposit_fragment_et_name);
        spinnerInterests = (Spinner) v.findViewById(R.id.add_deposit_fragment_interest_selector);
        spinnerCurrencies = (Spinner) v.findViewById(R.id.add_deposit_fragment_currency_selector);
        seekBar = (SeekBar) v.findViewById(R.id.add_deposit_fragment_value_seek_bar);

        interests = getResources().getStringArray(R.array.interests_names);
        currencies = getResources().getStringArray(R.array.deposit_currencies_names);

        //инициализация выбора процентных ставок
        interestsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.interests_names, android.R.layout.simple_spinner_item);
        interestsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterests.setAdapter(interestsAdapter);

        //инициализация выбора валют вклада
        currenciesAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.deposit_currencies_names,
                android.R.layout.simple_spinner_item);
        currenciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrencies.setAdapter(currenciesAdapter);

        spinnerInterests.setSelection(0);
        spinnerCurrencies.setSelection(1);

        //добавляем onItemSelectedLisntere
        spinnerInterests.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                interestSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencySelectedPosition = position;
                seekBar.setMax((int) purse.getMoney(currencySelectedPosition, calendar.get(Calendar.YEAR)));
                seekBar.setProgress(seekBar.getMax());
                tvValue.setText(String.valueOf(seekBar.getMax()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //seekbar

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvValue.setText(String.valueOf(progress));
                moneyToAdd = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bAdd = (Button) v.findViewById(R.id.add_deposit_fragment_button);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bank.addDeposit(new Deposit(etName.getText().toString(), currentTime, moneyToAdd, Constants.interestValues[interestSelectedPosition]));
                getActivity().getSupportFragmentManager().popBackStack();
                onDepositAddListener.onDepositAdd();
            }
        });
        return v;
    }

    private int getCurrencyIndexByName(String currencyName) {
        String[] currencies = getResources().getStringArray(R.array.deposit_currencies_names);
        int index = 0;
        if(currencyName.equals(currencies[0])) {
            index = Bank.CURRENCY_RUBLES;
        } else if(currencyName.equals(currencies[1])) {
            index = Bank.CURRENCY_DOLLARS;
        } else if(currencyName.equals(currencies[2])) {
            index = Bank.CURRENCY_POUNDS;
        }
        return index;
    }
}
