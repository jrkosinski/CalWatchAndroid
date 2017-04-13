using System;

using Newtonsoft.Json;

namespace CalWatch.Api.ViewModels
{
    public class FilterParamsViewModel : ViewModelBase
    {
        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "dateFrom")]
        public string DateFrom { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "dateTo")]
        public string DateTo { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "timeFrom")]
        public string TimeFrom { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "timeTo")]
        public string TimeTo { get; set; }
    }
}
