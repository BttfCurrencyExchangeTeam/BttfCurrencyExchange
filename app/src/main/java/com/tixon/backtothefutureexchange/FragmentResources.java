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
import android.widget.Spinner;

public class FragmentResources extends Fragment {

    Spinner selector;
    ArrayAdapter<?> selectorAdapter;
    Button buttonAdd;

    Delorean delorean;
    Purse purse;

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
        selector = (Spinner) v.findViewById(R.id.resources_fragment_spinner);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //button on click listener
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedResourcePosition) {
                    case 0:
                        break;
                    case 1:
                        break;
                    default: break;
                }
            }
        });
        return v;
    }
}
