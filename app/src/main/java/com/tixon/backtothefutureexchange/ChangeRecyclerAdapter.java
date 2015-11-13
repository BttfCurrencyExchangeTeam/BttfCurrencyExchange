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

public class ChangeRecyclerAdapter extends RecyclerView.Adapter<ChangeRecyclerAdapter.ChangeViewHolder> {

    private ArrayList<String> currencies;
    private Context context;
    private FragmentChange fragment;
    private int selectedPosition = 0;

    private int currencyIndex;
    private Purse purse;
    private int year;

    private int[] availableCurrencies;

    private OnItemCheckedListener onItemCheckedListener;

    public void setOnItemCheckedListener(OnItemCheckedListener listener) {
        this.onItemCheckedListener = listener;
    }

    /**
     * Constructor for purseAdapter
     * @param availableCurrencies: get from Bank object when creating the purseAdapter
     */
    public ChangeRecyclerAdapter(FragmentChange fragment, int[] availableCurrencies, int currencyIndex, int year, Purse purse) {
        currencies = new ArrayList<>();
        this.context = fragment.getContext();
        this.fragment = fragment;
        this.year = year;
        this.availableCurrencies = availableCurrencies;
        this.currencyIndex = currencyIndex;
        this.purse = purse;
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

        setFragmentData(fragment);
        fragment.setCurrencyTo(availableCurrencies[selectedPosition]);
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
            holder.frame.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        } else {
            holder.frame.setBackgroundColor(context.getResources()
                    .getColor(android.R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return currencies.size();
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
            Log.d("myLogs", "year = " + year + ", money = " + purse.getMoney(currencyIndex, year));
            onItemCheckedListener.onChange(availableCurrencies[position]);
            notifyDataSetChanged();
        }
    }

    //устанавливает в FragmentChange для seekBar и textView значения, полученные здесь
    public void setFragmentData(FragmentChange fragment) {
        fragment.seekBar.setMax((int) purse.getMoney(currencyIndex, year));
        fragment.seekBar.setProgress(fragment.seekBar.getMax());
        fragment.moneyToExchange = ((int) purse.getMoney(currencyIndex, year));
        fragment.textView.setText(String.valueOf(purse.getMoney(currencyIndex, year)));
    }
}