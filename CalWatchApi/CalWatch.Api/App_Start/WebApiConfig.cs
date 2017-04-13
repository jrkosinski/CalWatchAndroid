using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

namespace CalWatch.Api
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/v/1/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

            config.Routes.MapHttpRoute(
                name: "ChildApi",
                routeTemplate: "api/v/1/{parentController}/{parentId}/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );
        }
    }
}
