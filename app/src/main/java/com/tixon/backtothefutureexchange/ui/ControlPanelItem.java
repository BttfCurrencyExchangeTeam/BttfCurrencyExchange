package com.tixon.backtothefutureexchange.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.tixon.backtothefutureexchange.Constants;
import com.tixon.backtothefutureexchange.R;

import java.util.Calendar;

public class ControlPanelItem extends RelativeLayout {

    public static final int GREEN = 1;
    public static final int YELLOW = 2;
    public static final int RED = 3;

    private EditText etMonth, etDay, etYear, etHour, etMinute;

    private String[] months;

    private Calendar calendar;

    public ControlPanelItem(Context context) {
        super(context);
        init();
    }

    public ControlPanelItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ControlPanelItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.control_panel, this, true);

        months = getResources().getStringArray(R.array.months);

        etMonth = (EditText) view.findViewById(R.id.et_month);
        etDay = (EditText) view.findViewById(R.id.et_day);
        etYear = (EditText) view.findViewById(R.id.et_year);
        etHour = (EditText) view.findViewById(R.id.et_hour);
        etMinute = (EditText) view.findViewById(R.id.et_minute);

        etMonth.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        etDay.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        etYear.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        etHour.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        etMinute.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
    }

    public void setTextColor(int colorIndex) {
        switch (colorIndex) {
            case GREEN:
                //set green
                setColorForEditTexts(getResources().getColor(R.color.green));
                break;
            case YELLOW:
                //set yellow
                setColorForEditTexts(getResources().getColor(R.color.yellow));
                break;
            case RED:
                //set red
                setColorForEditTexts(getResources().getColor(R.color.red));
                break;
            default: break;
        }
    }

    public int getYear() {
        return Integer.parseInt(etYear.getText().toString());
    }

    public void setYear(int year) {
        etYear.setText(String.valueOf(year));
    }

    public void setDate(Calendar calendar) {
        this.calendar = calendar;
        etMonth.setText(months[calendar.get(Calendar.MONTH)]);
        etDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        etYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        etMinute.setText(String.valueOf(calendar.get(Calendar.MINUTE)));
        etHour.setText(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
    }

    public Calendar getDate() {
        return this.calendar;
    }

    public void setOnChangeDateClickListener(View.OnClickListener listener) {
        etMonth.setOnClickListener(listener);
        etDay.setOnClickListener(listener);
        etYear.setOnClickListener(listener);
    }

    public void setOnChangeTimeClickListener(View.OnClickListener listener) {
        etHour.setOnClickListener(listener);
        etMinute.setOnClickListener(listener);
    }

    //set colors for each editText
    private void setColorForEditTexts(int color) {
        etMonth.setTextColor(color);
        etDay.setTextColor(color);
        etYear.setTextColor(color);
        etHour.setTextColor(color);
        etMinute.setTextColor(color);
    }
}
