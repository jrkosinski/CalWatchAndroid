package com.calwatch.android.tasks;

import com.calwatch.android.api.responses.ApiResponse;

/**
 * Created by John R. Kosinski on 22/1/2559.
 */
public interface IApiResponseCallback<T extends ApiResponse> {
    void onFinished(T response);
}
