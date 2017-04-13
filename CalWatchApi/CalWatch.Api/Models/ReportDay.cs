using System;

namespace CalWatch.Api.Models
{
    public class ReportDay
    {
        /// <summary>
        /// Date (day) of this report record.
        /// </summary>
        public DateTime Date { get; set; }
        /// <summary>
        /// Total calories consumed on the given day, in the given time period.
        /// </summary>
        public long TotalCaloriesForPeriod { get; set; }
        public long TotalCaloriesForDay { get; set; }
        /// <summary>
        /// Gets/sets a value indicating whether calories for today exceeded 
        /// daily target. 
        /// </summary>
        public bool OverDailyTarget { get; set; }
    }
}
