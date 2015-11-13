package com.tixon.backtothefutureexchange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddDepositFragment extends Fragment {

    EditText etName;
    Button bAdd;

    private static Bank bank;
    private static long currentTime;

    public static AddDepositFragment newInstance(Bank mBank, long mCurrentTime) {
        bank = mBank;
        currentTime = mCurrentTime;
        return new AddDepositFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_deposit_fragment, container, false);
        etName = (EditText) v.findViewById(R.id.add_deposit_fragment_et_name);
        bAdd = (Button) v.findViewById(R.id.add_deposit_fragment_button);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bank.addDeposit(new Deposit(etName.getText().toString(), currentTime, 500, 22));
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return v;
    }
}
