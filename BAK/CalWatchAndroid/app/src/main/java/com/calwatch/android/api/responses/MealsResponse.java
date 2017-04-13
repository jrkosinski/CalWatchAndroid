package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.Meal;

import java.util.List;

/**
 * Created by Home on 22/1/2559.
 */
public class MealsResponse extends ApiResponse {
    private List<Meal> meals;

    public List<Meal> getUsers(){return this.meals;}
    public void setUsers(List<Meal> value) {this.meals = value;}

    public MealsResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
