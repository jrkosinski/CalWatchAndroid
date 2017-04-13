package com.calwatch.android.util;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by John R. Kosinski on 23/1/2559.
 */
public class DateTimeUtil
{
    private static final String[] daysOfWeek = { "Su", "M", "T", "W", "Th", "F", "S"};

    //TODO: these formats should be used from AppSettings (from API), not hard-coded on client
    private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");

    public static final long SecondsPerYear = 31556952;

    public static String DateTimeToString(DateTime dateTime)
    {
        String output = "";
        try {
            if (dateTime != null) {
                output = dateTime.toString(dateTimeFormat);
                //output = "01/01/2015 00:00";
            }
        }
        catch (Exception e){
            return null;
        }

        return output;
    }

    public static DateTime StringToDateTime(String value)
    {
        DateTime output = null;
        try {
            if (!StringUtil.isNullOrEmpty(value)) {
                output = DateTime.parse(value, dateTimeFormat);
                //output = new DateTime(2015, 1, 1, 0, 0);
            }
        }
        catch (Exception e){
            return null;
        }

        return output;
    }

    public static String DateToString(DateTime date)
    {
        String output = "";
        try {
            if (date != null) {
                output = date.toString(dateFormat);
                //output = "01/01/2015";
            }
        }
        catch (Exception e){
            return null;
        }

        return output;
    }

    public static DateTime StringToDate(String value)
    {
        DateTime output = null;
        try {
            if (!StringUtil.isNullOrEmpty(value)) {
                output = DateTime.parse(value, dateFormat);
                //output = new DateTime(2015, 1, 1, 0, 0);
            }
        }
        catch (Exception e){
            return null;
        }

        return output;
    }

    public static String TimeToString(LocalTime time)
    {
        String output = "";
        try {
            if (time != null) {
                output = time.toString(timeFormat);
                //output = "00:00";
            }
        }
        catch (Exception e){
            return null;
        }

        return output;
    }

    public static LocalTime StringToTime(String value)
    {
        LocalTime output = null;
        try {
            if (!StringUtil.isNullOrEmpty(value)) {
                output = LocalTime.parse(value, timeFormat);
                //output = new LocalTime(0, 0, 0);
            }
        }
        catch (Exception e){
            return null;
        }

        return output;
    }

    public static String getDayOfWeek(DateTime value, boolean abbrev)
    {
        String output = "";
        if (value != null)
        {
            output = daysOfWeek[value.getDayOfWeek()-1];
            if (abbrev)
            {
                if (output.length() > 3)
                    output = output.substring(0, 3);
            }
        }
        return output;
    }

    public static boolean isSameDay(DateTime a, DateTime b)
    {
        if (a != null && b != null) {
            return (a.getYear() == b.getYear() &&
                    a.getMonthOfYear() == b.getMonthOfYear() &&
                    a.getDayOfMonth() == b.getDayOfMonth());
        }
        return false;
    }
}
