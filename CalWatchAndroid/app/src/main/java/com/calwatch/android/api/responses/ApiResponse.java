package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.ErrorInfo;

/**
 * Created by John R. Kosinski on 22/1/2559.
 * Base class for response objects.
 */
public class ApiResponse {
    private ErrorInfo errorInfo;
    private int responseCode;

    public ApiResponse(){}

    public ApiResponse(int responseCode)
    {
        this.setResponseCode(responseCode);
    }

    public boolean hasError(){
        return this.errorInfo != null;
    }
    public boolean isSuccessful() {
        return !this.hasError() && getResponseCode() >= 200 && getResponseCode() < 300;
    }

    public int getResponseCode() { return this.responseCode;}

    public void setResponseCode(int value) { this.responseCode = value;}

    public ErrorInfo getErrorInfo(){
        return errorInfo;
    }

    public void setErrorInfo(ErrorInfo value){
        this.errorInfo = value;
    }
}
