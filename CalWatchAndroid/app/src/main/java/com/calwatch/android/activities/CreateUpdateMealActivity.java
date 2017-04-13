package com.calwatch.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import com.calwatch.android.api.models.Meal;
import com.calwatch.android.api.requests.CreateUpdateMealRequest;
import com.calwatch.android.api.responses.MealResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.Callback;
import com.calwatch.android.tasks.CreateMealAsyncTask;
import com.calwatch.android.tasks.GetMealAsyncTask;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.UpdateMealAsyncTask;
import com.calwatch.android.util.AlertUtil;
import com.calwatch.android.util.DateTimeUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import com.calwatch.android.R;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.calwatch.android.util.FormValidationElement;
import com.calwatch.android.util.FormValidationManager;
import com.calwatch.android.util.PermissionUtil;
import com.calwatch.android.util.StringUtil;
import com.calwatch.android.util.ValidationCallback;
import com.calwatch.android.util.ValidationUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Form for creating new meals, or updating properties of existing ones.
 */
public class CreateUpdateMealActivity extends ActivityBase {
    private static final String LogTag = "CreateUpdtMealActivity";
    private static final int DatePickerDialogId = 238;
    private static final int TimePickerDialogId = 239;

    private EditText descriptionText;
    private EditText caloriesText;
    private Meal mealToUpdate;
    private DatePicker datePicker;
    private TextView dateDisplayLabel;
    private TextView timeDisplayLabel;
    private DateTime selectedDateTime;
    private FormValidationManager formValidationManager;

    public Meal getMealToUpdate(){
        return mealToUpdate;
    }

    public boolean isInUpdateMode()  {
        return this.mealToUpdate != null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.activity_create_update_meal);
        setAppBarTitle("Update Meal");

        super.onCreate(savedInstanceState);

        //get UI elements
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        final Button submitButton = (Button) findViewById(R.id.submitButton);
        final Button editDateButton = (Button) findViewById(R.id.editDateButton);
        final Button editTimeButton = (Button) findViewById(R.id.editTimeButton);
        final TextView descriptionLabel = (TextView) findViewById(R.id.descriptionLabel);
        final TextView caloriesLabel = (TextView) findViewById(R.id.caloriesLabel);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

        this.datePicker = (DatePicker) findViewById(R.id.datePicker);
        this.dateDisplayLabel = (TextView) findViewById(R.id.dateDisplayText);
        this.timeDisplayLabel = (TextView) findViewById(R.id.timeDisplayText);
        this.descriptionText = (EditText) findViewById(R.id.descriptionText);
        this.caloriesText = (EditText) findViewById(R.id.caloriesText);

        formValidationManager = new FormValidationManager();
        formValidationManager.setSubmitButton(submitButton);

        this.setTotalCaloriesLabelText();

        //try to get Meal from Intent
        int mealId = getIntent().getIntExtra("MealId", 0);
        if (mealId > 0) {
            this.getMealToUpdate(mealId);
        } else {
            setSelectedDateTime(DateTime.now());
        }

        //disable date/time controls if no permission to edit
        if (!PermissionUtil.currentUserCanEditTarget())
        {
            editDateButton.setEnabled(false);
            editTimeButton.setEnabled(false);
        }

        //edit date action
        editDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DatePickerDialogId);
            }
        });

        //edit time action
        editTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TimePickerDialogId);
            }
        });

        //cancel action
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(Activity.RESULT_CANCELED);
            }
        });

        //submit action
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidationManager.validateForm(true, true))
                    submitForm();
            }
        });

        //validation for description
        formValidationManager.addElement(new FormValidationElement(
                descriptionText,
                descriptionLabel,
                true,
                new ValidationCallback() {
                    @Override
                    public String validate(EditText editText) {
                        return ValidationUtil.validateDescription(editText.getText().toString());
                    }

                    @Override
                    public void setErrorState(EditText editText, String errorMessage, TextView errorLabel) {
                        super.setErrorState(editText, errorMessage, errorLabel);
                        if (StringUtil.isNullOrEmpty(errorMessage)) {
                            if (errorLabel != null) {
                                errorLabel.setText("description");
                                errorLabel.setTextColor(ContextCompat.getColor(CreateUpdateMealActivity.this, R.color.colorPrimaryDark));
                            }
                        } else
                            errorLabel.setTextColor(ContextCompat.getColor(CreateUpdateMealActivity.this, R.color.colorRed));
                    }
                }
        ));

        //validation for calories
        formValidationManager.addElement(new FormValidationElement(
                caloriesText,
                caloriesLabel,
                true,
                new ValidationCallback() {
                    @Override
                    public String validate(EditText editText) {
                        return ValidationUtil.validateCalories(editText.getText().toString());
                    }

                    @Override
                    public void setErrorState(EditText editText, String errorMessage, TextView errorLabel) {
                        super.setErrorState(editText, errorMessage, errorLabel);
                        if (StringUtil.isNullOrEmpty(errorMessage)) {
                            if (errorLabel != null) {
                                errorLabel.setText("calories");
                                errorLabel.setTextColor(ContextCompat.getColor(CreateUpdateMealActivity.this, R.color.colorPrimaryDark));
                            }
                        } else
                            errorLabel.setTextColor(ContextCompat.getColor(CreateUpdateMealActivity.this, R.color.colorRed));
                    }
                }
        ));

        //calories change as you type
        caloriesText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setTotalCaloriesLabelText();
            }
        });

        //pre-validate form
        formValidationManager.validateForm(false, true);

        //if user has no permission to edit, disable everything
        if (!PermissionUtil.currentUserCanEditTarget())
            formValidationManager.disableForm();
    }

    @Override
    protected void onUserChanged()
    {
        super.onUserChanged();
        exit(Activity.RESULT_OK);
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
        dateDisplayLabel.setText(String.format("%s/%s/%s",
                StringUtil.padLeft(Integer.toString(month), 2, '0'),
                StringUtil.padLeft(Integer.toString(day), 2, '0'),
                Integer.toString(year)
        ));
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

        if (mealToUpdate != null)
        {
            setTotalCaloriesLabelText();
            //findViewById(R.id.totalCaloriesLabelContainer).setVisibility(
                    //(DateTimeUtil.isSameDay(mealToUpdate.getDateTimeObject(), selectedDateTime)) ?
                            //View.VISIBLE :
                            //View.INVISIBLE);
        }
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
        String hour = StringUtil.padLeft(Integer.toString(time.getHourOfDay()), 2, '0');
        String minute = StringUtil.padLeft(Integer.toString(time.getMinuteOfHour()), 2, '0');

        timeDisplayLabel.setText(String.format("%s:%s", hour, minute));
    }

    private void submitForm()
    {
        final CreateUpdateMealRequest request = new CreateUpdateMealRequest();
        request.setDescription(descriptionText.getText().toString());
        request.setCalories(Integer.parseInt(caloriesText.getText().toString()));
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
        else {
            final UpdateMealAsyncTask updateMealAsyncTask = new UpdateMealAsyncTask(
                    CreateUpdateMealActivity.this,
                    LocalStorage.getCurrentTargetUserId(),
                    getMealToUpdate().getId(),
                    new CreateUpdateMealCallback()
            );

            updateMealAsyncTask.execute(request);
        }
    }

    private void getMealToUpdate(int mealId)
    {
        GetMealAsyncTask task = new GetMealAsyncTask(this, LocalStorage.getCurrentTargetUserId(), new GetMealCallback());
        task.execute(mealId);
    }

    @Override
    protected void exit()
    {
        super.exit();
        overridePendingTransitionVerticalExit();
    }

    private void setTotalCaloriesLabelText()
    {
        View container = findViewById(R.id.totalCaloriesLabelContainer);
        String totalCaloriesForDayString = getIntent().getStringExtra("TotalCalories");

        if (mealToUpdate != null &&
                !StringUtil.isNullOrEmpty(totalCaloriesForDayString) &&
                DateTimeUtil.isSameDay(selectedDateTime, mealToUpdate.getDateTimeObject()))
        {
            //get total calories for day, and calories for this meal
            int totalCaloriesForDay = Integer.parseInt(totalCaloriesForDayString);
            int caloriesForMeal = 0;
            if (caloriesText.getText().length() > 0)
                caloriesForMeal = Integer.parseInt(caloriesText.getText().toString());

            //adjust to show real number of edited calories
            int adjustedCalories = (totalCaloriesForDay - mealToUpdate.getCalories() + caloriesForMeal);
            String value = Integer.toString(adjustedCalories);

            //get the labels
            //TextView preLabel = (TextView)findViewById(R.id.totalCaloriesLabel);
            TextView label = (TextView)findViewById(R.id.totalCaloriesLabel);

            //set the text to display
            container.setVisibility(View.VISIBLE);
            String text = String.format("total calories for %s: ", DateTimeUtil.DateToString(mealToUpdate.getDateTimeObject()));
            //preLabel.setText(text);
            label.setText(value);

            //set the color of text label
            if (adjustedCalories > LocalStorage.getCurrentTargetUser().getTargetCaloriesPerDay())
                label.setTextColor(ContextCompat.getColor(CreateUpdateMealActivity.this, R.color.colorRed));
            else
                label.setTextColor(ContextCompat.getColor(CreateUpdateMealActivity.this, R.color.colorGreen));
        }
        else
            container.setVisibility(View.INVISIBLE);
    }

    private class CreateUpdateMealCallback implements IApiResponseCallback<MealResponse>
    {
        @Override
        public void onFinished(MealResponse response)
        {
            String action = isInUpdateMode() ? "Update" : "Create";

            Log.i(LogTag, action + " meal finished.");

            if (response != null && response.isSuccessful()) {
                exit(Activity.RESULT_OK);
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
        public void onFinished(MealResponse response) {
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

                //get total calories for given day
                setTotalCaloriesLabelText();

                if (!PermissionUtil.currentUserCanEditTarget())
                    formValidationManager.disableForm();
            }
            else
            {
                AlertUtil.showAlert(
                        CreateUpdateMealActivity.this,
                        "Get Meal Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Unable to retrieve meal.",
                        new Callback() {
                            @Override
                            public void execute() {
                                exit(RESULT_CANCELED);
                            }
                        }
                );
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
