package com.calwatch.android.util;

import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by John R. Kosinski on 28/1/2559.
 */
public abstract class ValidationCallback
{
    public String validate(EditText editText) { return "";}

    public void setErrorState(EditText editText, String errorMessage, TextView errorLabel)
    {
        if (errorLabel != null)
            errorLabel.setText(errorMessage);
    }
}
