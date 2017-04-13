package com.calwatch.android.tasks;

import com.calwatch.android.api.requests.UpdateUserRequest;
import com.calwatch.android.api.responses.UserResponse;
import android.content.Context;

import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class UpdateUserAsyncTask extends AsyncApiTask<UpdateUserRequest, Integer, UserResponse> {
    private static final String LogTag = "UpdateUserAsyncTask";
    private final int userId;

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public UpdateUserAsyncTask(Context context, int userId, IApiResponseCallback callback) {
        super(context, callback);
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected UserResponse doInBackground(final UpdateUserRequest... request) {
        UserResponse response = null;
        try {
            response = ApiService.updateUser(userId, request[0].getUser());
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final UserResponse response) {
        super.onPostExecute(response);
    }
}
