using System;

using CalWatch.Api.Models;

//TODO: admin users should have ability to update userpermissions
namespace CalWatch.Api.Services
{
    /// <summary>
    /// Handles logic for determining whether user X has permission to perform action Y.
    /// </summary>
    public static class SecurityModel
    {
        public static bool UserHasPermission(User user, ActionType action, int? userId = null)
        {
            switch (action)
            {
                case ActionType.ViewUsers:
                    return (
                        user.PermissionLevel == UserPermissionLevel.Admin || 
                        user.PermissionLevel == UserPermissionLevel.DataViewer
                    );

                case ActionType.ViewUser:
                    return (
                        user.Id == userId ||
                        user.PermissionLevel == UserPermissionLevel.Admin || 
                        user.PermissionLevel == UserPermissionLevel.DataViewer
                    );

                case ActionType.GetReport:
                    return (
                        user.Id == userId ||
                        user.PermissionLevel == UserPermissionLevel.Admin ||
                        user.PermissionLevel == UserPermissionLevel.DataViewer
                    );

                case ActionType.UpdateUser:
                    return (
                        user.Id == userId ||
                        user.PermissionLevel == UserPermissionLevel.Admin
                    ); 

                case ActionType.CreateUser:
                    return true;

                case ActionType.CreateMeal:
                    return (
                        user.Id == userId ||
                        user.PermissionLevel == UserPermissionLevel.Admin
                    );

                case ActionType.UpdateMeal:
                    return (
                        user.Id == userId ||
                        user.PermissionLevel == UserPermissionLevel.Admin
                    );

                case ActionType.DeleteMeal:
                    return (
                        user.Id == userId ||
                        user.PermissionLevel == UserPermissionLevel.Admin
                    ); 
            }

            return false;
        }

        public enum ActionType 
        {
            ViewUsers, 
            ViewUser, 
            UpdateUser, 
            CreateUser, 
            CreateMeal, 
            UpdateMeal, 
            DeleteMeal, 
            GetReport
        }
    }
}
