package com.calwatch.android.activities;

import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.LoginAsyncTask;
import com.calwatch.android.api.requests.LoginRequest;

import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.util.AlertUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.calwatch.android.R;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends ActivityBase {

    private static final String LogTag = "LoginActivity";

    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signupTextLink;
    private String password;
    private CheckBox rememberPasswordCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //TODO: this preamble can be shortened in every activity, using ActivityBase
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        setShowMenu(false);

        initializeState();

        //get widgets
        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupTextLink = (TextView) findViewById(R.id.signupTextLink);
        rememberPasswordCheckbox = (CheckBox)findViewById(R.id.rememberPasswordCheckbox);

        //restore saved username/password
        usernameText.setText(LocalStorage.getUsername());
        rememberPasswordCheckbox.setChecked(LocalStorage.getRememberPassword());
        if (rememberPasswordCheckbox.isChecked())
            passwordText.setText(LocalStorage.getPassword());

        //auto login (for testing)
        //autoLogin();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LoginActivity.this.setProgressActivity(true);
                doLogin();
            }
        });

        signupTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void doLogin()
    {
        final String username = usernameText.getText().toString();
        final String password = passwordText.getText().toString();
        this.password = password;

        //TODO: add some validation before submitting

        final LoginAsyncTask loginAsyncTask = new LoginAsyncTask(LoginActivity.this, new LoginCallback());
        loginAsyncTask.execute(new LoginRequest(username, password));
    }

    private void autoLoginForTesting()
    {
        usernameText.setText("user1"); //LocalStorage.getUsername());
        passwordText.setText("pass1"); //LocalStorage.getPassword());

        doLogin();
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
                finish();
                overridePendingTransitionHorizontalEntrance();
            }
            else
            {
                AlertUtil.showAlert(
                        LoginActivity.this,
                        "Login Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Login was unsuccessful."
                );
            }
        }
    }
}
