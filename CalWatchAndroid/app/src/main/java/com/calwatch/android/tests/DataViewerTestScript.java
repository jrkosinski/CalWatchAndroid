package com.calwatch.android.tests;

import org.junit.FixMethodOrder;
import org.junit.Test;

import com.calwatch.android.api.ApiService;
import com.calwatch.android.api.models.User;
import com.calwatch.android.tests.testsupport.TestData;
import com.calwatch.android.tests.testsupport.TestLogic;

import org.junit.runners.MethodSorters;

/**
 * Created by John R. Kosinski on 27/1/2559.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataViewerTestScript {

    @Test
    public void a_logIn()
    {
        ApiService.configure("http://192.168.1.36:31716/api/v/1/");

        TestData.setPassword("dataviewer");

        User dataViewerUser = new User();
        dataViewerUser.setUsername("DataViewer");
        dataViewerUser.setPassword(TestData.getPassword());
        TestData.setTestUser(dataViewerUser);

        TestLogic.logIn();
    }

    @Test
    public void b_createMeals()
    {
        TestLogic.createMeals();
    }

    @Test
    public void c_updateMeal()
    {
        TestLogic.updateMeal();
    }

    @Test
    public void d_deleteMeals()
    {
        TestLogic.deleteMeals();
    }

    @Test
    public void e_filterMeals()
    {
        TestLogic.filterMeals();
    }

    @Test
    public void f_caloriesPerDay()
    {
        TestLogic.caloriesPerDay();
    }

    @Test
    public void g_securityTest()
    {
        TestLogic.securityTest();
    }
}
