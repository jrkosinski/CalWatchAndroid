package com.calwatch.android.activities;

import com.calwatch.android.api.models.ErrorInfo;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.LoginAsyncTask;
import com.calwatch.android.api.requests.LoginRequest;

import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.util.AlertUtil;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.calwatch.android.R;
import com.calwatch.android.util.FormValidationElement;
import com.calwatch.android.util.FormValidationManager;
import com.calwatch.android.util.ValidationCallback;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Login screen
 */
public class LoginActivity extends ActivityBase {

    private static final String LogTag = "LoginActivity";

    private EditText usernameText;
    private EditText passwordText;
    private TextView usernameLabel;
    private TextView passwordLabel;
    private CheckBox rememberPasswordCheckbox;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentResId(R.layout.activity_login);
        setAppBarTitle("Login");
        setShowMenu(false);
        super.onCreate(savedInstanceState);

        //get widgets
        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        rememberPasswordCheckbox = (CheckBox)findViewById(R.id.rememberPasswordCheckbox);
        usernameLabel = (TextView)findViewById(R.id.usernameLabel);
        passwordLabel = (TextView)findViewById(R.id.passwordLabel);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView signupTextLink = (TextView) findViewById(R.id.signupTextLink);

        //restore saved username/Password
        usernameText.setText(LocalStorage.getUsername());
        rememberPasswordCheckbox.setChecked(LocalStorage.getRememberPassword());
        if (rememberPasswordCheckbox.isChecked())
            passwordText.setText(LocalStorage.getPassword());

        //auto login (for testing)
        //autoLogin();

        //login action
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                usernameLabel.setText("");
                passwordLabel.setText("");
                LoginActivity.this.setProgressActivity(true);
                doLogin();
            }
        });

        //signup action
        signupTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //form validation
        final FormValidationManager formValidationManager = new FormValidationManager();
        formValidationManager.setSubmitButton(loginButton);

        //validation for username text
        formValidationManager.addElement(new FormValidationElement(
                usernameText,
                null,
                true,
                new ValidationCallback() {
                    @Override
                    public String validate(EditText editText) {
                        return (editText.getText().length() > 0 ? "" : "username required");
                    }
                }
        ));

        //validation for password text
        formValidationManager.addElement(new FormValidationElement(
                passwordText,
                null,
                true,
                new ValidationCallback() {
                    @Override
                    public String validate(EditText editText) {
                        return (editText.getText().length() > 0 ? "" : "password required");
                    }
                }
        ));

        //prevalidate
        formValidationManager.validateForm(false, true);
    }

    private void doLogin()
    {
        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();
        this.password = password;

        final LoginAsyncTask loginAsyncTask = new LoginAsyncTask(LoginActivity.this, new LoginCallback());
        loginAsyncTask.execute(new LoginRequest(username, password));
    }

    private void autoLoginForTesting()
    {
        usernameText.setText("user1"); //LocalStorage.getUsername());
        passwordText.setText("pass1"); //LocalStorage.getPassword());

        doLogin();
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
            Log.i(LogTag, "Login finished.");

            if (response != null && response.isSuccessful()) {
                //set user settings
                LocalStorage.setRememberPassword(rememberPasswordCheckbox.isChecked());

                Intent intent = new Intent(LoginActivity.this, MealsViewActivity.class);
                startActivity(intent);
                exit();
            }
            else
            {
                //if we can find out what the error's about, we can display it above the appropriate textbox
                boolean showingErrorOnForm = false;
                if (response != null && response.hasError())
                {
                    ErrorInfo errorInfo = response.getErrorInfo();
                    if (errorInfo != null && errorInfo.getMessage() != null)
                    {
                        if (errorInfo.getMessage().toLowerCase().contains("username")) {
                            showingErrorOnForm = true;
                            usernameLabel.setText(errorInfo.getMessage());
                        }

                        else if (errorInfo.getMessage().toLowerCase().contains("password")) {
                            showingErrorOnForm = true;
                            passwordLabel.setText(errorInfo.getMessage());
                        }
                    }
                }

                //for unhandled errors, display popup
                if (!showingErrorOnForm) {
                    AlertUtil.showAlert(
                            LoginActivity.this,
                            "Login Error",
                            (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Login was unsuccessful."
                    );
                }
            }
        }
    }
}
