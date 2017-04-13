package com.calwatch.android.tasks;

import com.calwatch.android.api.responses.UserResponse;
import android.content.Context;
import android.os.AsyncTask;

import com.calwatch.android.api.requests.LoginRequest;
import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by Home on 22/1/2559.
 */
public class LoginAsyncTask extends AsyncApiTask<LoginRequest, Integer, UserResponse> {
    private static final String LogTag = "LoginAsyncTask";

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public LoginAsyncTask(Context context, IApiResponseCallback callback) {
        super(context, callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected UserResponse doInBackground(final LoginRequest... request) {
        UserResponse response = null;
        try {
            response = ApiService.login(request[0]);
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
