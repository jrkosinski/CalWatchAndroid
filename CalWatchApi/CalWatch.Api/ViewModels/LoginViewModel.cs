using System;

using Newtonsoft.Json;

namespace CalWatch.Api.ViewModels
{
    public class LoginViewModel : ViewModelBase
    {
        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "username")]
        public string Username { get; set; }

        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "password")]
        public string Password { get; set; }
    }
}
