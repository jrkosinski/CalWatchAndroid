package com.calwatch.android.tasks;

import com.calwatch.android.api.requests.CreateUpdateMealRequest;
import com.calwatch.android.api.responses.MealResponse;
import android.content.Context;

import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class UpdateMealAsyncTask extends AsyncApiTask<CreateUpdateMealRequest, Integer, MealResponse> {
    private static final String LogTag = "UpdateMealAsyncTask";
    private final int userId;
    private final int mealId;

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public UpdateMealAsyncTask(Context context, int userId, int mealId, IApiResponseCallback callback) {
        super(context, callback);
        this.userId = userId;
        this.mealId = mealId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected MealResponse doInBackground(final CreateUpdateMealRequest... request) {
        MealResponse response = null;
        try {
            response = ApiService.updateMeal(userId, mealId, request[0].getMeal());
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
