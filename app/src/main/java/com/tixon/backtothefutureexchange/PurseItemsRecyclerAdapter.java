package com.tixon.backtothefutureexchange;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PurseItemsRecyclerAdapter extends RecyclerView.Adapter<PurseItemsRecyclerAdapter.PurseViewHolder> {

    private Context context;
    private double[] purse;
    private String[] currencyNames;
    private int selectedPosition = 0;
    private long currentTimeInMillis;

    private List<OnCurrencyChangedListener> onCurrencyChangedListeners = new ArrayList<>();

    //уведомление слушателей, что была изменена валюта в этом адаптере
    public void addOnCurrencyChangedListener(OnCurrencyChangedListener listener) {
        onCurrencyChangedListeners.add(listener);
    }

    public void notifyWhenCurrencyChanged(int currencyTo) {
        for(OnCurrencyChangedListener listener: onCurrencyChangedListeners) {
            try {
                listener.onCurrencyChanged(currencyTo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Constructor
    public PurseItemsRecyclerAdapter(Context context, double[] purse, long currentTimeInMillis) {
        this.context = context;
        this.purse = purse;
        this.currentTimeInMillis = currentTimeInMillis;

        currencyNames = context.getResources().getStringArray(R.array.purse_currency_names);
    }

    //force update currentTime
    public void updateTime(long currentTimeInMillis) {
        this.currentTimeInMillis = currentTimeInMillis;
        notifyDataSetChanged();
    }

    @Override
    public PurseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purse_view_item_layout,
                parent, false);
        return new PurseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PurseViewHolder holder, int position) {
        holder.tvHowMuch.setText(formatString(purse[position]));
        holder.tvCurrencyName.setText(currencyNames[position]);
        holder.ivCurrency.setImageResource(getImageResource(position));
        holder.position = position;

        if(position == selectedPosition) {
            holder.layoutFrame.setBackgroundResource(R.color.selected_currency_item_color);
        } else {
            holder.layoutFrame.setBackgroundResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return purse.length;
    }

    /**
     * ViewHolder class
     */

    public class PurseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvCurrencyName, tvHowMuch;
        public ImageView ivCurrency;
        public RelativeLayout layoutFrame;

        public int position;

        public PurseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvCurrencyName = (TextView) itemView.findViewById(R.id.purse_item_currency_name);
            tvHowMuch = (TextView) itemView.findViewById(R.id.purse_item_text);
            ivCurrency = (ImageView) itemView.findViewById(R.id.purse_item_currency_image);
            layoutFrame = (RelativeLayout) itemView.findViewById(R.id.purse_item_frame);
        }

        @Override
        public void onClick(View v) {
            innerSelectCurrency(position, currentTimeInMillis);
            notifyDataSetChanged();
        }
    }

    private int getImageResource(int position) {
        int resource = 0;
        switch (position) {
            case Purse.IMPERIAL_RUBLES: resource = R.drawable.flag_ru; break;
            case Purse.SOVIET_RUBLES: resource = R.drawable.flag_ussr; break;
            case Purse.RF_RUBLES: resource = R.drawable.flag_ru; break;
            case Purse.DOLLARS: resource = R.drawable.flag_us; break;
            case Purse.POUNDS: resource = R.drawable.flag_uk; break;
        }
        return resource;
    }

    private void innerSelectCurrency(int position, long timeInMillis) {
        int currencyIndex;
        if(position >= 0 && position <= 2) {
            //positions for rubles (0, 1, 2)
            if(timeInMillis >= Constants.JAN_1998_1) {
                selectedPosition = Purse.RF_RUBLES;
            } else if((timeInMillis >= Constants.DEC_1922_22) && (timeInMillis < Constants.JAN_1998_1)) {
                selectedPosition = Purse.SOVIET_RUBLES;
            } else if(timeInMillis < Constants.DEC_1922_22) {
                selectedPosition = Purse.IMPERIAL_RUBLES;
            }
            currencyIndex = Bank.CURRENCY_RUBLES;
            notifyWhenCurrencyChanged(currencyIndex); //notify
        } else if(position == 3) {
            selectedPosition = 3;
            currencyIndex = Bank.CURRENCY_DOLLARS;
            notifyWhenCurrencyChanged(currencyIndex); //notify
        } else if(position == 4) {
            selectedPosition = 4;
            currencyIndex = Bank.CURRENCY_POUNDS;
            notifyWhenCurrencyChanged(currencyIndex); //notify
        }
    }

    public void selectCurrency(int currency, long timeInMillis) {
        switch (currency) {
            case Bank.CURRENCY_RUBLES:
                if(timeInMillis >= Constants.JAN_1998_1) {
                    selectedPosition = Purse.RF_RUBLES;
                } else if((timeInMillis >= Constants.DEC_1922_22) && (timeInMillis < Constants.JAN_1998_1)) {
                    selectedPosition = Purse.SOVIET_RUBLES;
                } else if(timeInMillis < Constants.DEC_1922_22) {
                    selectedPosition = Purse.IMPERIAL_RUBLES;
                }
                break;
            case Bank.CURRENCY_DOLLARS:
                selectedPosition = 3;
                break;
            case Bank.CURRENCY_POUNDS:
                selectedPosition = 4;
                break;
            default: break;
        }
        notifyWhenCurrencyChanged(currency);
        notifyDataSetChanged();
    }

    private String formatString(double number) {
        String sNumber = String.valueOf(number);
        int dotIndex = sNumber.indexOf(".");
        int digitsAfterDot = 2;
        try {
            sNumber = sNumber.substring(0, dotIndex + digitsAfterDot + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sNumber;
    }
}
