package com.tixon.backtothefutureexchange.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tixon.backtothefutureexchange.Bank;
import com.tixon.backtothefutureexchange.Constants;
import com.tixon.backtothefutureexchange.Delorean;
import com.tixon.backtothefutureexchange.OnAddResourcesListener;
import com.tixon.backtothefutureexchange.Purse;
import com.tixon.backtothefutureexchange.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentAddResources extends Fragment {
    TextView tvResources, tvAllCash, tvPrice, tvError;
    ImageView ivLess, ivMore;
    Button bAdd;
    private int resourceType;
    private int plutoniumCount = 0;
    private double fuelCount = 0;

    private double cash, price;

    long currentTime;

    private String addResourcesText;

    /*private OnAddResourcesListener onAddResourcesListener;*/

    private List<OnAddResourcesListener> onAddResourcesListeners = new ArrayList<>();

    /*public void setOnAddResourcesListener(OnAddResourcesListener listener) {
        this.onAddResourcesListener = listener;
    }*/

    public void addOnAddResourcesListener(OnAddResourcesListener listener) {
        onAddResourcesListeners.add(listener);
    }

    public void notifyOnPlutoniumAdded(int count, double price) {
        for(OnAddResourcesListener listener: onAddResourcesListeners) {
            listener.onAddPlutonium(count, price);
        }
    }

    public void notifyOnFuelAdded(double count, double price) {
        for(OnAddResourcesListener listener: onAddResourcesListeners) {
            listener.onAddFuel(count);
        }
    }

    public static FragmentAddResources newInstance(int resourceType, long currentTimeInMillis) {
        FragmentAddResources fragment = new FragmentAddResources();
        Bundle args = new Bundle();
        args.putLong(Constants.KEY_RESOURCES_PRESENT_TIME, currentTimeInMillis);
        args.putInt(Constants.KEY_RESOURCE_TYPE, resourceType);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_resources, container, false);

        addResourcesText = getString(R.string.add_resources_description);

        price = 0;

        tvResources = (TextView) v.findViewById(R.id.tv_resources);
        tvAllCash = (TextView) v.findViewById(R.id.tv_resources_all_cash);
        tvPrice = (TextView) v.findViewById(R.id.tv_resources_price);
        tvError = (TextView) v.findViewById(R.id.tv_resources_error);

        ivLess = (ImageView) v.findViewById(R.id.iv_resources_less);
        ivMore = (ImageView) v.findViewById(R.id.iv_resources_more);
        bAdd = (Button) v.findViewById(R.id.resources_fragment_button_add);

        if(plutoniumCount == 0) {
            bAdd.setEnabled(false);
        }

        final Delorean delorean = Delorean.getInstance();
        final Bank bank = Bank.getInstance(getResources().getStringArray(R.array.dollars),
                getResources().getStringArray(R.array.pounds));
        final Purse purse = Purse.getInstance();

        resourceType = getArguments().getInt(Constants.KEY_RESOURCE_TYPE);
        currentTime = getArguments().getLong(Constants.KEY_RESOURCES_PRESENT_TIME);

        cash = purse.getAllCash(bank, currentTime);

        //установить текст
        tvResources.setText(addResourcesText + " " + 0);
        tvAllCash.setText(getResources().getString(R.string.resources_all_cash) +
                " $" + formatString(cash));

        ivLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (resourceType) {
                    case Constants.RESOURCE_TYPE_PLUTONIUM:
                        if (plutoniumCount > 0) {
                            plutoniumCount--;
                            price = Constants.PLUTONIUM_PRICE * plutoniumCount;
                            if (!(price > cash)) {
                                tvError.setVisibility(View.INVISIBLE);
                            }
                            tvResources.setText(addResourcesText + " " + plutoniumCount);
                            tvPrice.setText(getResources().getString(R.string.resources_price) +
                                    " $" + price);
                            if(plutoniumCount == 0) {
                                bAdd.setEnabled(false);
                            }
                        }
                        break;
                    case Constants.RESOURCE_TYPE_FUEL:
                        if (fuelCount > 0) {
                            fuelCount -= 0.1;
                            tvResources.setText(addResourcesText + " " + fuelCount);
                        }
                        break;
                }
            }
        });

        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (resourceType) {
                    case Constants.RESOURCE_TYPE_PLUTONIUM:
                        bAdd.setEnabled(true);
                        if (price > cash) {
                            tvError.setVisibility(View.VISIBLE);
                            bAdd.setEnabled(false);
                        } else {
                            plutoniumCount++;
                            price = Constants.PLUTONIUM_PRICE * plutoniumCount;
                            tvResources.setText(addResourcesText + " " + plutoniumCount);
                            tvPrice.setText(getResources().getString(R.string.resources_price) +
                                    " $" + price);
                            tvError.setVisibility(View.INVISIBLE);
                            if (price > cash) {
                                tvError.setVisibility(View.VISIBLE);
                                bAdd.setEnabled(false);
                            }
                        }
                        break;
                    case Constants.RESOURCE_TYPE_FUEL:
                        fuelCount += 0.1;
                        tvResources.setText(addResourcesText + " " + fuelCount);
                        break;
                }
            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (resourceType) {
                    case Constants.RESOURCE_TYPE_PLUTONIUM:
                        //уведомить слушателей (MainActivity, Delorean) о добавлении плутония
                        notifyOnPlutoniumAdded(plutoniumCount, price);
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                    case Constants.RESOURCE_TYPE_FUEL:

                        break;
                }
            }
        });



        return v;
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
