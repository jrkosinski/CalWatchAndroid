package com.calwatch.android.tasks;

import com.calwatch.android.api.responses.UserListResponse;
import android.content.Context;

import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class GetUsersAsyncTask extends AsyncApiTask<Integer, Integer, UserListResponse> {
    private static final String LogTag = "GetUsersAsyncTask";

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public GetUsersAsyncTask(final Context context, final IApiResponseCallback callback) {
        super(context, callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected UserListResponse doInBackground(final Integer... userId) {
        UserListResponse response = null;
        try {
            response = ApiService.getUsers();
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final UserListResponse response) {
        super.onPostExecute(response);
    }
}
