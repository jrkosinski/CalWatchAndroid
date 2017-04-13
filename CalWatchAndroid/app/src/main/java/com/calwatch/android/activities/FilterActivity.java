package com.calwatch.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import com.calwatch.android.api.models.FilterParams;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.util.DateTimeUtil;

import android.os.Bundle;
import android.view.View;
import com.calwatch.android.R;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Allows user to set filter options for meals.
 */
public class FilterActivity extends ActivityBase {
    private static final int DateFromPickerDialogId = 238;
    private static final int DateToPickerDialogId = 239;
    private static final int TimeFromPickerDialogId = 240;
    private static final int TimeToPickerDialogId = 241;

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
        setContentResId(R.layout.activity_filter);
        setAppBarTitle("Update Meal");
        setShowToolbar(false);
        super.onCreate(savedInstanceState);

        this.dateFromLabel = (TextView)findViewById(R.id.dateFromText);
        this.dateToLabel = (TextView)findViewById(R.id.dateToText);
        this.timeFromLabel = (TextView)findViewById(R.id.timeFromText);
        this.timeToLabel = (TextView)findViewById(R.id.timeToText);

        final Button clearDateFromButton = (Button)findViewById(R.id.clearDateFromButton);
        final Button clearDateToButton = (Button)findViewById(R.id.clearDateToButton);
        final Button clearTimeFromButton = (Button)findViewById(R.id.clearTimeFromButton);
        final Button clearTimeToButton = (Button)findViewById(R.id.clearTimeToButton);
        final Button cancelButton = (Button)findViewById(R.id.cancelButton);
        final Button filterButton = (Button)findViewById(R.id.filterButton);
        final Button editDateFromButton = (Button)findViewById(R.id.editDateFromButton);
        final Button editDateToButton = (Button)findViewById(R.id.editDateToButton);
        final Button editTimeFromButton = (Button)findViewById(R.id.editTimeFromButton);
        final Button editTimeToButton = (Button)findViewById(R.id.editTimeToButton);

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
            public void onClick(View v)
            {
                exit(Activity.RESULT_CANCELED);
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

    @Override
    protected void exit() {
        super.exit();
        overridePendingTransitionVerticalExit();
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
        else {
            DateTime dateTo = DateTimeUtil.StringToDate(dateToLabel.getText().toString());
            if (dateTo != null)
            {
                if (value.isAfter(dateTo)) {
                    value = dateTo;
                    dateTo = value;
                }
            }
            dateFromLabel.setText(DateTimeUtil.DateToString(value));
        }
    }

    private void setDateTo(DateTime value)
    {
        dateTo = value;
        if (value == null)
            dateToLabel.setText("");
        else {
            DateTime dateFrom = DateTimeUtil.StringToDate(dateFromLabel.getText().toString());
            if (dateFrom != null)
            {
                if (value.isBefore(dateFrom)) {
                    value = dateFrom;
                    dateTo = value;
                }
            }

            dateToLabel.setText(DateTimeUtil.DateToString(value));
        }
    }

    private void setTimeFrom(LocalTime value)
    {
        timeFrom = value;
        if (value == null)
            timeFromLabel.setText("");
        else {
            LocalTime timeTo = DateTimeUtil.StringToTime(timeToLabel.getText().toString());
            if (timeTo != null) {
                if (value.isAfter(timeTo)) {
                    value = timeTo;
                    timeTo = value;
                }
            }
        }

        timeFromLabel.setText(DateTimeUtil.TimeToString(value));
    }

    private void setTimeTo(LocalTime value)
    {
        timeTo = value;
        if (value == null)
            timeToLabel.setText("");
        else {
            LocalTime timeFrom = DateTimeUtil.StringToTime(timeFromLabel.getText().toString());
            if (timeFrom != null) {
                if (value.isBefore(timeFrom)) {
                    value = timeFrom;
                    timeTo = value;
                }
            }
        }

        timeToLabel.setText(DateTimeUtil.TimeToString(value));
    }

    private void doFilter()
    {
        FilterParams filterParams = new FilterParams();

        filterParams.setDateFrom(DateTimeUtil.DateToString(dateFrom));
        filterParams.setDateTo(DateTimeUtil.DateToString(dateTo));
        filterParams.setTimeFrom(DateTimeUtil.TimeToString(timeFrom));
        filterParams.setTimeTo(DateTimeUtil.TimeToString(timeTo));

        LocalStorage.setFilterParams(filterParams);

        exit(RESULT_OK);
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
