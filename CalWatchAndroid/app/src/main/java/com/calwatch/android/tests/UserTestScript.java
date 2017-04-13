package com.calwatch.android.tests;

import org.junit.FixMethodOrder;
import org.junit.Test;

import com.calwatch.android.api.ApiService;
import com.calwatch.android.tests.testsupport.TestLogic;

import org.junit.runners.MethodSorters;

/**
 * Created by John R. Kosinski on 27/1/2559.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTestScript {

    @Test
    public void a_signUp()
    {
        ApiService.configure("http://192.168.1.36:31716/api/v/1/");

        TestLogic.signUp();
    }

    @Test
    public void b_logOut()
    {
        TestLogic.logOut();
    }

    @Test
    public void c_logIn()
    {
        TestLogic.logIn();
    }

    @Test
    public void d_createMeals()
    {
        TestLogic.createMeals();
    }

    @Test
    public void e_updateMeal()
    {
        TestLogic.updateMeal();
    }

    @Test
    public void f_deleteMeals()
    {
        TestLogic.deleteMeals();
    }

    @Test
    public void g_filterMeals()
    {
        TestLogic.filterMeals();
    }

    @Test
    public void h_caloriesPerDay()
    {
        TestLogic.caloriesPerDay();
    }

    @Test
    public void i_securityTest()
    {
        TestLogic.securityTest();
    }
}
