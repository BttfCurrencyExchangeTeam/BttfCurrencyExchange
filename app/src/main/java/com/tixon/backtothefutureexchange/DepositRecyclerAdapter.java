package com.tixon.backtothefutureexchange;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DepositRecyclerAdapter extends RecyclerView.Adapter<DepositRecyclerAdapter.DepositViewHolder> {

    private ArrayList<Deposit> deposits;
    private long currentTime;

    private static final int DEPOSIT_TYPE = 0;
    private static final int ADD_TYPE = 1;

    private OnAddDepositItemClickListener onAddDepositItemClickListener;

    public void setOnAddDepositListener(OnAddDepositItemClickListener listener) {
        this.onAddDepositItemClickListener = listener;
    }

    public DepositRecyclerAdapter(ArrayList<Deposit> deposits, long currentTime) {
        this.deposits = deposits;
        this.currentTime = currentTime;
    }

    @Override
    public DepositViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == DEPOSIT_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposits_item_layout, parent, false);
            return new DepositViewHolder(v, viewType);
        }
        if(viewType == ADD_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposit_add_item_layout, parent, false);
            return new DepositViewHolder(v, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(DepositViewHolder holder, int position) {
        if(holder.viewType == DEPOSIT_TYPE) {
            String name = deposits.get(position).getName();
            double interest = deposits.get(position).getInterest();
            double initValue = deposits.get(position).getInitValue();
            double currentValue = deposits.get(position).getValue(currentTime);
            double income = currentValue - initValue;

            holder.tvName.setText(name + " (" + interest + "%)");
            holder.tvInitValue.setText(String.valueOf(initValue));
            holder.tvCurrentValue.setText(String.valueOf(currentValue));
            holder.tvIncome.setText(String.valueOf(income));
        }
    }

    @Override
    public int getItemCount() {
        return deposits.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() - 1) {
            return ADD_TYPE;
        }
        return DEPOSIT_TYPE;
    }

    public class DepositViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
        protected int viewType;
        TextView tvName, tvInitTime, tvInitValue, tvCurrentValue, tvIncome;
        ImageView imageView;

        public DepositViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            itemView.setOnClickListener(this);

            tvName = (TextView) itemView.findViewById(R.id.deposit_name);
            tvInitTime = (TextView) itemView.findViewById(R.id.deposit_init_time);
            tvInitValue = (TextView) itemView.findViewById(R.id.deposit_initial_value);
            tvCurrentValue = (TextView) itemView.findViewById(R.id.deposit_current_value);
            tvIncome = (TextView) itemView.findViewById(R.id.deposit_income);
            imageView = (ImageView) itemView.findViewById(R.id.deposit_image);
        }

        @Override
        public void onClick(View v) {
            if(viewType == DEPOSIT_TYPE) {
                //напр. снять со счёта
            } if(viewType == ADD_TYPE) {
                //добавить вклад (обращение к вызову фрагмента)
                onAddDepositItemClickListener.onDepositClick();
            }
        }
    }
}
