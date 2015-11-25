package com.tixon.backtothefutureexchange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

    private static final String LOG_TAG = "myLogs";
    EditText etName;
    TextView tvValue, tvInterest;
    Button bAdd;
    SeekBar seekBar;
    Spinner spinnerCurrencies;

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

    ArrayAdapter<?> currenciesAdapter;

    String[] currencies;

    private double moneyToAdd = 0, interest;

    private int currencySelectedPosition = 0;

    private static Bank bank;
    private static Purse purse;
    private static long currentTime;

    //private OnDepositAddListener onDepositAddListener;

    /*public void setOnDepositAddListener(OnDepositAddListener listener) {
        this.onDepositAddListener = listener;
    }*/

    public static AddDepositFragment newInstance(Bank mBank, Purse mPurse, long mCurrentTime, double interest) {
        bank = mBank;
        purse = mPurse;
        currentTime = mCurrentTime;
        calendar.setTimeInMillis(mCurrentTime);

        AddDepositFragment fragment = new AddDepositFragment();
        Bundle args = new Bundle();
        args.putDouble(Constants.KEY_INTEREST, interest);
        fragment.setArguments(args);
        return fragment;
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
        tvInterest = (TextView) v.findViewById(R.id.tv_interest);
        etName = (EditText) v.findViewById(R.id.add_deposit_fragment_et_name);
        spinnerCurrencies = (Spinner) v.findViewById(R.id.add_deposit_fragment_currency_selector);
        seekBar = (SeekBar) v.findViewById(R.id.add_deposit_fragment_value_seek_bar);

        currencies = getResources().getStringArray(R.array.deposit_currencies_names_before_1998);

        interest = getArguments().getDouble(Constants.KEY_INTEREST);

        tvInterest.setText(getResources().getString(R.string.deposit_interest_text) +
                " " + interest);

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

        //spinnerInterests.setSelection(0);
        spinnerCurrencies.setSelection(0);

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

        //seekBar listener

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
                if(!emptyOrSpacesOnly(etName.getText().toString())) {
                    bank.addDeposit(new Deposit(etName.getText().toString(), currentTime,
                            moneyToAdd, interest, currencySelectedPosition));
                    getActivity().getSupportFragmentManager().popBackStack();
                    //уведомляет слушателей о том, что был добавлен вклад
                    notifyDepositAdded(moneyToAdd, currencySelectedPosition,
                            calendar.getTimeInMillis());
                    onMoneyChangedListener.onMoneyChanged();
                } else {
                    Snackbar.make(v, R.string.deposit_insert_name_text, Snackbar.LENGTH_LONG)
                            .setAction(R.string.deposit_insert_name_action_text, new
                                    View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            etName.setText("");
                                            etName.requestFocus();
                                            etName.setActivated(true);
                                        }
                    }).show();
                }
            }
        });
        return v;
    }

    private void notifyDepositAdded(double howMuch, int currencyIndex, long timeInMillis) {
        for(OnDepositAddListener onDepositAddListener: onDepositAddListeners) {
            onDepositAddListener.onDepositAdd(howMuch, currencyIndex, timeInMillis);
        }
    }

    private boolean emptyOrSpacesOnly(String s) {
        while(s.contains(" ")) {
            s = s.replace(" ", "");
        }
        return s.length() == 0;
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
