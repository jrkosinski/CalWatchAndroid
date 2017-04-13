using System;
using System.Collections.Generic;

namespace CalWatch.Api.Models
{
    public class Report
    {
        private List<ReportDay> days = new List<ReportDay>(); 

        /// <summary>
        /// Total calories for entire report period.
        /// </summary>
        public long TotalCalories { get; set; }
        /// <summary>
        /// Average per day, in the time period requested.
        /// </summary>
        public long AverageCaloriesPerDay { get; set; }
        public int NumberOfDaysOverTarget { get; set; }
        /// <summary>
        /// List of days involved in compiling the report.
        /// </summary>
        public List<ReportDay> Days
        {
            get
            {
                return days;
            }
        }
        public FilterParams FilterParams { get; set; }
    }
}
