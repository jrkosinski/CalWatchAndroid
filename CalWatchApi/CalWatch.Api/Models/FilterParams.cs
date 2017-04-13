using System;

namespace CalWatch.Api.Models
{
    public class FilterParams
    {
        public DateTime DateFrom { get; private set; }
        public DateTime DateTo { get; private set; }
        public TimeSpan TimeFrom { get; private set; }
        public TimeSpan TimeTo { get; private set; }

        public FilterParams(
            DateTime? dateFrom = null,
            DateTime? dateTo = null,
            TimeSpan? timeFrom = null,
            TimeSpan? timeTo = null
        )
        {
            this.SetDateFrom(dateFrom);
            this.SetDateTo(dateTo);
            this.SetTimeFrom(timeFrom);
            this.SetTimeTo(timeTo);
        }

        public void SetDateFrom(DateTime? value)
        {
            if (value == null)
                value = DateTime.MinValue;

            this.DateFrom = value.Value;
        }

        public void SetDateTo(DateTime? value)
        {
            if (value == null)
                value = DateTime.MaxValue;

            this.DateTo = value.Value;
        }

        public void SetTimeFrom(TimeSpan? value)
        {
            if (value == null)
                value = TimeSpan.Parse("00:00:00");

            this.TimeFrom = value.Value;
        }

        public void SetTimeTo(TimeSpan? value)
        {
            if (value == null)
                value = TimeSpan.Parse("23:59:59");

            this.TimeTo = value.Value;
        }
    }
}
