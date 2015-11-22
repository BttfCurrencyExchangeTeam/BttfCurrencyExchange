package com.tixon.backtothefutureexchange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentResources extends Fragment implements View.OnClickListener {

    Spinner selector;
    ArrayAdapter<?> selectorAdapter;
    Button buttonAdd;
    ImageView ivLess, ivMore;
    TextView tv;

    Delorean delorean;
    Purse purse;

    private int plutoniumNumber = 0, fuelNumber = 0;

    private int selectedResourcePosition;

    public FragmentResources() {
        delorean = Delorean.getInstance();
        purse = Purse.getInstance();
    }

    public static FragmentResources newInstance() {
        FragmentResources fragmentResources = new FragmentResources();
        return fragmentResources;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_resources, container, false);
        buttonAdd = (Button) v.findViewById(R.id.resources_fragment_button_add);
        ivLess = (ImageView) v.findViewById(R.id.iv_resources_less);
        ivMore = (ImageView) v.findViewById(R.id.iv_resources_more);
        tv = (TextView) v.findViewById(R.id.tv_resources);
        //create spinner adapter
        selectorAdapter = ArrayAdapter.createFromResource(container.getContext(), R.array.resources_names, android.R.layout.simple_spinner_item);
        selectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set spinner adapter
        selector.setAdapter(selectorAdapter);
        //set 0 element checked
        selector.setSelection(0);
        //set spinner item selected listener
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedResourcePosition = position;
                switch (position) {
                    case 0:
                        tv.setText("Количество плутония: " + plutoniumNumber);
                        break;
                    case 1:
                        tv.setText("Количество топлива: " + fuelNumber);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ivLess.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resources_fragment_button_add:

                switch (selectedResourcePosition) {
                    case 0:
                        break;
                    case 1:
                        break;
                    default: break;
                }

                break;
            case R.id.iv_resources_less:
                switch (selectedResourcePosition) {
                    case 0:
                        plutoniumNumber--;
                        tv.setText("Количество плутония: " + plutoniumNumber);
                        break;
                    case 1:
                        fuelNumber--;
                        tv.setText("Количество топлива: " + fuelNumber);
                        break;
                    default: break;
                }
                break;
            case R.id.iv_resources_more:
                switch (selectedResourcePosition) {
                    case 0:
                        plutoniumNumber++;
                        tv.setText("Количество плутония: " + plutoniumNumber);
                        break;
                    case 1:
                        fuelNumber++;
                        tv.setText("Количество топлива: " + fuelNumber);
                        break;
                    default: break;
                }
                break;
        }
    }
}
