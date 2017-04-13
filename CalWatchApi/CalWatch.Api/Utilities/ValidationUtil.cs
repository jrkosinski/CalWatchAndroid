using System;

using CalWatch.Api.Models;
using CalWatch.Api.ViewModels;
using CalWatch.Api.Exceptions;
using CalWatch.Data;

namespace CalWatch.Api.Utilities
{
    /// <summary>
    /// Standard input validation.
    /// </summary>
    public static class ValidationUtil
    {
        public static void ValidateAndCleanCreateUser(User user)
        {
            //check for nulls 
            if (user.Username == null)
                throw new ValidationFailedException("Username cannot be null.");

            if (user.Password == null)
                throw new ValidationFailedException("Password cannot be null."); 

            //clean inputs 
            user.Username = user.Username.Trim();
            user.Password = user.Password.Trim();

            //permission level for new user must be "User" only
            user.PermissionLevel = UserPermissionLevel.User;

            //validate inputs
            if (!ValidateMinMaxLength(user.Username, AppSettings.UsernameMinLength, AppSettings.UsernameMaxLength))
                throw new ValidationFailedException(String.Format("Username length must be between {0} and {1}.", AppSettings.UsernameMinLength, AppSettings.UsernameMaxLength));

            if (!ValidateMinMaxLength(user.Password, AppSettings.PasswordMinLength, AppSettings.PasswordMaxLength))
                throw new ValidationFailedException(String.Format("Password length must be between {0} and {1}.", AppSettings.PasswordMinLength, AppSettings.PasswordMaxLength));
            
            //validate username & password format 
            if (!ValidateUsernameFormat(user.Username))
                throw new ValidationFailedException("Username format incorrect; should be alphanumeric only, no spaces or special chars except underscores.");

            if (!ValidatePasswordFormat(user.Password))
                throw new ValidationFailedException("Password format incorrect; may not contain spaces."); 

            //validate calories
            ValidateCalories(user.TargetCaloriesPerDay);
        }

        public static void ValidateAndCleanUpdateUser(User currentUser, User user)
        {
            //permission level for new user must be <= current user
            //if (user.PermissionLevel > currentUser.PermissionLevel)
            //    throw new UserNotAuthorizedException("Permission level cannot exceed that of current user.");

            ValidateCalories(user.TargetCaloriesPerDay);
        }

        public static void ValidateAndCleanCreateMeal(Meal meal)
        {
            //clean inputs 
            if (meal.Description == null)
                meal.Description = String.Empty;

            meal.Description = meal.Description.Trim();

            //validate inputs
            if (!ValidateMinMaxLength(meal.Description, AppSettings.DescriptionMinLength, AppSettings.DescriptionMaxLength))
                throw new ValidationFailedException(String.Format("Description length must be between {0} and {1}.", AppSettings.DescriptionMinLength, AppSettings.DescriptionMaxLength));

            ValidateCalories(meal.Calories);

            if (meal.DateTime == default(System.DateTime))
                throw new ValidationFailedException("DateTime is a required field.");

            ValidateDateTime(meal.DateTime) ;
        }

        public static void ValidateAndCleanUpdateMeal(Meal meal)
        {
            ValidateAndCleanCreateMeal(meal);
        }

        public static void ValidateUserViewModel(UserViewModel viewModel)
        {
            ValidateIntProperty(viewModel.TargetCaloriesPerDay, "TargetCaloriesPerDay");

            /*
            if (!String.IsNullOrEmpty(viewModel.PermissionLevel))
            {
                UserPermissionLevel permissionLevel;
                if (!Enum.TryParse<UserPermissionLevel>(viewModel.PermissionLevel, out permissionLevel))
                    throw new ValidationFailedException("Invalid value for PermissionLevel."); 
            }*/
        }

        public static void ValidateMealViewModel(MealViewModel viewModel)
        {
            ValidateIntProperty(viewModel.Id, "Id");
            ValidateIntProperty(viewModel.UserId, "UserId");
            ValidateIntProperty(viewModel.Calories, "Calories");
            ValidateDateTimeProperty(viewModel.DateTime, "DateTime"); 
        }


        private static bool ValidateMinMaxLength(string value, int min, int max)
        {
            return (value.Length >= min && value.Length <= max); 
        }

        private static bool ValidateValuePositive(int value)
        {
            return (value >= 0); 
        }

        private static void ValidateCalories(int value)
        {
            if (value < AppSettings.MinCaloriesValue || value > AppSettings.MaxCaloriesValue)
                throw new ValidationFailedException(String.Format("Target calories per day must be a number between {0} and {1}.", AppSettings.MinCaloriesValue, AppSettings.MaxCaloriesValue)); 
        }

        private static void ValidateDateTime(DateTime dateTime)
        {
            if (dateTime < AppSettings.MinDateValue || dateTime > AppSettings.MaxDateValue)
                throw new ValidationFailedException(String.Format("DateTime must be between {0} and {1}.",
                    DateTimeUtil.DateToString(AppSettings.MinDateValue),
                    DateTimeUtil.DateToString(AppSettings.MaxDateValue)
                    ));
        }

        private static void ValidateIntProperty(string propertyValue, string propertyName)
        {
            int intValue;
            if (!String.IsNullOrEmpty(propertyValue))
            {
                if (!Int32.TryParse(propertyValue, out intValue))
                    throw new ValidationFailedException(String.Format("Invalid value for {0}.", propertyName));
            }
        }

        private static void ValidateDateTimeProperty(string propertyValue, string propertyName)
        {
            if (!String.IsNullOrEmpty(propertyValue))
            {
                if (DateTimeUtil.StringToDateTime(propertyValue) == null)
                    throw new ValidationFailedException(String.Format("Invalid value for {0}. Format is " + AppSettings.DateTimeFormat, propertyName));
            }
        }

        private static bool ValidateUsernameFormat(string username)
        {
            return System.Text.RegularExpressions.Regex.IsMatch(username, AppSettings.UsernameFormat); 
        }

        private static bool ValidatePasswordFormat(string password)
        {
            return (!password.Contains(" ")); 
        }
    }
}
