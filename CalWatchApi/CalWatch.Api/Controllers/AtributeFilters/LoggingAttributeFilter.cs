using System;
using System.Net;
using System.Web.Http.Controllers;
using System.Web.Http.Filters;

using CalWatch.Api.Utilities;
using NLog;

namespace CalWatch.Api.Controllers.AtributeFilters
{
    public class LoggingFilterAttribute : System.Web.Http.Filters.ActionFilterAttribute 
{
    public override void OnActionExecuting(HttpActionContext actionContext)
    {
        try{
            if (actionContext != null)
            {
                var request = actionContext.Request;

                string jsonString = String.Empty;
                string authToken = String.Empty;
                string uri = String.Empty;

                //get the auth token 
                authToken = AuthTokenUtil.GetAuthTokenFromRequestHeaders(actionContext.Request.Headers);
                    
                if (request.RequestUri != null)
                    uri = request.RequestUri.AbsoluteUri;

                //log the request
                LogManager.GetCurrentClassLogger().Log(LogLevel.Debug,
                    String.Format("AuthToken: {0} Request: {1} Body: {2}",
                        authToken,
                        uri,
                        jsonString
                    )
                );
            }
        }
        catch{}
    }

    public async override void OnActionExecuted(HttpActionExecutedContext actionExecutedContext)
    {
        try
        {
            if (actionExecutedContext != null)
            {
                //get request & response 
                var response = actionExecutedContext.Response;
                var request = actionExecutedContext.Request;

                if (request != null && response != null)
                {
                    string jsonString = String.Empty;
                    string uri = String.Empty;
                    HttpStatusCode statusCode = HttpStatusCode.Unused;

                    //get response code
                    statusCode = actionExecutedContext.Response.StatusCode;

                    //get any json content in the response
                    if (response.Content != null)
                        jsonString = await actionExecutedContext.Response.Content.ReadAsStringAsync();

                    //get auth token 
                    string authToken = AuthTokenUtil.GetAuthTokenFromRequestHeaders(request.Headers);

                    //get request uri
                    if (request.RequestUri != null)
                        uri = actionExecutedContext.Request.RequestUri.AbsoluteUri;

                    //log response 
                    LogManager.GetCurrentClassLogger().Log(LogLevel.Debug,
                        String.Format("AuthToken: {0} Request: {1} ResponseCode: {2} Response: {3}",
                            authToken,
                            uri,
                            statusCode,
                            jsonString
                        )
                    );
                }
            }
        }
        catch { }
    }
}}
