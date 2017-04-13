package com.calwatch.android.tasks;

import com.calwatch.android.api.responses.ApiResponse;
import android.content.Context;
import android.os.AsyncTask;

import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by Home on 22/1/2559.
 */
public class DeleteMealAsyncTask extends AsyncApiTask<Integer, Integer, ApiResponse> {
    private static final String LogTag = "DeleteMealAsyncTask";
    private final int userId;

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public DeleteMealAsyncTask(Context context, int userId, IApiResponseCallback callback) {
        super(context, callback);
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ApiResponse doInBackground(final Integer... mealId) {
        ApiResponse response = null;
        try {
            response = ApiService.deleteMeal(userId, mealId[0]);
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final ApiResponse response)
    {
        super.onPostExecute(response);
    }
}
