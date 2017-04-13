package com.calwatch.android.tests.testsupport;

import com.calwatch.android.api.models.Meal;
import com.calwatch.android.api.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John R. Kosinski on 27/1/2559.
 */
public class TestData {
    public static User testUser;
    public static String password = "12345";
    public static List<Meal> meals = new ArrayList<Meal>();

    public static User getTestUser() {
        return testUser;
    }
    public static void setTestUser(User testUser) {
        TestData.testUser = testUser;
    }

    public static String getPassword() {
        return password;
    }
    public static void setPassword(String value)  {
        password = value;
    }

    public static List<Meal> getMeals() {
        return meals;
    }
    public static void setMeals(List<Meal> meals) {
        TestData.meals = meals;
    }
}
