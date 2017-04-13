using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;

using CalWatch.Api.Models;
using CalWatch.Api.ViewModels;
using CalWatch.Api.Utilities;
using CalWatch.Api.Services;

namespace CalWatch.Api.Controllers
{
    public class UsersController : ApiControllerBase
    {
        // GET /users
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Get()
        {
            return this.WrapInTryCatch(() =>
            {
                //get users 
                var output = UserService.GetUsers(this.AuthToken);

                //not found 
                if (output == null)
                    return new HttpResponseMessage(HttpStatusCode.NotFound);

                //no content 
                if (output.Count() == 0)
                    return new HttpResponseMessage(HttpStatusCode.NoContent);

                //map viewmodel
                List<UserViewModel> viewModels = new List<UserViewModel>();
                foreach (var user in output)
                    viewModels.Add(MappingLayer.UserToViewModel(user)); 

                //return output 
                return new HttpResponseMessage()
                {
                    Content = new JsonContent(viewModels)
                };
            }); 
        }

        // GET /users/x
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Get(int id)
        {
            return this.WrapInTryCatch(() =>
            {
                //get user 
                var output = UserService.GetUser(this.AuthToken, id);

                //not found 
                if (output == null)
                    return new HttpResponseMessage(HttpStatusCode.NotFound);

                //map & return response 
                return new HttpResponseMessage()
                {
                    Content = new JsonContent(MappingLayer.UserToViewModel(output))
                };
            });
        }

        // POST /users
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public async Task<HttpResponseMessage> Post(HttpRequestMessage request)
        {
            //read json input 
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
                    //map viewmodel to object
                    UserViewModel viewModel = JsonUtil.JsonToObject<UserViewModel>(jsonString);

                    if (viewModel != null)
                    {
                        //create user
                        User user = MappingLayer.ViewModelToUser(viewModel);
                        user = UserService.CreateUser(user);

                        //return response with auth token 
                        var output = MappingLayer.UserToViewModel(user);
                        output.AuthToken = user.AuthToken;

                        return this.CreateResponseWithContent<UserViewModel>(HttpStatusCode.Created, output);
                    }
                    else
                        return new HttpResponseMessage(HttpStatusCode.BadRequest);
                }
                else
                    return new HttpResponseMessage(HttpStatusCode.BadRequest);
            }); 
        }

        // PUT /users/x
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public async Task<HttpResponseMessage> Put(int id, HttpRequestMessage request)
        {
            //read json input 
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
                    //map json to object
                    UserViewModel viewModel = JsonUtil.JsonToObject<UserViewModel>(jsonString);

                    if (viewModel != null)
                    {
                        //update user object
                        User user = MappingLayer.ViewModelToUser(viewModel);
                        user = UserService.UpdateUser(this.AuthToken, id, user);

                        //return response 
                        return this.CreateResponseWithContent<UserViewModel>(HttpStatusCode.OK, MappingLayer.UserToViewModel(user));
                    }
                    else
                        return new HttpResponseMessage(HttpStatusCode.BadRequest);
                }
                else
                    return new HttpResponseMessage(HttpStatusCode.BadRequest);
            }); 
        }

        // DELETE /users/x
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Delete(int id, HttpRequestMessage request)
        {
            return this.WrapInTryCatch(() =>
            {
                //get user 
                UserService.DeleteUser(this.AuthToken, id);

                //map & return response 
                return new HttpResponseMessage(HttpStatusCode.NoContent);
            });
        }
    }
}
