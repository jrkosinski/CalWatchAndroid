package com.calwatch.android.tasks;

import com.calwatch.android.api.responses.MealResponse;
import android.content.Context;

import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by Home on 22/1/2559.
 */
public class GetMealAsyncTask extends AsyncApiTask<Integer, Integer, MealResponse> {
    private static final String LogTag = "GetMealAsyncTask";
    private int userId;

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public GetMealAsyncTask(Context context, int userId, IApiResponseCallback callback) {
        super(context, callback);
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected MealResponse doInBackground(final Integer... mealId) {
        MealResponse response = null;
        try {

            response = ApiService.getMeal(userId, mealId[0]);
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final MealResponse response) {
        super.onPostExecute(response);
    }
}
