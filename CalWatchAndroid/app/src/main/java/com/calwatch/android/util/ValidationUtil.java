package com.calwatch.android.util;

import com.calwatch.android.storage.LocalStorage;

/**
 * Created by John R. Kosinski on 29/1/2559.
 * Utilities for validating input.
 */
public class ValidationUtil
{
    public static String validatePassword(String password)
    {
        if (password.length() == 0)
        return "password required";
        if (password.length() < LocalStorage.getAppSettings().getPasswordMinLength())
            return "minimum length is " + Integer.toString(LocalStorage.getAppSettings().getPasswordMinLength());
        if (password.length() > LocalStorage.getAppSettings().getPasswordMaxLength())
            return "maximum length is " + Integer.toString(LocalStorage.getAppSettings().getPasswordMaxLength());
        if (password.contains(" "))
            return "may not contain spaces";
        return "";
    }

    public static String validateUsername(String username)
    {
        if (username.length() == 0)
            return "username required";
        if (username.length() < LocalStorage.getAppSettings().getUsernameMinLength())
            return "minimum length is " + Integer.toString(LocalStorage.getAppSettings().getUsernameMinLength());
        if (username.length() > LocalStorage.getAppSettings().getUsernameMaxLength())
            return "maximum length is " + Integer.toString(LocalStorage.getAppSettings().getUsernameMaxLength());
        if (!StringUtil.matchesPattern(username, LocalStorage.getAppSettings().getUsernameFormat())) // "^[a-zA-Z0-9_]*$"))
            return "contains invalid characters";
        return "";
    }

    public static String validateDescription(String description)
    {
        if (description.length() == 0)
            return "description is required";
        if (description.length() < LocalStorage.getAppSettings().getMealDescriptionMinLength())
            return "minimum length is " + Integer.toString(LocalStorage.getAppSettings().getMealDescriptionMinLength());
        if (description.length() > LocalStorage.getAppSettings().getMealDescriptionMaxLength())
            return "maximum length is " + Integer.toString(LocalStorage.getAppSettings().getMealDescriptionMaxLength());
        return "";
    }

    public static String validateCalories(String calories)
    {
        int n =0;
        try   {
            n = Integer.parseInt(calories);
        }
        catch (Exception e)  {
            return "enter a valid number";
        }

        if (n < LocalStorage.getAppSettings().getMinCaloriesValue())
            return "minimum calories is " + Integer.toString(LocalStorage.getAppSettings().getMinCaloriesValue());
        if (n > LocalStorage.getAppSettings().getMaxCaloriesValue())
            return "maximum calories is " + Integer.toString(LocalStorage.getAppSettings().getMaxCaloriesValue());

        return "";
    }
}
