package com.tixon.backtothefutureexchange;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

    private Calendar calendarDestination, calendarPresent, calendarLast;

    SQLiteDatabase db;
    DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_panel_activity_layout);

        calendarDestination = Calendar.getInstance();
        calendarPresent = Calendar.getInstance();
        calendarLast = Calendar.getInstance();

        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Intent fromMainActivity = getIntent();

        calendarDestination.setTimeInMillis(fromMainActivity.getLongExtra(Constants.KEY_TIME_PRESENT, System.currentTimeMillis()));
        calendarPresent.setTimeInMillis(fromMainActivity.getLongExtra(Constants.KEY_TIME_PRESENT, System.currentTimeMillis()));
        calendarLast.setTimeInMillis(fromMainActivity.getLongExtra(Constants.KEY_TIME_LAST, System.currentTimeMillis()));
        initPanel();
    }

    private void initPanel() {
        destinationTimePanel = (ControlPanelItem) findViewById(R.id.destination_time_panel);
        presentTimePanel = (ControlPanelItem) findViewById(R.id.present_time_panel);
        lastTimeDepartedPanel = (ControlPanelItem) findViewById(R.id.last_time_departed_panel);

        Button bTravel = (Button) findViewById(R.id.control_panel_button_travel);
        bTravel.setOnClickListener(this);

        destinationTimePanel.setPanelType(ControlPanelItem.DESTINATION_TIME);
        presentTimePanel.setPanelType(ControlPanelItem.PRESENT_TIME);
        lastTimeDepartedPanel.setPanelType(ControlPanelItem.LAST_TIME_DEPARTED);

        destinationTimePanel.setDate(calendarDestination);
        presentTimePanel.setDate(calendarPresent);
        lastTimeDepartedPanel.setDate(calendarLast);

        presentTimePanel.startTimeRoll();


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
                intent.putExtra(Constants.KEY_TIME_PRESENT, presentTimePanel.getDate().getTimeInMillis());
                intent.putExtra(Constants.KEY_TIME_LAST, lastTimeDepartedPanel.getDate().getTimeInMillis());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void showDatePickerDialog() {
        Calendar presentTimeCalendar = Calendar.getInstance();
        presentTimeCalendar.setTimeInMillis(presentTimePanel.getDate().getTimeInMillis());

        int day = presentTimeCalendar.get(Calendar.DAY_OF_MONTH);
        int month = presentTimeCalendar.get(Calendar.MONTH);
        int year = presentTimeCalendar.get(Calendar.YEAR);

        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar destinationCalendar = destinationTimePanel.getDate();
                destinationCalendar.set(year, monthOfYear, dayOfMonth);
                destinationTimePanel.setDate(destinationCalendar);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(ControlPanelActivity.this,
                mDateSetListener, year, month, day);
        dialog.getDatePicker().setMaxDate(Constants.DEC_2045_31);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showTimePickerDialog() {
        Calendar presentTimeCalendar = Calendar.getInstance();
        presentTimeCalendar.setTimeInMillis(destinationTimePanel.getDate().getTimeInMillis());

        int minute = presentTimeCalendar.get(Calendar.MINUTE);
        int hour = presentTimeCalendar.get(Calendar.HOUR_OF_DAY);

        TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar destinationCalendar = Calendar.getInstance();
                destinationCalendar.setTimeInMillis(destinationTimePanel.getDate().getTimeInMillis());

                destinationCalendar.set(Calendar.MINUTE, minute);
                destinationCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                destinationTimePanel.setDate(destinationCalendar);

            }
        };

        TimePickerDialog dialog = new TimePickerDialog(ControlPanelActivity.this,
                mTimeSetListener, hour, minute, true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
