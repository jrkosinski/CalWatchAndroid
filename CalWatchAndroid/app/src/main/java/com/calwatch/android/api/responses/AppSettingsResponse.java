package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.AppSettings;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class AppSettingsResponse extends ApiResponse {
    private AppSettings appSettings;

    public AppSettings getAppSettings() {
        return appSettings;
    }
    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    @Override
    public boolean isSuccessful()
    {
        return super.isSuccessful() && this.appSettings != null;
    }

    public AppSettingsResponse(int responseCode) {
        super.setResponseCode(responseCode);
    }
}
