package com.calwatch.android.activities;

import android.app.Activity;
import com.calwatch.android.R;
import com.calwatch.android.api.responses.ApiResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.ICallback;
import com.calwatch.android.tasks.LogoutAsyncTask;
import com.calwatch.android.util.AlertUtil;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


/**
 * Created by Home on 25/1/2559.
 */
public class ActivityBase extends AppCompatActivity
{
    protected static final int FilterMealsActivityId = 101;
    protected static final int UpdateMealActivityId = 102;
    protected final static int UserSettingsActivityId = 103;
    protected final static int SwitchUserActivityId = 104;

    private TextView adminUsernameLabel;
    private boolean showMenu = true;
    protected View progressView;


    protected void setShowMenu(boolean showMenu)
    {
        this.showMenu = showMenu;
    }

    protected void initializeState()
    {
        this.progressView = findViewById(R.id.main_progress_bar);
        this.adminUsernameLabel = (TextView)findViewById(R.id.adminUsernameLabel);
        this.adminUsernameLabel.setText(LocalStorage.getCurrentTargetUsername());
    }

    public void setProgressActivity(final boolean isProgress)
    {
        if (progressView != null) {
            if (isProgress) {
                progressView.setVisibility(View.VISIBLE);
            } else {
                progressView.setVisibility(View.GONE);
            }
        }
    }

    public void overridePendingTransitionVerticalEntrance()
    {
        overridePendingTransition(R.anim.slide_down, R.anim.do_nothing);
    }

    public void overridePendingTransitionVerticalExit()
    {
        overridePendingTransition(R.anim.do_nothing, R.anim.slide_up);
    }

    public void overridePendingTransitionHorizontalEntrance()
    {
        overridePendingTransition(R.anim.slide_left, R.anim.do_nothing);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case SwitchUserActivityId:
                if (resultCode == Activity.RESULT_OK)
                    onUserChanged();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (this.showMenu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if (this.showMenu) {
            menu.add(0, R.id.action_settings, Menu.NONE, "Settings");

            //add the switch user option if user is qualified
            String permissionLevel = LocalStorage.getPermissionLevel();
            if (permissionLevel != null) {
                if (permissionLevel.equals("DataViewer") || permissionLevel.equals("Admin"))
                    menu.add(0, R.id.action_switch_user, Menu.NONE, "Switch User");
            }

            menu.add(0, R.id.action_logout, Menu.NONE, "Logout");

            return super.onPrepareOptionsMenu(menu);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showUserSettings();
        }

        if (id == R.id.action_logout) {
            doLogout();
        }

        if (id == R.id.action_switch_user)
            showSwitchUser();

        return super.onOptionsItemSelected(item);
    }

    protected void showUserSettings()
    {
        Intent intent = new Intent(ActivityBase.this, UserSettingsActivity.class);
        startActivityForResult(intent, UserSettingsActivityId);
        overridePendingTransitionVerticalEntrance();
    }

    protected void doLogout()
    {
        AlertUtil.showDialogTwoOptions(
                ActivityBase.this,
                "Logout",
                "Are you sure you want to log out?",
                "Yes", "Cancel",
                new LogoutConfirmCallback(), null
        );
    }

    protected void showSwitchUser()
    {
        Intent intent = new Intent(ActivityBase.this, SwitchUserActivity.class);
        startActivityForResult(intent, SwitchUserActivityId);
        overridePendingTransitionVerticalEntrance();
    }

    protected void onUserChanged()
    {
        this.adminUsernameLabel.setText(LocalStorage.getCurrentTargetUsername());
    }


    private class LogoutCallback implements IApiResponseCallback<ApiResponse>
    {
        @Override
        public void onFinished(ApiResponse response)
        {
            Log.i("Menu Settings", "Logout finished.");

            //clear auth token
            LocalStorage.setAuthToken("");

            //redirect back to login
            Intent intent = new Intent(ActivityBase.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class LogoutConfirmCallback implements ICallback
    {
        @Override
        public void execute()
        {
            LogoutAsyncTask task = new LogoutAsyncTask(ActivityBase.this, new LogoutCallback());
            task.execute(true);
        }
    }
}
