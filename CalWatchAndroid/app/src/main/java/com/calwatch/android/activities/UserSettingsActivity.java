package com.calwatch.android.activities;

import com.calwatch.android.api.requests.UpdateUserRequest;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.Callback;
import com.calwatch.android.tasks.GetUserAsyncTask;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.UpdateUserAsyncTask;
import com.calwatch.android.util.AlertUtil;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import com.calwatch.android.R;
import com.calwatch.android.util.FormValidationElement;
import com.calwatch.android.util.FormValidationManager;
import com.calwatch.android.util.PermissionUtil;
import com.calwatch.android.util.StringUtil;
import com.calwatch.android.util.ValidationCallback;
import com.calwatch.android.util.ValidationUtil;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Form for editing user settings/properties.
 */
public class UserSettingsActivity extends ActivityBase {
    private static final String LogTag = "UserSettingsActivity";

    private EditText targetCaloriesText;
    private String permissionLevel;
    private FormValidationManager formValidationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentResId(R.layout.activity_user_settings);
        setAppBarTitle("User Settings");
        super.onCreate(savedInstanceState);

        targetCaloriesText = (EditText)findViewById(R.id.targetCaloriesText);

        final Button updateButton = (Button)findViewById(R.id.updateButton);
        final Button cancelButton = (Button)findViewById(R.id.cancelButton);
        final TextView caloriesLabel = (TextView)findViewById(R.id.caloriesLabel);

        //cancel button action
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(RESULT_CANCELED);
            }
        });

        formValidationManager = new FormValidationManager();
        formValidationManager.setSubmitButton(updateButton);

        //updte button action
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidationManager.validateForm(true, true)) {
                    final UpdateUserAsyncTask updateUserAsyncTask = new UpdateUserAsyncTask(UserSettingsActivity.this, LocalStorage.getCurrentTargetUserId(), new UpdateUserCallback());
                    UpdateUserRequest request = new UpdateUserRequest();
                    request.setPermissionLevel(permissionLevel);
                    request.setTargetCaloriesPerDay(Integer.parseInt(targetCaloriesText.getText().toString()));
                    updateUserAsyncTask.execute(request);
                }
            }
        });

        //validation for calories
        formValidationManager.addElement(new FormValidationElement(
                targetCaloriesText,
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
                                errorLabel.setText("target calories / day");
                                errorLabel.setTextColor(ContextCompat.getColor(UserSettingsActivity.this, R.color.colorPrimaryDark));
                            }
                        } else
                            errorLabel.setTextColor(ContextCompat.getColor(UserSettingsActivity.this, R.color.colorRed));
                    }
                }
        ));

        formValidationManager.validateForm(false, true);

        //get current user record
        GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(this, new GetUserCallback());
        getUserAsyncTask.execute(LocalStorage.getCurrentTargetUserId());
    }

    @Override
    protected void onUserChanged()
    {
        super.onUserChanged();
        exit(RESULT_OK);
    }


    private class UpdateUserCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response) {

            Log.i(LogTag, "Update user finished.");

            if (response.isSuccessful())
            {
                LocalStorage.setCurrentTargetUser(response.getUser());
                exit(RESULT_OK);
            }
            else
            {
                AlertUtil.showAlert(
                        UserSettingsActivity.this,
                        "Update User Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Update user was unsuccessful."
                );
            }
        }
    }

    private class GetUserCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response) {

            Log.i(LogTag, "Get user finished.");

            if (response.isSuccessful())
            {
                targetCaloriesText.setText(Integer.toString(response.getUser().getTargetCaloriesPerDay()));
                permissionLevel = response.getUser().getPermissionLevel();

                targetCaloriesText.setSelection(targetCaloriesText.getText().length(), targetCaloriesText.getText().length());

                if (!PermissionUtil.currentUserCanEditTarget())
                    formValidationManager.disableForm();
            } else {
                AlertUtil.showAlert(
                        UserSettingsActivity.this,
                        "Get User Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Get user was unsuccessful.",
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

    @Override
    protected void exit() {
        super.exit();
        overridePendingTransitionVerticalExit();
    }
}
