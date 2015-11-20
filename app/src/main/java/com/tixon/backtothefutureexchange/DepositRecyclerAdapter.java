package com.tixon.backtothefutureexchange;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DepositRecyclerAdapter extends RecyclerView.Adapter<DepositRecyclerAdapter.DepositViewHolder> {

    private ArrayList<Deposit> deposits;
    private long currentTime;

    private static final int DEPOSIT_TYPE = 0;
    private static final int ADD_TYPE = 1;

    private OnAddDepositItemClickListener onAddDepositItemClickListener;

    private OnMoneyWithdrawListener onMoneyWithdrawListener;

    public void setOnMoneyWithdrawListener(OnMoneyWithdrawListener listener) {
        this.onMoneyWithdrawListener = listener;
    }




    public void setOnAddDepositListener(OnAddDepositItemClickListener listener) {
        this.onAddDepositItemClickListener = listener;
    }

    public DepositRecyclerAdapter(ArrayList<Deposit> deposits, long currentTime) {
        this.deposits = deposits;
        this.currentTime = currentTime;
    }

    public void updateCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public void updateDeposits(ArrayList<Deposit> deposits) {
        this.deposits.clear();
        this.deposits.addAll(deposits);
        notifyDataSetChanged();
    }

    @Override
    public DepositViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == DEPOSIT_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposit_item_layout, parent, false);
            return new DepositViewHolder(v, viewType);
        }
        if(viewType == ADD_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deposit_add_item_layout, parent, false);
            return new DepositViewHolder(v, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(DepositViewHolder holder, final int position) {
        if(holder.viewType == DEPOSIT_TYPE) {
            String name = deposits.get(position).getName();
            double interest = deposits.get(position).getInterest();
            double initValue = deposits.get(position).getInitValue();
            final double currentValue = deposits.get(position).getValue(currentTime);
            double income = currentValue - initValue;
            Date initDate = new Date(deposits.get(position).getInitTime());
            String dateFormatString = "dd.MM.yyyy 'в' hh:mm";
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);

            final Calendar currentCalendar  = Calendar.getInstance();
            currentCalendar.setTimeInMillis(currentTime);

            holder.tvName.setText(name + " (" + interest + "%)");
            holder.tvInitValue.setText(holder.context.getResources()
                    .getString(R.string.deposit_item_initial_value) + " " + String.valueOf(initValue));
            holder.tvInitTime.setText(holder.context.getResources().getString(R.string.deposit_item_initial_time) + " " + dateFormat.format(initDate));
            holder.tvCurrentValue.setText(holder.context.getResources()
                    .getString(R.string.deposit_item_current_value) + " " + String.valueOf(currentValue));
            holder.tvIncome.setText(holder.context.getResources()
                    .getString(R.string.deposit_item_income) + " " + String.valueOf(income));

            holder.actionPanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("myLogs", "action clicked");
                    onMoneyWithdrawListener.onMoneyWithdraw((int) currentValue,
                            deposits.get(position).getCurrency(),
                            currentCalendar.get(Calendar.YEAR));
                    deposits.remove(position);
                    notifyDataSetChanged();
                }
            });
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
        boolean actionPanelIsVisible = false;
        Context context;
        TextView tvName, tvInitTime, tvInitValue, tvCurrentValue, tvIncome;
        ImageView imageView;
        LinearLayout actionPanel;
        RelativeLayout frame;

        final int rightMarginOpened = 120;

        public DepositViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            context = itemView.getContext();
            itemView.setOnClickListener(this);


            frame = (RelativeLayout) itemView.findViewById(R.id.deposit_frame);

            tvName = (TextView) itemView.findViewById(R.id.deposit_name);
            tvInitTime = (TextView) itemView.findViewById(R.id.deposit_init_time);
            tvInitValue = (TextView) itemView.findViewById(R.id.deposit_initial_value);
            tvCurrentValue = (TextView) itemView.findViewById(R.id.deposit_current_value);
            tvIncome = (TextView) itemView.findViewById(R.id.deposit_income);
            imageView = (ImageView) itemView.findViewById(R.id.deposit_image);
            //actions
            actionPanel = (LinearLayout) itemView.findViewById(R.id.deposit_actions_layout);
        }

        @Override
        public void onClick(View v) {
            if(viewType == DEPOSIT_TYPE) {
                //напр. снять со счёта
                if(!actionPanelIsVisible) {
                    actionPanel.setVisibility(View.VISIBLE);
                    actionPanelIsVisible = true;
                } else {
                    actionPanel.setVisibility(View.GONE);
                    actionPanelIsVisible = false;
                }

            } if(viewType == ADD_TYPE) {
                //добавить вклад (обращение к вызову фрагмента)
                onAddDepositItemClickListener.onDepositClick();
            }
        }
    }
}
