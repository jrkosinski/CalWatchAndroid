package com.calwatch.android.tests;

import android.test.InstrumentationTestCase;

import com.calwatch.android.api.ApiService;

/**
 * Created by Home on 27/1/2559.
 */
public class MainTestScript extends InstrumentationTestCase {
    private String authToken;
    private int userId;

    public void signUp() throws Exception
    {

    }
    public void logOut() throws Exception
    {
        assertEquals(2, 1);
    }
    public void logBackIn() throws Exception
    {
        assertEquals(1, 1);
    }
    public void createMeals() throws Exception
    {
        assertEquals(1, 1);
    }
    public void updateMeal() throws Exception
    {
        assertEquals(1, 1);
    }
    public void filterMeals() throws Exception
    {
        assertEquals(1, 1);
    }
}
