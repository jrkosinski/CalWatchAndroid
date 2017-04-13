package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.Meal;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class MealResponse extends ApiResponse {
    private Meal meal;

    public Meal getMeal() {
        return meal;
    }
    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    @Override
    public boolean isSuccessful()
    {
        return super.isSuccessful() && this.meal != null;
    }

    public MealResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
