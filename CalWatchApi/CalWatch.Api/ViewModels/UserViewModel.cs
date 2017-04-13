using System;

using Newtonsoft.Json;

namespace CalWatch.Api.ViewModels
{
    public class UserViewModel : ViewModelBase
    {
        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "id")]
        public string Id { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "username")]
        public string Username { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "password")]
        public string Password { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "permissionLevel")]
        public string PermissionLevel { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "authToken")]
        public string AuthToken { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "targetCaloriesPerDay")]
        public string TargetCaloriesPerDay { get; set; }

        //[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "meals")]
        //public List<MealViewModel> Meals { get; private set; }

        public UserViewModel()
        {
            //this.Meals = new List<MealViewModel>(); 
        }
    }
}
