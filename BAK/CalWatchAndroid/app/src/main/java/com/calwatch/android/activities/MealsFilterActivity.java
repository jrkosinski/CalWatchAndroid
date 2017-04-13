package com.calwatch.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import com.calwatch.android.api.models.MealFilterParams;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.util.DateTimeUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.calwatch.android.R;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class MealsFilterActivity extends ActivityBase {
    private static final int DateFromPickerDialogId = 238;
    private static final int DateToPickerDialogId = 239;
    private static final int TimeFromPickerDialogId = 240;
    private static final int TimeToPickerDialogId = 241;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView dateFromLabel;
    private TextView dateToLabel;
    private TextView timeFromLabel;
    private TextView timeToLabel;

    private DateTime dateFrom;
    private DateTime dateTo;
    private LocalTime timeFrom;
    private LocalTime timeTo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setVisibility(View.GONE);

        initializeState();

        this.datePicker = (DatePicker)findViewById(R.id.datePicker);
        this.timePicker = (TimePicker)findViewById(R.id.timePicker);
        this.dateFromLabel = (TextView)findViewById(R.id.dateFromText);
        this.dateToLabel = (TextView)findViewById(R.id.dateToText);
        this.timeFromLabel = (TextView)findViewById(R.id.timeFromText);
        this.timeToLabel = (TextView)findViewById(R.id.timeToText);
        Button clearDateFromButton = (Button)findViewById(R.id.clearDateFromButton);
        Button clearDateToButton = (Button)findViewById(R.id.clearDateToButton);
        Button clearTimeFromButton = (Button)findViewById(R.id.clearTimeFromButton);
        Button clearTimeToButton = (Button)findViewById(R.id.clearTimeToButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        Button filterButton = (Button)findViewById(R.id.filterButton);
        Button editDateFromButton = (Button)findViewById(R.id.editDateFromButton);
        Button editDateToButton = (Button)findViewById(R.id.editDateToButton);
        Button editTimeFromButton = (Button)findViewById(R.id.editTimeFromButton);
        Button editTimeToButton = (Button)findViewById(R.id.editTimeToButton);

        //get the displayed dates/times from calling activity
        String dateFromString = getIntent().getStringExtra("DateFrom");
        String dateToString = getIntent().getStringExtra("DateTo");
        String timeFromString = getIntent().getStringExtra("TimeFrom");
        String timeToString = getIntent().getStringExtra("TimeTo");

        //set the starting dates/times
        this.setDateFrom((dateFromString != null && dateFromString.length() > 0) ? DateTimeUtil.StringToDate(dateFromString) : null);
        this.setDateTo((dateToString != null && dateToString.length() > 0) ? DateTimeUtil.StringToDate(dateToString) : null);
        this.setTimeFrom((timeFromString != null && timeFromString.length() > 0) ? DateTimeUtil.StringToTime(timeFromString) : null);
        this.setTimeTo((timeToString != null && timeToString.length() > 0) ? DateTimeUtil.StringToTime(timeToString) : null);

        //edit date action
        editDateFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DateFromPickerDialogId);
            }
        });
        editDateToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DateToPickerDialogId);
            }
        });

        //edit time action
        editTimeFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TimeFromPickerDialogId);
            }
        });
        editTimeToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TimeToPickerDialogId);
            }
        });

        //clear dates action
        clearDateFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateFrom(null);
            }
        });
        clearDateToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTo(null);
            }
        });

        //clear times action
        clearTimeFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeFrom(null);
            }
        });
        clearTimeToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeTo(null);
            }
        });

        //cancel action
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
                overridePendingTransitionVerticalExit();
            }
        });

        //filter action
        filterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                doFilter();
            }
        });
    }

    //TODO: fix this deprecated stuff with DialogFragment
    @Override
    protected Dialog onCreateDialog(int id)
    {
        DateTime date = null;
        LocalTime time = null;

        switch (id) {
            case DateFromPickerDialogId:
                date = dateFrom;
                if (date == null)
                    date = DateTime.now();
                return new DatePickerDialog(this, dateFromPickerListener,
                        date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());

            case DateToPickerDialogId:
                date = dateTo;
                if (date == null)
                    date = DateTime.now();
                return new DatePickerDialog(this, dateToPickerListener,
                        date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());

            case TimeFromPickerDialogId:
                time = timeFrom;
                if (time == null)
                    time = DateTime.now().toLocalTime();
                return new TimePickerDialog(this, timeFromPickerListener,
                        time.getHourOfDay(), time.getMinuteOfHour(), true);

            case TimeToPickerDialogId:
                time = timeTo;
                if (time == null)
                    time = DateTime.now().toLocalTime();
                return new TimePickerDialog(this, timeToPickerListener,
                        time.getHourOfDay(), time.getMinuteOfHour(), true);
        }
        return null;
    }

    private void setDateFrom(DateTime value)
    {
        dateFrom = value;
        if (value == null)
            dateFromLabel.setText("");
        else
            dateFromLabel.setText(DateTimeUtil.DateToString(value));
    }

    private void setDateTo(DateTime value)
    {
        dateTo = value;
        if (value == null)
            dateToLabel.setText("");
        else
            dateToLabel.setText(DateTimeUtil.DateToString(value));
    }

    private void setTimeFrom(LocalTime value)
    {
        timeFrom = value;
        if (value == null)
            timeFromLabel.setText("");
        else
            timeFromLabel.setText(DateTimeUtil.TimeToString(value));
    }

    private void setTimeTo(LocalTime value)
    {
        timeTo = value;
        if (value == null)
            timeToLabel.setText("");
        else
            timeToLabel.setText(DateTimeUtil.TimeToString(value));
    }

    private void doFilter()
    {
        MealFilterParams filterParams = new MealFilterParams();

        filterParams.setFromDate(DateTimeUtil.DateToString(dateFrom));
        filterParams.setToDate(DateTimeUtil.DateToString(dateTo));
        filterParams.setFromTime(DateTimeUtil.TimeToString(timeFrom));
        filterParams.setToTime(DateTimeUtil.TimeToString(timeTo));

        LocalStorage.setMealFilterParams(filterParams);

        setResult(RESULT_OK);
        finish();
        overridePendingTransitionVerticalExit();
    }


    private DatePickerDialog.OnDateSetListener dateFromPickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
        {
            DateTime dateTime = new DateTime(selectedYear, selectedMonth+1, selectedDay, 0, 0, 0, 0);

            // set selected date into datepicker also
            setDateFrom(dateTime);
        }
    };

    private DatePickerDialog.OnDateSetListener dateToPickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
        {
            DateTime dateTime = new DateTime(selectedYear, selectedMonth+1, selectedDay, 0, 0, 0, 0);

            // set selected date into datepicker also
            setDateTo(dateTime);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeFromPickerListener
            = new TimePickerDialog.OnTimeSetListener() {

        // when dialog box is closed, below method will be called.
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute)
        {
            LocalTime time = new LocalTime(selectedHour, selectedMinute);

            // set selected date into datepicker also
            setTimeFrom(time);
        }
    };

    private TimePickerDialog.OnTimeSetListener timeToPickerListener
            = new TimePickerDialog.OnTimeSetListener() {

        // when dialog box is closed, below method will be called.
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute)
        {
            LocalTime time = new LocalTime(selectedHour, selectedMinute);

            // set selected date into datepicker also
            setTimeTo(time);
        }
    };
}
