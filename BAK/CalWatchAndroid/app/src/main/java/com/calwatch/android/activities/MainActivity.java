package com.calwatch.android.activities;

import com.calwatch.android.R;
import com.calwatch.android.api.ApiService;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.GetUserAsyncTask;
import com.calwatch.android.tasks.IApiResponseCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setShowMenu(false);
        toolbar.setVisibility(View.GONE);

        initializeState();
        ApiService.configure(getApplicationContext().getString(R.string.api_base_url));
        setProgressActivity(true);

        //initialize storage
        LocalStorage.initialize(getApplicationContext());

        //check if user is already logged in, or can be auto-logged in. If not, go to login screen.
        String authToken = LocalStorage.getAuthToken();
        if (authToken != null && authToken.length() > 0)
        {
            //make any call, for the purpose of checking API connectivity and auth token validity
            GetUserAsyncTask task = new GetUserAsyncTask(this, new GetUserCallback());
            task.execute(0);
        }
        else {
            goToLogin();
        }
    }

    private void goToLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private class GetUserCallback implements IApiResponseCallback<UserResponse>
    {
        @Override
        public void onFinished(UserResponse response)
        {
            if (response != null)
            {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, MealsViewActivity.class);
                    LocalStorage.setUserId(response.getUser().getId());
                    LocalStorage.setUsername(response.getUser().getUsername());
                    startActivity(intent);
                    finish();
                    return;
                }
            }

            //go to meals view
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
