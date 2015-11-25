package com.tixon.backtothefutureexchange.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tixon.backtothefutureexchange.Constants;
import com.tixon.backtothefutureexchange.R;

import java.util.Calendar;

public class ControlPanelItem extends RelativeLayout {
    public static final int DESTINATION_TIME = 0;
    public static final int PRESENT_TIME = 1;
    public static final int LAST_TIME_DEPARTED = 2;

    private FrameLayout fieldDate, fieldTime;
    private EditText etMonth, etDay, etYear, etHour, etMinute;
    private TextView tvPanelName;
    private String[] months;
    private String[] timeTypes;
    private int[] colors;

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
        timeTypes = getResources().getStringArray(R.array.time_types);
        colors = getResources().getIntArray(R.array.panel_colors);

        fieldDate = (FrameLayout) view.findViewById(R.id.field_date);
        fieldTime = (FrameLayout) view.findViewById(R.id.field_time);

        etMonth = (EditText) view.findViewById(R.id.et_month);
        etDay = (EditText) view.findViewById(R.id.et_day);
        etYear = (EditText) view.findViewById(R.id.et_year);
        etHour = (EditText) view.findViewById(R.id.et_hour);
        etMinute = (EditText) view.findViewById(R.id.et_minute);

        tvPanelName = (TextView) view.findViewById(R.id.control_panel_tv_panel_name);

        etMonth.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        etDay.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        etYear.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        etHour.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        etMinute.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
    }

    /**
     *
     * @param timeType: номер панели (настоящее, предполагаемое, последнее посещённое время)
     */
    public void setPanelType(int timeType) {
        tvPanelName.setText(timeTypes[timeType]);
        setColorForEditTexts(colors[timeType]);
    }

    public void setDate(Calendar calendar) {
        this.calendar = calendar;
        etMonth.setText(months[calendar.get(Calendar.MONTH)]);
        etDay.setText(formatNumber(calendar.get(Calendar.DAY_OF_MONTH)));
        etYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        etMinute.setText(formatNumber(calendar.get(Calendar.MINUTE)));
        etHour.setText(formatNumber(calendar.get(Calendar.HOUR_OF_DAY)));
    }

    private String formatNumber(int number) {
        StringBuilder sb = new StringBuilder();
        //если число однозначное
        if(number / 10 == 0) {
            sb.append(0); //в самом начале ставим 0
        }
        //результат: 01, 02, 23, 30
        sb.append(number);
        return sb.toString();
    }

    public Calendar getDate() {
        return this.calendar;
    }

    public void setOnChangeDateClickListener(View.OnClickListener listener) {
        fieldDate.setOnClickListener(listener);
    }

    public void setOnChangeTimeClickListener(View.OnClickListener listener) {
        fieldTime.setOnClickListener(listener);
    }

    //установка цвета для каждого поля editText
    private void setColorForEditTexts(int color) {
        etMonth.setTextColor(color);
        etDay.setTextColor(color);
        etYear.setTextColor(color);
        etHour.setTextColor(color);
        etMinute.setTextColor(color);
    }

    /**
     * Запускает посекундный отсчёт для панели времени
     * используется только в панелях настоящего времени
     */
    public void startTimeRoll() {
        Calendar systemCalendar = Calendar.getInstance();
        systemCalendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, systemCalendar.get(Calendar.SECOND));
        final Handler h = new Handler();
        Thread threadTimeRoll = new Thread(new Runnable() {
            @Override
            public void run() {
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addSecond(calendar);
                        setDate(calendar);
                        //Log.d("myLogs", "seconds = " + calendar.get(Calendar.SECOND));
                        h.postDelayed(this, 1000);
                    }
                }, 1000);
            }
        });
        threadTimeRoll.start();
    }

    private void addSecond(Calendar calendar) {
        calendar.add(Calendar.SECOND, 1);
    }
}
