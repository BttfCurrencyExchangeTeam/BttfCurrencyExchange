package com.tixon.backtothefutureexchange;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PurseItemsRecyclerAdapter extends RecyclerView.Adapter<PurseItemsRecyclerAdapter.PurseViewHolder> {

    private Context context;
    private double[] purse;
    private String[] currencyNames;

    public PurseItemsRecyclerAdapter(Context context, double[] purse) {
        this.context = context;
        this.purse = purse;

        currencyNames = context.getResources().getStringArray(R.array.purse_currency_names);
    }

    @Override
    public PurseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purse_view_item_layout,
                parent, false);
        return new PurseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PurseViewHolder holder, int position) {
        holder.tvHowMuch.setText(String.valueOf(purse[position]));
        holder.tvCurrencyName.setText(currencyNames[position]);
    }

    @Override
    public int getItemCount() {
        return purse.length;
    }

    /**
     * ViewHolder class
     */

    public class PurseViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCurrencyName, tvHowMuch;
        public ImageView ivCurrency;
        public PurseViewHolder(View itemView) {
            super(itemView);
            tvCurrencyName = (TextView) itemView.findViewById(R.id.purse_item_currency_name);
            tvHowMuch = (TextView) itemView.findViewById(R.id.purse_item_text);
            ivCurrency = (ImageView) itemView.findViewById(R.id.purse_item_currency_image);
        }
    }
}
