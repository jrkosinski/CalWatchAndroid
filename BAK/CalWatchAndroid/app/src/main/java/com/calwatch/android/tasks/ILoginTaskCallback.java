package com.calwatch.android.tasks;

import com.calwatch.android.api.responses.UserResponse;

/**
 * Created by Home on 22/1/2559.
 */
public interface ILoginTaskCallback {
    void onFinished(UserResponse response);
}
