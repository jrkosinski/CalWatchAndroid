package com.calwatch.android.activities;

import android.app.Activity;
import com.calwatch.android.R;
import com.calwatch.android.api.models.Report;
import com.calwatch.android.api.responses.ApiResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.Callback;
import com.calwatch.android.tasks.LogoutAsyncTask;
import com.calwatch.android.util.AlertUtil;
import com.calwatch.android.util.StringUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


/**
 * Created by John R. Kosinski on 25/1/2559.
 * Abstract base class for all activities in application
 */
public abstract class ActivityBase extends AppCompatActivity
{
    protected static final int FilterMealsActivityId = 101;
    protected static final int UpdateMealActivityId = 102;
    protected final static int UserSettingsActivityId = 103;
    protected final static int SwitchUserActivityId = 104;
    protected final static int ReportActivityId = 105;

    private TextView adminUsernameLabel;
    private boolean showMenu = true;
    private boolean showToolbar = true;
    protected View progressView;
    private int contentResId;
    private String appBarTitle;

    //in child classes, these properties must be set BEFORE calling super.onCreate
    protected void setShowMenu(boolean showMenu){this.showMenu = showMenu; }
    protected void setShowToolbar(boolean value) {showToolbar = value;}
    protected void setContentResId(int value) {contentResId = value;}
    protected void setAppBarTitle(String value) {appBarTitle=value;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //configure top toolbar content & whatnot
        setContentView(contentResId);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); //appBarTitle);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        toolbar.setPadding(0, 0, 0, 0);

        //hide toolbar if requested
        if (!showToolbar)
            toolbar.setVisibility(View.GONE);

        //configure global
        this.progressView = findViewById(R.id.main_progress_bar);
        this.adminUsernameLabel = (TextView)findViewById(R.id.adminUsernameLabel);
        this.displayUsername();
    }

    /*
    Deals with display of the progress spinner.
     */
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

    /*
    Causes next view to slide down from top of screen.
     */
    public void overridePendingTransitionVerticalEntrance()
    {
        overridePendingTransition(R.anim.slide_down, R.anim.do_nothing);
    }

    /*
    Causes view to slide down to bottom of screen.
     */
    public void overridePendingTransitionVerticalExit()
    {
        overridePendingTransition(R.anim.do_nothing, R.anim.slide_up);
    }

    /*
    Causes next view to slide over from right of screen.
     */
    public void overridePendingTransitionHorizontalEntrance()
    {
        overridePendingTransition(R.anim.slide_left, R.anim.do_nothing);
    }

    /*
    Causes next view to exit by sliding to the left.
     */
    public void overridePendingTransitionHorizontalExit()
    {
        overridePendingTransition(R.anim.do_nothing, R.anim.slide_right);
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
            if (!(this instanceof ReportActivity))
                menu.add(0, R.id.action_report, Menu.NONE, "Calorie Report");
            else
                menu.add(0, R.id.action_view_meals, Menu.NONE, "View Meals");

            if (!(this instanceof UserSettingsActivity))
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

        //settings
        if (id == R.id.action_settings) {
            showUserSettings();
        }

        //logout
        if (id == R.id.action_logout) {
            doLogout();
        }

        //switch user
        if (id == R.id.action_switch_user)
            showSwitchUser();

        //report
        if (id == R.id.action_report)
            showReport();

        //report
        if (id == R.id.action_view_meals)
            showMealsView();

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
                new Callback() {
                    @Override
                    public void execute() {
                        LogoutAsyncTask task = new LogoutAsyncTask(ActivityBase.this, new LogoutCallback());
                        task.execute(true);
                    }
                }, null
        );
    }

    protected void showSwitchUser()
    {
        Intent intent = new Intent(ActivityBase.this, SwitchUserActivity.class);
        startActivityForResult(intent, SwitchUserActivityId);
        overridePendingTransitionVerticalEntrance();
    }

    protected void showReport()
    {
        Intent intent = new Intent(ActivityBase.this, ReportActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, ReportActivityId);
        overridePendingTransitionHorizontalEntrance();
        finish();
    }

    protected void showMealsView()
    {
        Intent intent = new Intent(ActivityBase.this, MealsViewActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, FilterMealsActivityId);
        overridePendingTransitionHorizontalEntrance();
        finish();
    }

    /*
    Override to provide custom action when (admin) user changes target user.
     */
    protected void onUserChanged()
    {
        this.displayUsername();
    }

    /*
    Displays username of current target user, if different from current logged-in user.
     */
    protected void displayUsername()
    {
        if (this.adminUsernameLabel != null) {
            this.adminUsernameLabel.setText("");

            if (LocalStorage.getCurrentTargetUser() != null) {
                if (LocalStorage.getCurrentTargetUser().getId() != LocalStorage.getUserId())
                    this.adminUsernameLabel.setText("User: " + StringUtil.truncateText(LocalStorage.getCurrentTargetUser().getUsername(), 20));
            }
        }
    }

    /*
    Activity exit (finish)
     */
    protected void exit()
    {
        finish();
    }

    /*
    Activity exit with result (finish)
     */
    protected void exit(int result)
    {
        setResult(result);
        exit();
    }

    private class LogoutCallback implements IApiResponseCallback<ApiResponse>
    {
        @Override
        public void onFinished(ApiResponse response)
        {
            Log.i("Menu Settings", "Logout finished.");

            //clear auth token, target user, filter params
            LocalStorage.setAuthToken("");
            LocalStorage.setCurrentTargetUser(null);
            LocalStorage.setFilterParams(null);

            //redirect back to login
            Intent intent = new Intent(ActivityBase.this, LoginActivity.class);
            startActivity(intent);
            exit();
        }
    }
}
