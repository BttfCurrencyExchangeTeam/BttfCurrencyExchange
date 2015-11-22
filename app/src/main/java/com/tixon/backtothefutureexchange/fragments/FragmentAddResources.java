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
import com.tixon.backtothefutureexchange.Purse;
import com.tixon.backtothefutureexchange.R;

public class FragmentAddResources extends Fragment {
    private TextView tvResources, tvAllCash;
    private ImageView ivLess, ivMore;
    private Button bAdd;
    private int resourceType;
    private int plutoniumCount = 0;
    private double fuelCount = 0;

    private double cash, price;

    private long currentTime;

    private String addResourcesText;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_resources, container, false);

        addResourcesText = getString(R.string.add_resources_description);

        tvResources = (TextView) v.findViewById(R.id.tv_resources);
        tvAllCash = (TextView) v.findViewById(R.id.tv_resources_all_cash);
        ivLess = (ImageView) v.findViewById(R.id.iv_resources_less);
        ivMore = (ImageView) v.findViewById(R.id.iv_resources_more);
        bAdd = (Button) v.findViewById(R.id.resources_fragment_button_add);

        Delorean delorean = Delorean.getInstance();
        Bank bank = Bank.getInstance(getResources().getStringArray(R.array.dollars),
                getResources().getStringArray(R.array.pounds));
        Purse purse = Purse.getInstance();

        resourceType = getArguments().getInt(Constants.KEY_RESOURCE_TYPE);
        currentTime = getArguments().getLong(Constants.KEY_RESOURCES_PRESENT_TIME);

        cash = purse.getAllCash(bank, currentTime);

        //установить текст
        tvResources.setText(addResourcesText + " " + 0);
        tvAllCash.setText("$" + String.valueOf(cash));

        ivLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (resourceType) {
                    case Constants.RESOURCE_TYPE_PLUTONIUM:
                        if (plutoniumCount > 0) {
                            plutoniumCount--;
                            price = Constants.PLUTONIUM_PRICE * plutoniumCount;
                            tvResources.setText(addResourcesText + " " + plutoniumCount);
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
                        plutoniumCount++;
                        price = Constants.PLUTONIUM_PRICE * plutoniumCount;
                        tvResources.setText(addResourcesText + " " + plutoniumCount);
                        break;
                    case Constants.RESOURCE_TYPE_FUEL:
                        fuelCount++;
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

                        break;
                    case Constants.RESOURCE_TYPE_FUEL:

                        break;
                }
            }
        });

        return v;
    }
}
