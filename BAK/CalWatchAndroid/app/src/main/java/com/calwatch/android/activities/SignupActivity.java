package com.calwatch.android.activities;

import com.calwatch.android.api.requests.SignupRequest;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.SignupAsyncTask;
import com.calwatch.android.util.AlertUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.calwatch.android.R;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends ActivityBase {

    private static final String LogTag = "SignupActivity";

    private EditText usernameText;
    private EditText passwordText;
    private EditText passwordReenterText;
    private EditText targetCaloriesText;
    private Button signupButton;

    private String username;
    private String password;
    private String password2;
    private int calories;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Signup");
        setShowMenu(false);

        initializeState();

        usernameText = (EditText)findViewById(R.id.usernameText);
        passwordText = (EditText)findViewById(R.id.passwordText);
        passwordReenterText = (EditText)findViewById(R.id.passwordReenterText);
        targetCaloriesText = (EditText)findViewById(R.id.targetCaloriesText);
        signupButton = (Button)findViewById(R.id.signupButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String validationError = validateInput();
                if (validationError != null)
                {
                    AlertUtil.showAlert(
                            SignupActivity.this,
                            "Invalid Input",
                            validationError
                    );
                }
                else
                {
                    final SignupAsyncTask signupAsyncTask = new SignupAsyncTask(SignupActivity.this, new LoginCallback());
                    SignupRequest request = new SignupRequest();
                    request.setUsername(username);
                    request.setPassword(password);
                    request.setTargetCaloriesPerDay(calories);
                    signupAsyncTask.execute(request);
                }
            }
        });
    }

    private String validateInput()
    {
        //TODO: validate as the user types
        String output = null;

        StringBuilder sb = new StringBuilder();

        this.username = usernameText.getText().toString().trim();
        this.password = passwordText.getText().toString().trim();
        this.password2 = passwordReenterText.getText().toString().trim();
        String caloriesString = targetCaloriesText.getText().toString().trim();

        //validate username
        if (username.length() == 0) {
            sb.append("* Username is required.\n");
        }
        else if (username.length() < 5 || username.length() > 50){
            sb.append("* Username must be between 5 and 50 characters.\n");
        }

        //validate password
        if (password.length() == 0) {
            sb.append("* Password is required.\n");
        }
        else if (password.length() < 5 || password.length() > 50){
            sb.append("* Password must be between 5 and 50 characters.\n");
        }
        else
        {
            //ensure that passwords match
            if (!password.equals(password2))
                sb.append("* Passwords do not match.\n");
        }

        //validate target calories
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


    private class LoginCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response)
        {
            Log.i(LogTag, "Signup finished.");

            if (response != null && response.isSuccessful()) {
                Intent intent = new Intent(SignupActivity.this, MealsViewActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransitionHorizontalEntrance();
            }
            else
            {
                AlertUtil.showAlert(
                        SignupActivity.this,
                        "Signup Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Signup was unsuccessful."
                );
            }
        }
    }
}
