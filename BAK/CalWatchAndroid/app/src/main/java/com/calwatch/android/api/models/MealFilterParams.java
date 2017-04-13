package com.calwatch.android.api.models;

/**
 * Created by Home on 23/1/2559.
 */
public class MealFilterParams {
    private String fromDate;
    private String toDate;
    private String fromTime;
    private String toTime;

    public String getFromDate() { return fromDate;}
    public void setFromDate(String fromDate) {
        if (fromDate == null || fromDate.length() == 0)
            fromDate = null;
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }
    public void setToDate(String toDate) {
        if (toDate == null || toDate.length() == 0)
            toDate = null;
        this.toDate = toDate;
    }

    public String getFromTime() {
        return fromTime;
    }
    public void setFromTime(String fromTime) {

        if (fromTime == null || fromTime.length() == 0)
            fromTime = null;
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }
    public void setToTime(String toTime) {

        if (toTime == null || toTime.length() == 0)
            toTime = null;
        this.toTime = toTime;
    }

    public boolean isEmpty()
    {
        return ((fromDate == null || fromDate.length() == 0) &&
                (toDate == null || toDate.length() == 0) &&
                (fromTime == null || fromTime.length() == 0) &&
                (toTime == null || toTime.length() == 0)
        );
    }
}
