package com.calwatch.android.api.models;

import com.calwatch.android.util.DateTimeUtil;

import org.joda.time.DateTime;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class Meal {
    private int id;
    private int userId;
    private int calories;
    private int totalCaloriesForDay;
    private String description;
    private String dateTime;
    private DateTime dateTimeObject;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int value) {
        this.userId = value;
    }

    public int getTotalCaloriesForDay() {
        return totalCaloriesForDay;
    }
    public void setTotalCaloriesForDay(int value) {
        this.totalCaloriesForDay = value;
    }

    public int getCalories() {
        return calories;
    }
    public void setCalories(int value) {
        this.calories = value;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String value) {
        this.description = value;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String value) {
        this.dateTime = value;
        this.dateTimeObject = DateTimeUtil.StringToDateTime(value);
    }

    //TODO: figure out how to serialize this more naturally
    public DateTime getDateTimeObject() {
        if (dateTimeObject == null) {
            this.dateTimeObject = DateTimeUtil.StringToDateTime(this.dateTime);
        }

        return dateTimeObject;
    }
    public void setDateTimeObject(DateTime value) {
        this.dateTimeObject = value;
        this.dateTime = DateTimeUtil.DateTimeToString(value);
    }
}
