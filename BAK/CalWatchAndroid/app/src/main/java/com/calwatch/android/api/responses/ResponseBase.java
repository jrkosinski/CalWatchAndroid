package com.calwatch.android.api.responses;

import com.calwatch.android.api.models.ErrorInfo;

/**
 * Created by Home on 22/1/2559.
 */
public class ResponseBase {
    private ErrorInfo errorInfo;
    private int responseCode;

    public boolean hasError(){
        return this.errorInfo != null;
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
