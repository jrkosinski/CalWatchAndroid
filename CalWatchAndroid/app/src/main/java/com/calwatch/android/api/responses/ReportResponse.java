package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.Report;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class ReportResponse extends ApiResponse {
    private Report report;

    public Report getReport() {
        return report;
    }
    public void setReport(Report meal) {
        this.report = meal;
    }

    @Override
    public boolean isSuccessful()
    {
        return super.isSuccessful();
    }

    public ReportResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
