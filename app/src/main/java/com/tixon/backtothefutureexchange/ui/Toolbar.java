package com.tixon.backtothefutureexchange.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tixon.backtothefutureexchange.Delorean;
import com.tixon.backtothefutureexchange.OnTimeTravelListener;
import com.tixon.backtothefutureexchange.R;

public class Toolbar extends LinearLayout implements OnTimeTravelListener {

    TextView  tvPlutonium;
    TextView tvFuel;
    LinearLayout frame;

    public Toolbar(Context context) {
        super(context);
        init();
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public Toolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.layout_toolbar, this, true);

        tvPlutonium = (TextView) v.findViewById(R.id.tv_toolbar_plutonium);
        tvFuel = (TextView) v.findViewById(R.id.tv_toolbar_fuel);
        frame = (LinearLayout) v.findViewById(R.id.toolbar_frame);
    }

    public void setPlutoniumNumber(int number) {
        String s = getResources().getString(R.string.plutonium_count) + " " + number;
        tvPlutonium.setText(s);
    }

    public void setFuelNumber(double number) {
        String s = getResources().getString(R.string.fuel_count) + " " + number;
        tvFuel.setText(s);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        frame.setOnClickListener(listener);
    }

    //запускается при перемещении во времени
    @Override
    public void onTimeTraveled() {
        setPlutoniumNumber(Delorean.getInstance().getPlutonium());
        setFuelNumber(Delorean.getInstance().getFuel());
    }
}
