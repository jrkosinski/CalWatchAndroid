package com.calwatch.android.tasks;

import com.calwatch.android.api.requests.SignupRequest;
import com.calwatch.android.api.responses.UserResponse;
import android.content.Context;
import android.os.AsyncTask;

import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by Home on 22/1/2559.
 */
public class SignupAsyncTask extends AsyncApiTask<SignupRequest, Integer, UserResponse> {
    private static final String LogTag = "SignupAsyncTask";

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public SignupAsyncTask(Context context, IApiResponseCallback callback) {
        super(context, callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected UserResponse doInBackground(final SignupRequest... request) {
        UserResponse response = null;
        try {
            response = ApiService.createUser(request[0].getUser());

            try {
            }
            catch (final Exception e1) {

            }
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
