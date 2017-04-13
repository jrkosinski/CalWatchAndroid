package com.calwatch.android.api.models;

import java.util.ArrayList;

/**
 * Created by John R. Kosinski on 30/1/2559.
 */
public class Report {
    private ArrayList<ReportDay> days = new ArrayList<>();
    private int totalCalories;
    private int averageCaloriesPerDay;
    private int numberOfDaysOverTarget;
    private FilterParams filterParams;

    public int getTotalCalories() {
        return totalCalories;
    }
    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public int getAverageCaloriesPerDay() {
        return averageCaloriesPerDay;
    }
    public void setAverageCaloriesPerDay(int averageCaloriesPerDay) {
        this.averageCaloriesPerDay = averageCaloriesPerDay;
    }

    public int getNumberOfDaysOverTarget() {
        return numberOfDaysOverTarget;
    }
    public void setNumberOfDaysOverTarget(int numberOfDaysOverTarget) {
        this.numberOfDaysOverTarget = numberOfDaysOverTarget;
    }

    public ArrayList<ReportDay> getDays() {
        return days;
    }
    public void setDays(ArrayList<ReportDay> days) {
        this.days = days;
    }

    public FilterParams getFilterParams(){
        return filterParams;
    }
    public void setFilterParams(FilterParams filterParams){
        this.filterParams = filterParams;
    }
}
