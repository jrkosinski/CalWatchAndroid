package com.calwatch.android.activities;

import com.calwatch.android.api.requests.SignupRequest;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.SignupAsyncTask;
import com.calwatch.android.util.AlertUtil;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.calwatch.android.R;
import com.calwatch.android.util.FormValidationElement;
import com.calwatch.android.util.FormValidationManager;
import com.calwatch.android.util.ValidationCallback;
import com.calwatch.android.util.ValidationUtil;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Signup screen.
 */
public class SignupActivity extends ActivityBase {

    private static final String LogTag = "SignupActivity";

    private EditText passwordText;
    private EditText passwordReenterText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentResId(R.layout.activity_signup);
        setAppBarTitle("Signup");
        setShowMenu(false);
        super.onCreate(savedInstanceState);

        passwordText = (EditText)findViewById(R.id.passwordText);
        passwordReenterText = (EditText)findViewById(R.id.passwordReenterText);

        final EditText usernameText = (EditText)findViewById(R.id.usernameText);
        final EditText targetCaloriesText = (EditText)findViewById(R.id.targetCaloriesText);
        final TextView usernameErrorLabel = (TextView)findViewById(R.id.usernameLabel);
        final TextView passwordErrorLabel = (TextView)findViewById(R.id.passwordLabel);
        final TextView passwordReenterErrorLabel = (TextView)findViewById(R.id.password2Label);
        final TextView targetCaloriesErrorLabel = (TextView)findViewById(R.id.caloriesLabel);
        final Button signupButton = (Button)findViewById(R.id.signupButton);

        final FormValidationManager formValidationManager = new FormValidationManager();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (formValidationManager.validateForm(true, true)) {
                    final SignupAsyncTask signupAsyncTask = new SignupAsyncTask(SignupActivity.this, new LoginCallback());
                    SignupRequest request = new SignupRequest();
                    request.setUsername(usernameText.getText().toString());
                    request.setPassword(passwordText.getText().toString());
                    request.setTargetCaloriesPerDay(Integer.parseInt(targetCaloriesText.getText().toString()));
                    signupAsyncTask.execute(request);
                }
            }
        });

        formValidationManager.setSubmitButton(signupButton);

        //add validation for username
        formValidationManager.addElement(
                new FormValidationElement(usernameText,
                        usernameErrorLabel,
                        true,
                        new ValidationCallback() {
                            @Override
                            public String validate(EditText editText) {
                                return ValidationUtil.validateUsername(editText.getText().toString());
                            }
                        }
                ));

        //add validation for password
        formValidationManager.addElement(
                new FormValidationElement(passwordText,
                        passwordErrorLabel,
                        true,
                        new ValidationCallback() {
                            @Override
                            public String validate(EditText editText)  {
                                String output = ValidationUtil.validatePassword(editText.getText().toString());
                                if (!editText.getText().toString().equals(passwordReenterText.getText().toString()))
                                    passwordReenterErrorLabel.setText("passwords do not match");
                                else
                                    passwordReenterErrorLabel.setText("");

                                return output;
                            }
                        }
                ));

        //add validation for re-enter password
        formValidationManager.addElement(
                new FormValidationElement(passwordReenterText,
                        passwordReenterErrorLabel,
                        true,
                        new ValidationCallback() {
                            @Override
                            public String validate(EditText editText)  {
                                final String password = passwordText.getText().toString();
                                final String password2 = editText.getText().toString();
                                if (!password.equals(password2))
                                    return "passwords do not match";
                                return "";
                            }
                        }
                ));

        //add validation for target calories
        formValidationManager.addElement(
                new FormValidationElement(targetCaloriesText,
                        targetCaloriesErrorLabel,
                        true,
                        new ValidationCallback() {
                            @Override
                            public String validate(EditText editText)  {
                                return ValidationUtil.validateCalories(editText.getText().toString());
                            }
                        }
                ));

        //prevalidate
        formValidationManager.validateForm(false, true);
    }

    @Override
    protected void exit()
    {
        super.exit();
        overridePendingTransitionHorizontalEntrance();
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
                exit();
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
