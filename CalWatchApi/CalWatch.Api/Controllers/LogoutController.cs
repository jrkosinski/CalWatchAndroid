using System.Net;
using System.Net.Http;

using CalWatch.Api.Services;

namespace CalWatch.Api.Controllers
{
    public class LogoutController : ApiControllerBase
    {
        // POST /users
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Post(HttpRequestMessage request)
        {
            return this.WrapInTryCatch(() =>
            {
                UserService.Logout(this.AuthToken);
                return new HttpResponseMessage(HttpStatusCode.OK);
            });
        }
    }
}
