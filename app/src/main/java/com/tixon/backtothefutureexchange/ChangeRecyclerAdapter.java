package com.tixon.backtothefutureexchange;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChangeRecyclerAdapter extends RecyclerView
        .Adapter<ChangeRecyclerAdapter.ChangeViewHolder> implements OnMoneyRangeChangedListener {

    private static final String LOG_TAG = "myLogs";
    private ArrayList<String> currencies;
    private Context context;
    FragmentChange fragment;
    private int selectedPosition = 0;

    private int currencyIndex;
    private Purse purse;
    private long timeInMillis;

    private int[] availableCurrencies;

    private OnItemCheckedListener onItemCheckedListener;

    public void setOnItemCheckedListener(OnItemCheckedListener listener) {
        this.onItemCheckedListener = listener;
    }

    /**
     * Constructor for purseAdapter
     * @param availableCurrencies: get from Bank object when creating the purseAdapter
     */
    public ChangeRecyclerAdapter(FragmentChange fragment, int[] availableCurrencies, int currencyIndex, long timeInMillis, Purse purse) {
        currencies = new ArrayList<>();
        this.context = fragment.getContext();
        this.fragment = fragment;
        this.timeInMillis = timeInMillis;
        this.availableCurrencies = availableCurrencies;
        this.currencyIndex = currencyIndex;
        this.purse = purse;
        setAvailableCurrencies(availableCurrencies);

        setFragmentData(fragment);
        fragment.setCurrencyTo(availableCurrencies[selectedPosition]);
    }

    public void setAvailableCurrencies(int[] availableCurrencies) {
        currencies.clear();
        for(int currency: availableCurrencies) {
            switch (currency) {
                case Bank.CURRENCY_RUBLES:
                    currencies.add(context.getResources().getString(R.string.currency_rubles));
                    break;
                case Bank.CURRENCY_DOLLARS:
                    currencies.add(context.getResources().getString(R.string.currency_dollars));
                    break;
                case Bank.CURRENCY_POUNDS:
                    currencies.add(context.getResources().getString(R.string.currency_pounds));
                    break;
                default: break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ChangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.currencies_recycler_item,
                parent, false);
        return new ChangeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChangeViewHolder holder, int position) {
        holder.currencyName.setText(currencies.get(position));
        holder.position = position;
        //setting selection frame
        if(position == selectedPosition) {
            holder.frame.setBackgroundResource(R.color.yellow);
        } else {
            holder.frame.setBackgroundResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    //OnMoneyRangeChangedListener method
    @Override
    public void onMoneyRangeChanged(int moneyToExchange, Bank bank, long timeInMillis) {
        for(int i = 0; i < currencies.size(); i++) {
            String currency = currencies.get(i);
            if(currency.contains(":")) {
                currency = currency.substring(0, currency.indexOf(":"));
            }
            currency += ": ";
            currency += String.valueOf(bank.change(bank.getCurrency(), availableCurrencies[i], moneyToExchange, timeInMillis));
            int dotIndex = currency.indexOf(".");

            try {
                currency = currency.substring(0, dotIndex + 3);
            } catch (Exception e) {
                Log.e(LOG_TAG, "error: onMoneyRangeChanged, currency substring: " + e.toString());
                e.printStackTrace();
            }

            currencies.set(i, currency);
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class
     */
    public class ChangeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView currencyName;
        public ImageView currencyImage;
        public RelativeLayout frame;
        public int position;

        public ChangeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            frame = (RelativeLayout) itemView.findViewById(R.id.currencies_recycler_item_frame);
            currencyName = (TextView) itemView.findViewById(R.id.currencies_recycler_item_textView);
            currencyImage = (ImageView) itemView.findViewById(R.id.currencies_recycler_item_imageView);
        }

        @Override
        public void onClick(View v) {
            Log.d("myLogs", "change item clicked: " + position + ", currency = " + currencies.get(position));
            selectedPosition = position;
            Log.d("myLogs", "timeInMillis = " + timeInMillis + ", money = " + purse.getMoney(currencyIndex, timeInMillis));
            onItemCheckedListener.onChange(availableCurrencies[position]);
            notifyDataSetChanged();
        }
    }

    //устанавливает в FragmentChange для seekBar и textView значения, полученные здесь
    public void setFragmentData(FragmentChange fragment) {
        fragment.seekBar.setMax((int) purse.getMoney(currencyIndex, timeInMillis));
        fragment.seekBar.setProgress(fragment.seekBar.getMax());
        fragment.moneyToExchange = ((int) purse.getMoney(currencyIndex, timeInMillis));
        fragment.textView.setText(String.valueOf(purse.getMoney(currencyIndex, timeInMillis)));
    }
}
