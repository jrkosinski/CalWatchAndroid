package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.Meal;

import java.util.List;

/**
 * Created by Home on 22/1/2559.
 */
public class MealListResponse extends ApiResponse {
    private List<Meal> meals;

    public List<Meal> getMeals(){return this.meals;}
    public void setMeals(List<Meal> value) {this.meals = value;}

    public MealListResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
