package com.calwatch.android.tasks;

import com.calwatch.android.activities.ActivityBase;
import com.calwatch.android.activities.LoginActivity;
import com.calwatch.android.api.responses.ApiResponse;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Home on 25/1/2559.
 */
public abstract class AsyncApiTask<TParams, TProgress, TResult extends ApiResponse> extends AsyncTask<TParams, TProgress, TResult> {
    protected final Context context;
    protected final IApiResponseCallback callback;

    protected abstract String getLogTag();

    public AsyncApiTask(Context context, IApiResponseCallback callback)
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

            //on 401, redirect to Login
            if (response != null && response.getResponseCode() == 401) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                hideProgressSpinner();
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
}
