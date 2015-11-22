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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddDepositFragment extends Fragment {

    EditText etName;
    TextView tvValue;
    Button bAdd;
    SeekBar seekBar;
    Spinner spinnerInterests, spinnerCurrencies;

    private static Calendar calendar = Calendar.getInstance();

    private OnMoneyChangedListener onMoneyChangedListener;

    //private List<OnMoneyChangedListener> onMoneyChangedListeners = new ArrayList<>();
    private List<OnDepositAddListener> onDepositAddListeners = new ArrayList<>();

    public void setOnMoneyChangedListener(OnMoneyChangedListener listener) {
        this.onMoneyChangedListener = listener;
    }

    /*public void addOnMoneyChangedListener(OnMoneyChangedListener listener) {
        onMoneyChangedListeners.add(listener);
    }*/
    public void addOnDepositAddListener(OnDepositAddListener listener) {
        onDepositAddListeners.add(listener);
    }

    ArrayAdapter<?> interestsAdapter, currenciesAdapter;

    String[] interests, currencies;

    private double moneyToAdd = 0;

    private int interestSelectedPosition = 0, currencySelectedPosition = 0;
    private String selectedCurrencyName;

    private static Bank bank;
    private static Purse purse;
    private static long currentTime;

    //private OnDepositAddListener onDepositAddListener;

    /*public void setOnDepositAddListener(OnDepositAddListener listener) {
        this.onDepositAddListener = listener;
    }*/

    public static AddDepositFragment newInstance(Bank mBank, Purse mPurse, long mCurrentTime) {
        bank = mBank;
        purse = mPurse;
        currentTime = mCurrentTime;
        calendar.setTimeInMillis(mCurrentTime);
        return new AddDepositFragment();
    }

    public void updateCurrentTime(long mCurrentTime) {
        currentTime = mCurrentTime;
        calendar.setTimeInMillis(mCurrentTime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_deposit, container, false);
        tvValue = (TextView) v.findViewById(R.id.add_deposit_fragment_tv_value);
        etName = (EditText) v.findViewById(R.id.add_deposit_fragment_et_name);
        spinnerInterests = (Spinner) v.findViewById(R.id.add_deposit_fragment_interest_selector);
        spinnerCurrencies = (Spinner) v.findViewById(R.id.add_deposit_fragment_currency_selector);
        seekBar = (SeekBar) v.findViewById(R.id.add_deposit_fragment_value_seek_bar);

        interests = getResources().getStringArray(R.array.interests_names);
        currencies = getResources().getStringArray(R.array.deposit_currencies_names_before_1998);

        //инициализация выбора процентных ставок
        interestsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.interests_names, android.R.layout.simple_spinner_item);
        interestsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterests.setAdapter(interestsAdapter);

        //инициализация выбора валют вклада
        String[] currenciesNames;
        if(currentTime < Constants.JAN_1998_1) {
            currenciesNames = getResources().getStringArray(R.array.deposit_currencies_names_before_1998);
        } else {
            currenciesNames = getResources().getStringArray(R.array.deposit_currencies_names_after_1998);
        }
        currenciesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, currenciesNames);
        currenciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrencies.setAdapter(currenciesAdapter);

        spinnerInterests.setSelection(0);
        spinnerCurrencies.setSelection(0);

        //добавляем onItemSelectedListener
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
                seekBar.setMax((int) purse.getMoney(currencySelectedPosition, calendar.getTimeInMillis()));
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
        //кнопка добавления вклада
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bank.addDeposit(new Deposit(etName.getText().toString(), currentTime, moneyToAdd,
                        Constants.interestValues[interestSelectedPosition], currencySelectedPosition));
                getActivity().getSupportFragmentManager().popBackStack();
                //уведомляет слушателей о том, что был добавлен вклад
                notifyDepositAdded(moneyToAdd, currencySelectedPosition,
                        calendar.getTimeInMillis());
                onMoneyChangedListener.onMoneyChanged();
            }
        });
        return v;
    }

    private void notifyDepositAdded(double howMuch, int currencyIndex, long timeInMillis) {
        for(OnDepositAddListener onDepositAddListener: onDepositAddListeners) {
            onDepositAddListener.onDepositAdd(howMuch, currencyIndex, timeInMillis);
        }
    }

    private int getCurrencyIndexByName(String currencyName) {
        String[] currencies = getResources().getStringArray(R.array.deposit_currencies_names_before_1998);
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
