package com.calwatch.android.api.models;

/**
 * Created by John R. Kosinski on 23/1/2559.
 */
public class FilterParams {
    private String dateFrom;
    private String dateTo;
    private String timeFrom;
    private String timeTo;

    public String getDateFrom() { return dateFrom;}
    public void setDateFrom(String dateFrom) {
        if (dateFrom == null || dateFrom.length() == 0)
            dateFrom = null;
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }
    public void setDateTo(String dateTo) {
        if (dateTo == null || dateTo.length() == 0)
            dateTo = null;
        this.dateTo = dateTo;
    }

    public String getTimeFrom() {
        return timeFrom;
    }
    public void setTimeFrom(String timeFrom) {

        if (timeFrom == null || timeFrom.length() == 0)
            timeFrom = null;
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }
    public void setTimeTo(String timeTo) {

        if (timeTo == null || timeTo.length() == 0)
            timeTo = null;
        this.timeTo = timeTo;
    }

    public boolean isEmpty()
    {
        return ((dateFrom == null || dateFrom.length() == 0) &&
                (dateTo == null || dateTo.length() == 0) &&
                (timeFrom == null || timeFrom.length() == 0) &&
                (timeTo == null || timeTo.length() == 0)
        );
    }


    public FilterParams(){}

    public FilterParams(String dateFrom, String dateTo, String timeFrom, String timeTo)
    {
        this.setDateFrom(dateFrom);
        this.setDateTo(dateTo);
        this.setTimeFrom(timeFrom);
        this.setTimeTo(timeTo);
    }
}
