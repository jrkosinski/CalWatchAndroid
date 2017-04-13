using System;

using Newtonsoft.Json;

namespace CalWatch.Api.ViewModels
{
    public class ExceptionViewModel : ViewModelBase
    {
        [JsonProperty(DefaultValueHandling=DefaultValueHandling.Ignore, PropertyName="message")]
        public string Message { get; set; }

        /*
        [JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore, PropertyName = "MessageDetail")]
        public string MessageDetail
        {
            get
            {
                return (messageDetail == null ? this.Message : messageDetail); 
            }
            set
            {
                messageDetail = value;
            }
        }
        */
    }
}
