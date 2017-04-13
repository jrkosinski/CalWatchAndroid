using System;

using Newtonsoft.Json;

namespace CalWatch.Api.ViewModels
{
    public class AppSettingsViewModel
    {
        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "serverStatus")]
        public string ServerStatus { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "usernameMinLength")]
        public int UsernameMinLength { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "usernameMaxLength")]
        public int UsernameMaxLength { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "passwordMinLength")]
        public int PasswordMinLength { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "passwordMaxLength")]
        public int PasswordMaxLength { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "mealDescriptionMinLength")]
        public int MealDescriptionMinLength { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "mealDescriptionMaxLength")]
        public int MealDescriptionMaxLength { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "minCaloriesValue")]
        public int MinCaloriesValue { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "maxCaloriesValue")]
        public int MaxCaloriesValue { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "minDate")]
        public string MinDateValue { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "maxDate")]
        public string MaxDateValue { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "dateTimeFormat")]
        public string DateTimeFormat { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "dateFormat")]
        public string DateFormat { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "timeFormat")]
        public string TimeFormat { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "usernameFormat")]
        public string UsernameFormat { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "passwordFormat")]
        public string PasswordFormat { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "sessionExpirationMinutes")]
        public string SessionExpirationMinutes { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "usersRoute")]
        public string UsersRoute { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "userRoute")]
        public string UserRoute { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "mealsRoute")]
        public string MealsRoute { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "mealRoute")]
        public string MealRoute { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "reportRoute")]
        public string ReportRoute { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "loginRoute")]
        public string LoginRoute { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "logoutRoute")]
        public string LogoutRoute { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "apiVersion")]
        public string ApiVersion { get; set; }
    }
}
