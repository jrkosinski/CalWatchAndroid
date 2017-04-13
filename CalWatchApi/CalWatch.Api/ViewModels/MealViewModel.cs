using System;

using Newtonsoft.Json;

namespace CalWatch.Api.ViewModels
{
    public class MealViewModel : ViewModelBase
    {
        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "id")]
        public string Id { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "userId")]
        public string UserId { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "calories")]
        public string Calories { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "description")]
        public string Description { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "totalCaloriesForDay")]
        public int TotalCaloriesForDay { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "dateTime")]
        public string DateTime { get; set; }
    }
}
