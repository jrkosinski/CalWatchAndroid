using System;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;

using CalWatch.Api.ViewModels;
using CalWatch.Api.Utilities;
using CalWatch.Api.Services;

namespace CalWatch.Api.Controllers
{
    public class LoginController : ApiControllerBase
    {
        // POST /login
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public async Task<HttpResponseMessage> Post(HttpRequestMessage request)
        {
            string jsonString = null;
            try
            {
                jsonString = await request.Content.ReadAsStringAsync();
            }
            catch (Exception)
            {
                return new HttpResponseMessage(HttpStatusCode.BadRequest);
            }

            return this.WrapInTryCatch(() =>
            {
                if (jsonString != null)
                {
                    LoginViewModel viewModel = JsonUtil.JsonToObject<LoginViewModel>(jsonString);
                    if (viewModel != null)
                    {
                        //authenticate user 
                        var user = UserService.Authenticate(viewModel.Username, viewModel.Password);

                        //return user object on successful login
                        var output = MappingLayer.UserToViewModel(user);
                        output.AuthToken = user.AuthToken;
                        return this.CreateResponseWithContent<UserViewModel>(HttpStatusCode.OK, output);
                    }
                    else
                        return new HttpResponseMessage(HttpStatusCode.BadRequest);
                }
                else
                    return new HttpResponseMessage(HttpStatusCode.BadRequest);
            });
        }
    }
}
