package com.calwatch.android.activities;

import com.calwatch.android.api.requests.UpdateUserRequest;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.GetUserAsyncTask;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.UpdateUserAsyncTask;
import com.calwatch.android.util.AlertUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.calwatch.android.R;
import android.widget.Button;
import android.widget.EditText;

public class UserSettingsActivity extends ActivityBase {
    private static final String LogTag = "UserSettingsActivity";

    private EditText targetCaloriesText;
    private String permissionLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Settings");

        initializeState();

        targetCaloriesText = (EditText)findViewById(R.id.targetCaloriesText);
        Button updateButton = (Button)findViewById(R.id.updateButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        //cancel button action
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransitionVerticalExit();
            }
        });

        //updte button action
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput())
                {
                    final UpdateUserAsyncTask updateUserAsyncTask = new UpdateUserAsyncTask(UserSettingsActivity.this, LocalStorage.getCurrentTargetUserId(), new UpdateUserCallback());
                    UpdateUserRequest request = new UpdateUserRequest();
                    request.setPermissionLevel(permissionLevel);
                    request.setTargetCaloriesPerDay(Integer.parseInt(targetCaloriesText.getText().toString()));
                    updateUserAsyncTask.execute(request);
                }
            }
        });

        //get current user record
        GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(this, new GetUserCallback());
        getUserAsyncTask.execute(LocalStorage.getCurrentTargetUserId());
    }

    @Override
    protected void onUserChanged()
    {
        super.onUserChanged();
        setResult(RESULT_OK);
        finish();
        overridePendingTransitionVerticalExit();
    }

    private boolean validateInput()
    {
        String text = targetCaloriesText.getText().toString().trim();
        if (text.length() == 0)
            text = "0";

        int calories = Integer.parseInt(text);
        if (calories < 0 || calories > 10000) {
            AlertUtil.showAlert(
                    this,
                    "Invalid Input",
                    "Please enter a valid numeric value for calories."
            );

            return false;
        }

        return true;
    }


    private class UpdateUserCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response) {

            Log.i(LogTag, "Update user finished.");

            if (response.isSuccessful())
            {
                setResult(RESULT_OK);
                finish();
                overridePendingTransitionVerticalExit();
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
            }
            else
            {
                AlertUtil.showAlert(
                        UserSettingsActivity.this,
                        "Get User Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Get user was unsuccessful."
                );
            }
        }
    }

    @Override
    protected void showUserSettings()
    {
    }
}
