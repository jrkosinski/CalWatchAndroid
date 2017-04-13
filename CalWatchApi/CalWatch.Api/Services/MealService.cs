using System;
using System.Collections.Generic;

using CalWatch.Api.Models;
using CalWatch.Api.Utilities;
using CalWatch.Api.Exceptions;
using CalWatch.Data;

namespace CalWatch.Api.Services
{
    /// <summary>
    /// Service layer will 
    /// - Check user permissions 
    /// - Perform any necessary validation 
    /// - Perform any business logic 
    /// </summary>
    public class MealService : ServiceBase
    {
        public static List<Meal> GetMeals(string authToken, int userId)
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //check security permissions
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.ViewUser, userId))
                throw new UserNotAuthorizedException("Current user is not authorized to view other users or their meals.");

            return AppDbContext.GetMeals(userId);
        }

        public static List<Meal> GetMealsFiltered(
            string authToken, 
            int userId, 
            FilterParams filterParams
          )
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //check security permissions
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.ViewUser, userId))
                throw new UserNotAuthorizedException("Current user is not authorized to view other users or their meals.");

            return AppDbContext.GetMealsFiltered(userId, filterParams);
        }

        public static Meal GetMeal(string authToken, int userId, int mealId)
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //check security permissions
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.ViewUser, userId))
                throw new UserNotAuthorizedException("Current user is not authorized to view other users or their meals.");

            return AppDbContext.GetMeal(mealId); 
        }

        public static Meal CreateMeal(string authToken, int userId, Meal meal)
        {
            //get current user 
            var currentUser = AuthorizeUser(authToken);

            //check security permissions 
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.CreateMeal, userId))
                throw new UserNotAuthorizedException(String.Format("Current user not authorized to create meal for user {0}.", userId));

            //nput validation 
            ValidationUtil.ValidateAndCleanCreateMeal(meal);

            //create meal 
            return AppDbContext.CreateMeal(userId, meal);
        }

        public static Meal UpdateMeal(string authToken, int userId, int mealId, Meal meal)
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //check security permissions 
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.UpdateMeal, userId))
                throw new UserNotAuthorizedException(String.Format("Current user not authorized to update meal  {0}.", mealId));

            //input validation 
            ValidationUtil.ValidateAndCleanUpdateMeal(meal);

            //update meal 
            meal.Id = mealId;
            return AppDbContext.UpdateMeal(meal);
        }

        public static void DeleteMeal(string authToken, int userId, int mealId)
        {
            //get current user
            var currentUser = AuthorizeUser(authToken);

            //check security permissions 
            if (!SecurityModel.UserHasPermission(currentUser, SecurityModel.ActionType.DeleteMeal, userId))
                throw new UserNotAuthorizedException(String.Format("Current user not authorized to delete meals for user {0}.", userId));

            AppDbContext.DeleteMeal(mealId);
        }
    }
}
