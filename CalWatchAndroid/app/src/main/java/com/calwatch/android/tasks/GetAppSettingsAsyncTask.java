package com.calwatch.android.tasks;

import com.calwatch.android.api.responses.ApiResponse;
import android.content.Context;

import com.calwatch.android.api.ApiService;
import com.calwatch.android.api.responses.AppSettingsResponse;

import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class GetAppSettingsAsyncTask extends AsyncApiTask<Integer, Integer, AppSettingsResponse> {
    private static final String LogTag = "GetAppSettingsAsyncTask";

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public GetAppSettingsAsyncTask(final Context context, final IApiResponseCallback callback) {
        super(context, callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected AppSettingsResponse doInBackground(final Integer... mealId) {
        AppSettingsResponse response = null;
        try {
            response = ApiService.getAppSettings();
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final AppSettingsResponse response)
    {
        super.onPostExecute(response);
    }
}
