package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.Meal;

/**
 * Created by Home on 21/1/2559.
 */
public class MealResponse extends ApiResponse {
    private Meal meal;

    public Meal getMeal() {
        return meal;
    }
    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public MealResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
