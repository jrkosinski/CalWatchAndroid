using System;

using CalWatch.Api.Models;
using CalWatch.Api.ViewModels;
using CalWatch.Data;

namespace CalWatch.Api.Utilities
{
    /// <summary>
    /// Maps viewmodels to their corresponding business objects, and vice versa.
    /// </summary>
    public static class MappingLayer
    {
        public static User ViewModelToUser(UserViewModel viewModel)
        {
            User user = new User();

            ValidationUtil.ValidateUserViewModel(viewModel);

            //if (!String.IsNullOrEmpty(viewModel.Id))
            //    user.Id = Int32.Parse(viewModel.Id);

            user.Username = viewModel.Username;
            user.Password = viewModel.Password;

            if (!String.IsNullOrEmpty(viewModel.TargetCaloriesPerDay))
                user.TargetCaloriesPerDay = Int32.Parse(viewModel.TargetCaloriesPerDay);

            UserPermissionLevel permissionLevel;
            Enum.TryParse<UserPermissionLevel>(viewModel.PermissionLevel, out permissionLevel);
            user.PermissionLevel = permissionLevel;

            return user;
        }

        public static UserViewModel UserToViewModel(User user)
        {
            UserViewModel viewModel = new UserViewModel();

            viewModel.Id = user.Id.ToString();
            viewModel.Username = user.Username;
            viewModel.Password = "*******"; 
            viewModel.PermissionLevel = user.PermissionLevel.ToString();
            viewModel.TargetCaloriesPerDay = user.TargetCaloriesPerDay.ToString();

            return viewModel;
        }

        public static Meal ViewModelToMeal(MealViewModel viewModel)
        {
            Meal meal = new Meal();

            ValidationUtil.ValidateMealViewModel(viewModel);

            if (!String.IsNullOrEmpty(viewModel.Id))
                meal.Id = Int32.Parse(viewModel.Id);

            if (!String.IsNullOrEmpty(viewModel.Calories))
                meal.Calories = Int32.Parse(viewModel.Calories);

            meal.Description = viewModel.Description;

            DateTime? dateTime = DateTimeUtil.StringToDateTime(viewModel.DateTime);
            if (dateTime != null)
                meal.DateTime = dateTime.Value;

            return meal;
        }

        public static MealViewModel MealToViewModel(Meal meal)
        {
            MealViewModel viewModel = new MealViewModel();

            viewModel.Id = meal.Id.ToString();
            viewModel.DateTime = DateTimeUtil.DateTimeToString(meal.DateTime);
            viewModel.Calories = meal.Calories.ToString();
            viewModel.Description = meal.Description;
            viewModel.TotalCaloriesForDay = meal.TotalCaloriesForDay;
            viewModel.UserId = meal.UserId.ToString();

            return viewModel;
        }

        public static FilterParamsViewModel FilterParamsToViewModel(FilterParams filterParams)
        {
            FilterParamsViewModel viewModel = new FilterParamsViewModel();
            viewModel.DateFrom = DateTimeUtil.DateToString(filterParams.DateFrom);
            viewModel.DateTo = DateTimeUtil.DateToString(filterParams.DateTo);
            viewModel.TimeFrom = DateTimeUtil.TimeToString(filterParams.TimeFrom);
            viewModel.TimeTo = DateTimeUtil.TimeToString(filterParams.TimeTo);

            return viewModel;
        }

        public static ReportViewModel ReportToViewModel(Report report)
        {
            ReportViewModel viewModel = new ReportViewModel();

            viewModel.AverageCaloriesPerDay = report.AverageCaloriesPerDay.ToString();
            viewModel.TotalCalories = report.TotalCalories.ToString();
            viewModel.NumberOfDaysOverTarget = report.NumberOfDaysOverTarget.ToString();

            if (report.FilterParams != null)
                viewModel.FilterParams = MappingLayer.FilterParamsToViewModel(report.FilterParams);

            foreach (var day in report.Days)
            {
                ReportDayViewModel dayViewModel = new ReportDayViewModel(); 
                dayViewModel.Date = DateTimeUtil.DateToString(day.Date);
                dayViewModel.TotalCaloriesForPeriod = day.TotalCaloriesForPeriod.ToString();
                dayViewModel.TotalCaloriesForDay = day.TotalCaloriesForDay.ToString();
                dayViewModel.OverDailyTarget = day.OverDailyTarget.ToString().ToLower();

                viewModel.Days.Add(dayViewModel); 
            }

            return viewModel;
        }
    }
}
