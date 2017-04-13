using System.Net;
using System.Net.Http;

using CalWatch.Api.Models;
using CalWatch.Api.Utilities;
using CalWatch.Api.Services;
using CalWatch.Api.ViewModels;

namespace CalWatch.Api.Controllers
{
    public class ReportController : ApiControllerBase
    {
        // GET /users/x/report
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Get(int parentId)
        {
            return this.WrapInTryCatch(() =>
            {
                //get params
                var filterParams = GetFilterParams();

                //create report
                Report report = ReportService.GetReport(this.AuthToken, parentId, filterParams);

                //user not found 
                if (report == null)
                    return new HttpResponseMessage(HttpStatusCode.NotFound);

                //no content 
                //if (report.Days.Count == 0)
                //    return CreateResponseWithContent<ReportViewModel>(HttpStatusCode.NoContent, MappingLayer.ReportToViewModel(report));

                //return response 
                return new HttpResponseMessage()
                {
                    Content = new JsonContent(MappingLayer.ReportToViewModel(report))
                };
            });
        }
    }
}
