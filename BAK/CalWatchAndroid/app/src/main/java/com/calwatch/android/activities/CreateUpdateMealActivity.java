package com.calwatch.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import com.calwatch.android.api.models.Meal;
import com.calwatch.android.api.requests.CreateUpdateMealRequest;
import com.calwatch.android.api.responses.MealResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.CreateMealAsyncTask;
import com.calwatch.android.tasks.GetMealAsyncTask;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.UpdateMealAsyncTask;
import com.calwatch.android.util.AlertUtil;
import com.calwatch.android.util.DateTimeUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.calwatch.android.R;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class CreateUpdateMealActivity extends ActivityBase {
    private static final String LogTag = "CreateUpdtMealActivity";
    private static final int DatePickerDialogId = 238;
    private static final int TimePickerDialogId = 239;

    private EditText descriptionText;
    private EditText caloriesText;
    private Meal mealToUpdate;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView dateDisplayText;
    private TextView timeDisplayText;
    private DateTime selectedDateTime;

    private int calories;
    private String description;

    public Meal getMealToUpdate(){
        return mealToUpdate;
    }

    public void setMealToUpdate(Meal value){
        mealToUpdate = value;
    }

    public boolean isInUpdateMode()  {
        return this.mealToUpdate != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_meal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeState();

        //get UI elements
        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        Button submitButton = (Button)findViewById(R.id.submitButton);
        Button editDateButton = (Button)findViewById(R.id.editDateButton);
        Button editTimeButton = (Button)findViewById(R.id.editTimeButton);
        this.datePicker = (DatePicker)findViewById(R.id.datePicker);
        this.timePicker = (TimePicker)findViewById(R.id.timePicker);
        this.dateDisplayText = (TextView)findViewById(R.id.dateDisplayText);
        this.timeDisplayText = (TextView)findViewById(R.id.timeDisplayText);
        this.descriptionText = (EditText)findViewById(R.id.descriptionText);
        this.caloriesText = (EditText)findViewById(R.id.caloriesText);

        //try to get Meal from Intent
        int mealId = getIntent().getIntExtra("MealId", 0);
        if (mealId > 0)
            this.getMealToUpdate(mealId);
        else
        {
            setSelectedDateTime(DateTime.now());
        }

        //set screen title
        getSupportActionBar().setTitle(mealId > 0 ? "Update Meal" : "Add Meal");

        //edit date action
        editDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showDialog(DatePickerDialogId);
            }
        });

        //edit time action
        editTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showDialog(TimePickerDialogId);
            }
        });

        //cancel action
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
                overridePendingTransitionVerticalExit();
            }
        });

        //submit action
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    @Override
    protected void onUserChanged()
    {
        super.onUserChanged();
        setResult(Activity.RESULT_OK);
        finish();
        overridePendingTransitionVerticalExit();
    }

    //TODO: fix this deprecated stuff with DialogFragment
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id) {
            case DatePickerDialogId:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        selectedDateTime.getYear(), selectedDateTime.getMonthOfYear()-1, selectedDateTime.getDayOfMonth());

            case TimePickerDialogId:
                return new TimePickerDialog(this, timePickerListener,
                        selectedDateTime.getHourOfDay(), selectedDateTime.getMinuteOfHour(), true);
        }
        return null;
    }

    private String validateInput()
    {
        //TODO: validate as the user types
        String output = null;

        StringBuilder sb = new StringBuilder();

        this.description = descriptionText.getText().toString().trim();
        String caloriesString = caloriesText.getText().toString().trim();

        //validate username
        if (description.length() == 0) {
            sb.append("* Description is required.\n");
        }
        else if (description.length() < 5 || description.length() > 500){
            sb.append("* Description must be between 5 and 500 characters.\n");
        }

        //validate calories
        if (caloriesString.length() == 0)
            this.calories = 0;
        else {
            try
            {
                this.calories = Integer.parseInt(caloriesString);

                if (this.calories < 0 || this.calories > 50000)
                    sb.append("* Enter a valid number of calories.\n");
            }
            catch (Exception e)
            {
                sb.append("* Enter a valid number of calories.\n");
            }
        }

        if (sb.length() > 0)
            output = sb.toString();

        return output;
    }

    private void setSelectedDateTime(DateTime dateTime)
    {
        selectedDateTime = dateTime;
        setSelectedDate(dateTime);
        setSelectedTime(dateTime.toLocalTime());
    }

    private void setSelectedDate(DateTime dateTime)
    {
        int month = dateTime.getMonthOfYear();
        int day = dateTime.getDayOfMonth();
        int year = dateTime.getYear();

        datePicker.init(year, month, day, null);
        dateDisplayText.setText(String.format("%d/%d/%d", month, day, year));
    }

    private void updateSelectedDate(DateTime dateTime)
    {
        setSelectedDate(dateTime);

        selectedDateTime = new DateTime(
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth(),
                selectedDateTime.getHourOfDay(),
                selectedDateTime.getMinuteOfHour(),
                selectedDateTime.getSecondOfMinute(),
                selectedDateTime.getMillisOfSecond()
        );
    }

    private void updateSelectedTime(LocalTime time)
    {
        setSelectedTime(time);

        selectedDateTime = new DateTime(
                selectedDateTime.getYear(),
                selectedDateTime.getMonthOfYear(),
                selectedDateTime.getDayOfMonth(),
                time.getHourOfDay(),
                time.getMinuteOfHour(),
                0, 0
        );
    }

    private void setSelectedTime(LocalTime time)
    {
        int hour = time.getHourOfDay();
        int minute = time.getMinuteOfHour();

        timeDisplayText.setText(String.format("%d:%d", hour, minute));
    }

    private void submitForm()
    {
        String validationError = validateInput();
        if (validationError != null)
        {
            AlertUtil.showAlert(
                    CreateUpdateMealActivity.this,
                    "Invalid Input",
                    validationError
            );
        }
        else
        {
            final CreateUpdateMealRequest request = new CreateUpdateMealRequest();
            request.setDescription(description);
            request.setCalories(calories);
            request.setDateTime(DateTimeUtil.DateTimeToString(selectedDateTime));

            //create new meal
            if (!isInUpdateMode()) {
                final CreateMealAsyncTask createMealAsyncTask = new CreateMealAsyncTask(
                        CreateUpdateMealActivity.this,
                        LocalStorage.getCurrentTargetUserId(),
                        new CreateUpdateMealCallback()
                );

                createMealAsyncTask.execute(request);
            }
            //or update existing meal
            else
            {
                final UpdateMealAsyncTask updateMealAsyncTask = new UpdateMealAsyncTask(
                        CreateUpdateMealActivity.this,
                        LocalStorage.getCurrentTargetUserId(),
                        getMealToUpdate().getId(),
                        new CreateUpdateMealCallback()
                );

                updateMealAsyncTask.execute(request);
            }
        }
    }

    private void getMealToUpdate(int mealId)
    {
        GetMealAsyncTask task = new GetMealAsyncTask(this, LocalStorage.getCurrentTargetUserId(), new GetMealCallback());
        task.execute(mealId);
    }

    private class CreateUpdateMealCallback implements IApiResponseCallback<MealResponse>
    {
        @Override
        public void onFinished(MealResponse response)
        {
            String action = isInUpdateMode() ? "Update" : "Create";

            Log.i(LogTag, action + " meal finished.");

            if (response != null && response.isSuccessful()) {
                setResult(Activity.RESULT_OK);
                finish();
                overridePendingTransitionVerticalExit();
            }
            else
            {
                AlertUtil.showAlert(
                        CreateUpdateMealActivity.this,
                        action + " Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : action + " Meal unsuccessful."
                );
            }
        }
    }

    private class GetMealCallback implements IApiResponseCallback<MealResponse>
    {
        @Override
        public void onFinished(MealResponse response)
        {
            Log.i(LogTag, "Get meal finished.");

            if (response != null && response.isSuccessful())
            {
                //set the displayed date
                mealToUpdate = response.getMeal();
                setSelectedDateTime((mealToUpdate != null && mealToUpdate.getDateTimeObject() != null) ?
                                mealToUpdate.getDateTimeObject() :
                                DateTime.now()
                );

                //preset text for update mode
                descriptionText.setText(mealToUpdate.getDescription());
                caloriesText.setText(Integer.toString(mealToUpdate.getCalories()));
            }
            else
            {
                AlertUtil.showAlert(
                        CreateUpdateMealActivity.this,
                        "Get Meal Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Unable to retrieve meal."
                );

                finish();
                overridePendingTransitionVerticalExit();
            }
        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
        {
            DateTime dateTime = new DateTime(selectedYear, selectedMonth+1, selectedDay, 0, 0, 0, 0);

            // set selected date into datepicker also
            updateSelectedDate(dateTime);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener
            = new TimePickerDialog.OnTimeSetListener() {

        // when dialog box is closed, below method will be called.
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute)
        {
            LocalTime time = new LocalTime(selectedHour, selectedMinute);

            // set selected date into datepicker also
            updateSelectedTime(time);
        }
    };
}
