package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.Meal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class MealListResponse extends ApiResponse {
    private List<Meal> meals;

    public List<Meal> getMeals(){
        if (meals == null)
            meals = new ArrayList<>();
        return this.meals;
    }
    public void setMeals(List<Meal> value) {this.meals = value;}

    @Override
    public boolean isSuccessful()
    {
        return super.isSuccessful() && this.meals != null;
    }

    public MealListResponse()
    {
        meals = new ArrayList<>();
    }

    public MealListResponse(int responseCode)
    {
        meals = new ArrayList<>();
        super.setResponseCode(responseCode);
    }
}
