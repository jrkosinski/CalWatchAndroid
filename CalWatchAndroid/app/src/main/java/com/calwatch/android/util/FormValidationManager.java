package com.calwatch.android.util;

import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by John R. Kosinski on 28/1/2559.
 * Used in auto-validating form elements.
 */
public class FormValidationManager {
    private Button submitButton;
    private ArrayList<FormValidationElement> formElements = new ArrayList<>();

    public void setSubmitButton(Button value)
    {
        submitButton = value;
    }

    public void addElement(FormValidationElement element)
    {
        element.setParent(this);
        formElements.add(element);
    }

    public boolean validateForm(boolean showErrors, boolean toggleButtonEnabled)
    {
        boolean formIsValid = true;
        for(FormValidationElement element : formElements)
        {
            if (!element.validate(showErrors))
                formIsValid = false;
        }

        if (toggleButtonEnabled && submitButton != null)   {
            submitButton.setEnabled(formIsValid);
        }

        return formIsValid;
    }

    public void disableForm()
    {
        for(FormValidationElement element : formElements)
        {
            element.disable();
        }
        if (submitButton != null)
            submitButton.setEnabled(false);
    }
}
