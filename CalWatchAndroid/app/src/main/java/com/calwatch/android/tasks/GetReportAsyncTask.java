package com.calwatch.android.tasks;

import com.calwatch.android.api.models.FilterParams;
import com.calwatch.android.api.responses.ReportResponse;
import android.content.Context;

import com.calwatch.android.api.ApiService;
import android.util.Log;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public class GetReportAsyncTask extends AsyncApiTask<Integer, Integer, ReportResponse> {
    private static final String LogTag = "GetReportAsyncTask";
    private final FilterParams filterParams;

    @Override
    protected String getLogTag() { return LogTag;}

    /**
     * Default constructor
     */
    public GetReportAsyncTask(final Context context, final FilterParams filterParams, final IApiResponseCallback callback) {
        super(context, callback);
        this.filterParams = filterParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ReportResponse doInBackground(final Integer... userId) {
        ReportResponse response = null;
        try {
            response = ApiService.getReport(userId[0], this.filterParams);
        }
        catch (final Exception e) {
            Log.e(LogTag, e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(final ReportResponse response) {
        super.onPostExecute(response);
    }
}
