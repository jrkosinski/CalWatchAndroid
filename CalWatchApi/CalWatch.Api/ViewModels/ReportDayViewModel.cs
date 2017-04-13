using System;

using Newtonsoft.Json;

namespace CalWatch.Api.ViewModels
{
    public class ReportDayViewModel : ViewModelBase
    {
        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "date")]
        public string Date { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "totalCaloriesForPeriod")]
        public string TotalCaloriesForPeriod { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "totalCaloriesForDay")]
        public string TotalCaloriesForDay { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "overDailyTarget")]
        public string OverDailyTarget { get; set; }
    }
}
