package com.calwatch.android.activities;

import com.calwatch.android.api.models.User;
import com.calwatch.android.api.responses.UserListResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.GetUsersAsyncTask;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.util.AlertUtil;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.calwatch.android.R;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class SwitchUserActivity extends ActivityBase {

    private final static String LogTag = "SwitchUserActivity";

    private ListView usersListView;
    private List<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        initializeState();

        usersListView = (ListView)findViewById(R.id.usersListView);

        refreshListFromServer();

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getItemAtPosition(position);
                LocalStorage.setCurrentTargetUserId(user.getId());
                LocalStorage.setCurrentTargetUsername(user.getUsername());
                LocalStorage.setMealFilterParams(null);
                setResult(RESULT_OK);
                finish();
                overridePendingTransitionVerticalExit();
            }
        });
    }

    private void refreshListFromServer()
    {
        final GetUsersAsyncTask getUsersAsyncTask = new GetUsersAsyncTask(SwitchUserActivity.this, new GetUsersCallback());
        getUsersAsyncTask.execute();
    }

    private void reloadListView()
    {
        final UserArrayAdapter listAdapter = new UserArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                this.usersList
        );

        usersListView.setAdapter(listAdapter);
    }

    private class UserArrayAdapter extends ArrayAdapter<User>
    {
        private final Context context;
        private final List<User> users;

        public UserArrayAdapter(Context context, int textViewResourceId, List<User> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.users = objects;
        }

        @Override
        public long getItemId(int position) {
            User item = getItem(position);
            return item.getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.user_row_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.usernameText);

            textView.setText(users.get(position).getUsername());

            return rowView;
        }
    }

    private class GetUsersCallback implements IApiResponseCallback<UserListResponse>
    {
        @Override
        public void onFinished(UserListResponse response) {
            Log.i(LogTag, "Get Users finished.");

            if (response != null && response.isSuccessful()) {
                usersList = response.getUsers();
                reloadListView();
            }
            else
            {
                AlertUtil.showAlert(
                        SwitchUserActivity.this,
                        "Get Users Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Get Users was unsuccessful."
                );
            }
        }
    }
}
