using System;
using System.Collections.Generic;

using Newtonsoft.Json;

namespace CalWatch.Api.ViewModels
{
    public class ReportViewModel : ViewModelBase
    {
        private List<ReportDayViewModel> days = new List<ReportDayViewModel>();

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "totalCalories")]
        public string TotalCalories { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "averageCaloriesPerDay")]
        public string AverageCaloriesPerDay { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "numberOfDaysOverTarget")]
        public string NumberOfDaysOverTarget { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "filterParams")]
        public FilterParamsViewModel FilterParams { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "days")]
        public List<ReportDayViewModel> Days
        {
            get { return days; }
        }
    }
}
