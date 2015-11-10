package com.tixon.backtothefutureexchange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;

public class FragmentChange extends Fragment implements OnItemCheckedListener {
    Button buttonChange;
    SeekBar seekBar;
    TextView textView;
    RecyclerView recyclerView;
    ChangeRecyclerAdapter adapter;
    LinearLayoutManager layoutManager;

    static Bank bank;
    static Purse purse;
    static Calendar calendar;

    private int moneyToExchange;
    private int currencyTo;

    private OnMoneyChangedListener onMoneyChangedListener;

    public void setOnMoneyChangedListener(OnMoneyChangedListener listener) {
        this.onMoneyChangedListener = listener;
    }

    public static FragmentChange newInstance(Bank mBank, Purse mPurse, Calendar mCalendar) {
        bank = mBank;
        purse = mPurse;
        calendar = mCalendar;
        //Bundle args = new Bundle();
        return new FragmentChange();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change, container, false);
        buttonChange = (Button) v.findViewById(R.id.fragment_change_button);
        seekBar = (SeekBar) v.findViewById(R.id.fragment_change_seek_bar);
        textView = (TextView) v.findViewById(R.id.fragment_change_text_view);
        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_change_recycler_view);
        adapter = new ChangeRecyclerAdapter(this, bank.getAvailableCurrencies(),
                bank.getCurrency(), calendar.get(Calendar.YEAR), purse);
        adapter.setOnItemCheckedListener(this); //назначение слушателя изменения валюты

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.valueOf(progress));
                moneyToExchange = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purse.change(bank, currencyTo, calendar.get(Calendar.YEAR),
                        (double) moneyToExchange);
                bank.setCurrency(currencyTo);
                onMoneyChangedListener.onMoneyChanged();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return v;
    }

    @Override
    public void onChange(int currencyTo) {
        this.currencyTo = currencyTo;
        Log.d("myLogs", "FragmentChange, onChangeListener: currencyTo = " + currencyTo);
    }

    public void setCurrencyTo(int currencyTo) {
        this.currencyTo = currencyTo;
    }
}
