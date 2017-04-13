using System;
using System.Collections.Generic;

using CalWatch.Api.Models;
using CalWatch.Api.Utilities;
using CalWatch.Api.Exceptions;
using CalWatch.Data;


namespace CalWatch.Api.Services
{
    public class ReportService : ServiceBase
    {
        public static Report GetReport(string authToken, int userId, FilterParams filterParams)
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //check security permissions
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.GetReport, userId))
                throw new UserNotAuthorizedException("Current user is not authorized to get reports for other users.");

            //get data, and generate report 
            Report report = new Report();

            var user = currentUser;
            if (user.Id != userId)
                user = AppDbContext.GetUser(userId); 

            var meals = MealService.GetMealsFiltered(authToken, userId, filterParams);
            return GenerateReport(user, meals, filterParams);
        }

        private static Report GenerateReport(User user, List<Meal> meals, FilterParams filterParams)
        {
            Report output = new Report();

            //create report from list of meals 
            if (meals != null && meals.Count > 0)
            {
                ReportDay currentDay = new ReportDay();
                currentDay.Date = meals[0].DateTime;

                foreach (var meal in meals)
                {
                    if (!meal.DateTime.IsSameDay(currentDay.Date))
                    {
                        output.Days.Add(currentDay);
                        if (currentDay.OverDailyTarget)
                            output.NumberOfDaysOverTarget++;

                        currentDay = new ReportDay();
                        currentDay.Date = meal.DateTime;
                    }

                    //tally up the calories 
                    currentDay.TotalCaloriesForPeriod += meal.Calories;
                    currentDay.TotalCaloriesForDay = meal.TotalCaloriesForDay;
                    currentDay.OverDailyTarget = (user.TargetCaloriesPerDay < currentDay.TotalCaloriesForDay);

                    output.TotalCalories += meal.Calories;
                }

                //add last day 
                output.Days.Add(currentDay);
                if (currentDay.OverDailyTarget)
                    output.NumberOfDaysOverTarget++;

                //calculate average 
                output.AverageCaloriesPerDay = (long)output.TotalCalories / output.Days.Count;
            }

            output.FilterParams = filterParams;

            return output; 
        }
    }
}
