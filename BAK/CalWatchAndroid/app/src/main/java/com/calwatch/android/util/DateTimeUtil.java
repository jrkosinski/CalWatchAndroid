package com.calwatch.android.util;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Time;

/**
 * Created by Home on 23/1/2559.
 */
public class DateTimeUtil
{
    private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");

    public static String DateTimeToString(DateTime dateTime)
    {
        String output = "";
        try {
            if (dateTime != null) {
                output = dateTime.toString(dateTimeFormat);
            }
        }
        catch (Exception e){
            Log.e("DateTimeUtil", e.getMessage());
        }

        return output;
    }

    public static DateTime StringToDateTime(String value)
    {
        DateTime output = null;
        try {
            if (value != null && value.length() > 0)
                output = DateTime.parse(value, dateTimeFormat);
        }
        catch (Exception e){
            Log.e("DateTimeUtil", e.getMessage());
        }

        return output;
    }

    public static String DateToString(DateTime date)
    {
        String output = "";
        try {
            if (date != null) {
                output = date.toString(dateFormat);
            }
        }
        catch (Exception e){
            Log.e("DateTimeUtil", e.getMessage());
        }

        return output;
    }

    public static DateTime StringToDate(String value)
    {
        DateTime output = null;
        try {
            if (value != null && value.length() > 0)
                output = DateTime.parse(value, dateFormat);
        }
        catch (Exception e){
            Log.e("DateTimeUtil", e.getMessage());
        }

        return output;
    }

    public static String TimeToString(LocalTime time)
    {
        String output = "";
        try {
            if (time != null) {
                output = time.toString(timeFormat);
            }
        }
        catch (Exception e){
            Log.e("DateTimeUtil", e.getMessage());
        }

        return output;
    }

    public static LocalTime StringToTime(String value)
    {
        LocalTime output = null;
        try {
            if (value != null && value.length() > 0)
                output = LocalTime.parse(value, timeFormat);
        }
        catch (Exception e){
            Log.e("DateTimeUtil", e.getMessage());
        }

        return output;
    }
}
