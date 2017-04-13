package com.calwatch.android.api.models;

import com.calwatch.android.util.DateTimeUtil;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by Home on 21/1/2559.
 */
public class Meal {
    private int id;
    private int userId;
    private int calories;
    private String description;
    private String dateTime;
    private DateTime dateTimeObject;
    //private Date dateCreated;
    //private Date dateModified;

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

    /*
    public Date getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Date value) {
        this.dateCreated = value;
    }

    public Date getDateModified() {
        return dateModified;
    }
    public void setDateModified(Date value) {
        this.dateModified = value;
    }
    */
}
