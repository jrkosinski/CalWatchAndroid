package com.calwatch.android.api.requests;

import com.calwatch.android.api.models.Meal;

/**
 * Created by John R. Kosinski on 23/1/2559.
 */
public class CreateUpdateMealRequest extends ApiRequest {
    private Meal meal;

    public Meal getMeal(){
        return this.meal;
    }

    public String getDescription(){
        return meal.getDescription();
    }
    public void setDescription(String value){
        meal.setDescription(value);
    }

    public String getDateTime(){
        return meal.getDateTime();
    }
    public void setDateTime(String value){
        meal.setDateTime(value);
    }

    public int getCalories(){
        return meal.getCalories();
    }
    public void setCalories(int value){
        meal.setCalories(value);
    }

    public CreateUpdateMealRequest(){
        this.meal = new Meal();
    }
}
