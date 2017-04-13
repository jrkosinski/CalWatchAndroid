package com.calwatch.android.api.models;

/**
 * Created by John R. Kosinski on 30/1/2559.
 */
public class ReportDay {
    private String date;
    private int totalCaloriesForPeriod;
    private int totalCaloriesForDay;
    private boolean overDailyTarget;

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public int getTotalCaloriesForPeriod() {
        return totalCaloriesForPeriod;
    }
    public void setTotalCaloriesForPeriod(int totalCaloriesForPeriod) {
        this.totalCaloriesForPeriod = totalCaloriesForPeriod;
    }

    public int getTotalCaloriesForDay() {
        return totalCaloriesForDay;
    }
    public void setTotalCaloriesForDay(int totalCaloriesForDay) {
        this.totalCaloriesForDay = totalCaloriesForDay;
    }

    public boolean getOverDailyTarget() {
        return overDailyTarget;
    }
    public void setOverDailyTarget(boolean overDailyTarget) {
        this.overDailyTarget = overDailyTarget;
    }
}
