package com.calwatch.android.tasks;

import com.calwatch.android.activities.ActivityBase;
import com.calwatch.android.activities.LoginActivity;
import com.calwatch.android.api.requests.LoginRequest;
import com.calwatch.android.api.responses.ApiResponse;
import com.calwatch.android.api.responses.UserResponse;
import com.calwatch.android.storage.LocalStorage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Abstract base class for async api-calling tasks.
 */
public abstract class AsyncApiTask<TParams, TProgress, TResult extends ApiResponse> extends AsyncTask<TParams, TProgress, TResult> {
    protected final Context context;
    protected final IApiResponseCallback callback;

    protected abstract String getLogTag();

    public AsyncApiTask(final Context context, final IApiResponseCallback callback)
    {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        showProgressSpinner();
    }

    @Override
    protected void onPostExecute(final ApiResponse response)
    {
        try {
            //task was cancelled
            if (isCancelled()) {
                hideProgressSpinner();
                return;
            }

            //on 401, attempt to log in again
            if (response != null && response.getResponseCode() == 401) {
                redirectToLogin();
            }
            //on success, call the callback
            else if (this.callback != null) {
                this.callback.onFinished(response);
                hideProgressSpinner();
            }
        } catch (Exception e)
        {
            Log.e(this.getLogTag(), "msg=" + e.getMessage());
            hideProgressSpinner();
        }
    }

    protected void showProgressSpinner()
    {
        ((ActivityBase)context).setProgressActivity(true);
    }

    protected void hideProgressSpinner()
    {
        ((ActivityBase) context).setProgressActivity(false);
    }

    private void redirectToLogin()
    {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        hideProgressSpinner();
    }
}
