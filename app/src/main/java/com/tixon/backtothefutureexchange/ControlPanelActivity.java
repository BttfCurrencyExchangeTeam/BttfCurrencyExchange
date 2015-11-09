package com.tixon.backtothefutureexchange;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.tixon.backtothefutureexchange.ui.ControlPanelItem;

import java.util.Calendar;

public class ControlPanelActivity extends AppCompatActivity implements
        View.OnClickListener {

    private ControlPanelItem destinationTimePanel, presentTimePanel, lastTimeDepartedPanel;
    private Button bTravel;

    private Calendar calendarPresent, calendarLast;

    private Delorean delorean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_panel_activity_layout);

        calendarPresent = Calendar.getInstance();
        calendarLast = Calendar.getInstance();

        delorean = Delorean.getDelorean();

        Intent fromMainActivity = getIntent();

        calendarPresent.setTimeInMillis(fromMainActivity.getLongExtra(Constants.KEY_TIME_PRESENT, System.currentTimeMillis()));
        calendarLast.setTimeInMillis(fromMainActivity.getLongExtra(Constants.KEY_TIME_LAST, System.currentTimeMillis()));
        initPanel();
    }

    private void initPanel() {
        destinationTimePanel = (ControlPanelItem) findViewById(R.id.destination_time_panel);
        presentTimePanel = (ControlPanelItem) findViewById(R.id.present_time_panel);
        lastTimeDepartedPanel = (ControlPanelItem) findViewById(R.id.last_time_departed_panel);

        bTravel = (Button) findViewById(R.id.control_panel_button_travel);
        bTravel.setOnClickListener(this);

        destinationTimePanel.setTextColor(ControlPanelItem.GREEN);
        presentTimePanel.setTextColor(ControlPanelItem.YELLOW);
        lastTimeDepartedPanel.setTextColor(ControlPanelItem.RED);

        destinationTimePanel.setDate(calendarPresent);
        presentTimePanel.setDate(calendarPresent);
        lastTimeDepartedPanel.setDate(calendarLast);

        destinationTimePanel.setPanelName(R.string.destination_panel_name);
        presentTimePanel.setPanelName(R.string.present_panel_name);
        lastTimeDepartedPanel.setPanelName(R.string.last_departure_panel_name);

        destinationTimePanel.setOnChangeDateClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        destinationTimePanel.setOnChangeTimeClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.control_panel_button_travel:
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_TIME_DESTINATION, destinationTimePanel.getDate().getTimeInMillis());
// onTimeTravelListener.onTimeTraveled();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void showDatePickerDialog() {
        Calendar presentTimeCalendar = presentTimePanel.getDate();
        int day = presentTimeCalendar.get(Calendar.DAY_OF_MONTH);
        int month = presentTimeCalendar.get(Calendar.MONTH);
        int year = presentTimeCalendar.get(Calendar.YEAR);

        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.set(year, monthOfYear, dayOfMonth);
                destinationTimePanel.setDate(newCalendar);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(ControlPanelActivity.this,
                mDateSetListener, year, month, day);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar presentTimeCalendar = presentTimePanel.getDate();
        int minute = presentTimeCalendar.get(Calendar.MINUTE);
        int hour = presentTimeCalendar.get(Calendar.HOUR_OF_DAY);

        TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                presentTimeCalendar.set(Calendar.MINUTE, minute);
                presentTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                destinationTimePanel.setDate(presentTimeCalendar);

            }
        };

        TimePickerDialog dialog = new TimePickerDialog(ControlPanelActivity.this,
                mTimeSetListener, hour, minute, true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
