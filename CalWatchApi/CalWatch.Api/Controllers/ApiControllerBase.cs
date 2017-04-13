using System;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Mvc;

using CalWatch.Api.Exceptions;
using CalWatch.Api.Models;
using CalWatch.Api.Utilities;
using CalWatch.Api.ViewModels;


namespace CalWatch.Api.Controllers
{
    public abstract class ApiControllerBase : ApiController
    {
        /// <summary>
        /// Gets the value of the AuthToken header in current request. 
        /// </summary>
        public string AuthToken
        {
            get {
                return AuthTokenUtil.GetAuthTokenFromRequestHeaders(Request.Headers); 
            }
        }

        /// <summary>
        /// Wrap around alll controller request handling code. 
        /// </summary>
        /// <param name="f"></param>
        /// <returns></returns>
        protected HttpResponseMessage WrapInTryCatch(Func<HttpResponseMessage> f)
        {
            try
            {
                return f();
            }

            catch (UserNotAuthorizedException notAuthorizedException)
            {
                return CreateErrorResponse(HttpStatusCode.Forbidden, notAuthorizedException);
            }
            catch (EntityNotFoundException notFoundException)
            {
                return CreateErrorResponse(HttpStatusCode.NotFound, notFoundException);
            }
            catch (AuthTokenInvalidException authInvalidException)
            {
                return CreateErrorResponse(HttpStatusCode.Forbidden, authInvalidException);
            }
            catch (AuthExpiredException authExpiredException)
            {
                return CreateErrorResponse(HttpStatusCode.Unauthorized, authExpiredException);
            }
            catch (ValidationFailedException validationException)
            {
                return CreateErrorResponse(HttpStatusCode.BadRequest, validationException);
            }
            catch (DuplicateEntityException duplicateException)
            {
                return CreateErrorResponse(HttpStatusCode.Conflict, duplicateException);
            }
            catch (AuthenticationException authException)
            {
                return CreateErrorResponse(HttpStatusCode.Conflict, authException);
            }
            catch (Exception e)
            {
                return CreateErrorResponse(HttpStatusCode.InternalServerError, e);
            }
        }

        /// <summary>
        /// Returns a standard error response with the given exception info & response code. 
        /// </summary>
        /// <param name="status"></param>
        /// <param name="exception"></param>
        /// <returns></returns>
        protected HttpResponseMessage CreateErrorResponse(HttpStatusCode status, Exception exception)
        {
            var content = new ExceptionViewModel() { Message = exception.Message };
            return CreateResponseWithContent<ExceptionViewModel>(status, content); 
        }

        /// <summary>
        /// Returns response message with JSON content. 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="status"></param>
        /// <param name="content"></param>
        /// <returns></returns>
        protected HttpResponseMessage CreateResponseWithContent<T>(HttpStatusCode status, T content)
        {
            HttpResponseMessage response = new HttpResponseMessage(status);
            response.Content = new ObjectContent<T>(content, new System.Net.Http.Formatting.JsonMediaTypeFormatter());
            return response;
        }

        /// <summary>
        /// Extracts querystring params used for filtering requests (dateFrom, dateTo, etc.) 
        /// </summary>
        /// <returns></returns>
        protected FilterParams GetFilterParams()
        {
            FilterParams output = null;

            var queryString = ControllerContext.Request.GetQueryNameValuePairs();

            var dateFrom = queryString.FirstOrDefault(k => k.Key == "dateFrom");
            var dateTo = queryString.FirstOrDefault(k => k.Key == "dateTo");
            var timeFrom = queryString.FirstOrDefault(k => k.Key == "timeFrom");
            var timeTo = queryString.FirstOrDefault(k => k.Key == "timeTo");

            DateTime? dateFromDate = DateTimeUtil.StringToDate(dateFrom.Value);
            DateTime? dateToDate = DateTimeUtil.StringToDate(dateTo.Value);
            TimeSpan? timeFromTime = DateTimeUtil.StringToTime(timeFrom.Value);
            TimeSpan? timeToTime = DateTimeUtil.StringToTime(timeTo.Value);

            if (dateFromDate != null || dateToDate != null ||
                timeFromTime != null || timeToTime != null)
            {
                output = new FilterParams(
                    dateFromDate,
                    dateToDate,
                    timeFromTime,
                    timeToTime
                );
            }

            return output;
        }
    }
}
