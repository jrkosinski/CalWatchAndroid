package com.calwatch.android.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by John R. Kosinski on 29/1/2559.
 * Used in auto-validating form elements.
 */
public class FormValidationElement
{
    private EditText editText;
    private TextView errorLabel;
    private ValidationCallback callback;
    private FormValidationManager parent;

    public void setParent(FormValidationManager parent) { this.parent = parent;}

    public FormValidationElement(EditText editText, TextView errorLabel, boolean validateWhileTyping, ValidationCallback callback) {
        this.editText = editText;
        this.callback = callback;
        this.errorLabel = errorLabel;

        if (validateWhileTyping)
        {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    validate(true);

                    if (parent != null)
                        parent.validateForm(false, true);
                }
            });
        }
    }

    public boolean validate(boolean showError)
    {
        boolean isValid = false;
        if (callback != null) {
            String errorMessage = callback.validate(editText);
            isValid = (errorMessage == null || errorMessage.length() == 0);

            if (showError)
                callback.setErrorState(editText, errorMessage, errorLabel);
        }

        return isValid;
    }

    public void disable()
    {
        if (editText != null)
            editText.setEnabled(false);
    }
}
