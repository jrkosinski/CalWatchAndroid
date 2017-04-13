using System.Net;
using System.Net.Http;

using CalWatch.Api.ViewModels;
using CalWatch.Api.Utilities;

namespace CalWatch.Api.Controllers
{
    public class AppSettingsController : ApiControllerBase
    {
        // GET /appsettings
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Get()
        {
            return this.WrapInTryCatch(() =>
            {
                AppSettingsViewModel output = new AppSettingsViewModel();

                output.DateFormat = AppSettings.DateFormat;
                output.TimeFormat = "HH:mm"; 
                output.DateTimeFormat = AppSettings.DateTimeFormat;
                output.MinDateValue = DateTimeUtil.DateToString(AppSettings.MinDateValue);
                output.MaxDateValue = DateTimeUtil.DateToString(AppSettings.MaxDateValue);
                output.MinCaloriesValue = AppSettings.MinCaloriesValue;
                output.MaxCaloriesValue = AppSettings.MaxCaloriesValue;
                output.UsernameMinLength = AppSettings.UsernameMinLength;
                output.UsernameMaxLength = AppSettings.UsernameMaxLength;
                output.PasswordMinLength = AppSettings.PasswordMinLength;
                output.PasswordMaxLength = AppSettings.PasswordMaxLength;
                output.MealDescriptionMinLength = AppSettings.DescriptionMinLength;
                output.MealDescriptionMaxLength = AppSettings.DescriptionMaxLength;
                output.UsernameFormat = AppSettings.UsernameFormat;
                output.PasswordFormat = AppSettings.PasswordFormat;
                output.SessionExpirationMinutes = AppSettings.SessionExpirationMinutes.ToString();

                output.ServerStatus = "UP";
                output.ApiVersion = "1.0";
                output.LoginRoute = "/login";
                output.LogoutRoute = "/logout";
                output.UsersRoute = "/users";
                output.UserRoute = "/users/{userId}";
                output.MealsRoute = "/users/{userId}/meals";
                output.MealRoute = "/users/{userId}/meals/{mealId}";
                output.ReportRoute = "/users/{userId}/report"; 

                return CreateResponseWithContent<AppSettingsViewModel>(HttpStatusCode.OK, output); 
            });
        }
    }
}
