package com.calwatch.android.tasks;

import com.calwatch.android.api.responses.ApiResponse;
import android.content.Context;

import com.calwatch.android.api.ApiService;
import com.calwatch.android.storage.LocalStorage;

import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class LogoutAsyncTask extends AsyncApiTask<Boolean, Integer, ApiResponse> {
    private static final String LogTag = "LogoutAsyncTask";

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public LogoutAsyncTask(Context context, IApiResponseCallback callback) {
        super(context, callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ApiResponse doInBackground(final Boolean... request) {
        ApiResponse response = null;
        try {
            response = ApiService.logout();
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final ApiResponse response) {
        LocalStorage.setCurrentTargetUser(null);
        super.onPostExecute(response);
    }
}
