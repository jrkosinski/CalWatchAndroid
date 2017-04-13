package com.calwatch.android.tasks;

import com.calwatch.android.api.models.FilterParams;
import com.calwatch.android.api.responses.MealListResponse;
import android.content.Context;

import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class GetMealsAsyncTask extends AsyncApiTask<Integer, Integer, MealListResponse> {
    private static final String LogTag = "GetMealsAsyncTask";
    private FilterParams filterParams;

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public GetMealsAsyncTask(final Context context, final FilterParams filterParams, final IApiResponseCallback callback) {
        super(context, callback);
        this.filterParams = filterParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected MealListResponse doInBackground(final Integer... userId) {
        MealListResponse response = null;
        try {
            response = ApiService.getMeals(userId[0], filterParams);
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final MealListResponse response) {
        super.onPostExecute(response);
    }
}
