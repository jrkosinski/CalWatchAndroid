using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;

using CalWatch.Api.Models;
using CalWatch.Api.ViewModels;
using CalWatch.Api.Utilities;
using CalWatch.Api.Services;
using CalWatch.Data;

namespace CalWatch.Api.Controllers
{
    public class MealsController : ApiControllerBase
    {
        // GET /users/x/meals
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Get(int parentId)
        {
            return this.WrapInTryCatch(() =>
            {
                var filterParams = GetFilterParams();

                //get meals output  
                List<Meal> meals = null;
                if (filterParams != null)
                    meals = MealService.GetMealsFiltered(this.AuthToken, parentId, filterParams);
                else
                    meals = MealService.GetMeals(this.AuthToken, parentId);

                //if null, indicates user not found 
                if (meals == null)
                    return new HttpResponseMessage(HttpStatusCode.NotFound);

                if (meals.Count == 0)
                    return new HttpResponseMessage(HttpStatusCode.NoContent);

                //map viewmodel 
                List<MealViewModel> viewModels = new List<MealViewModel>();
                foreach (var meal in meals)
                    viewModels.Add(MappingLayer.MealToViewModel(meal));

                //return response 
                return new HttpResponseMessage()
                {
                    Content = new JsonContent(viewModels)
                };
            });
        }

        // GET /users/x/meals/y
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Get(int parentId, int id)
        {
            return this.WrapInTryCatch(() =>
            {
                //get the user 
                var user = UserService.GetUser(this.AuthToken, parentId);

                //user not found 
                if (user == null)
                    return new HttpResponseMessage(HttpStatusCode.NotFound);

                //get the meal 
                var meal = MealService.GetMeal(this.AuthToken, user.Id, id); 
                if (meal == null)
                    return new HttpResponseMessage(HttpStatusCode.NotFound);

                //map to viewmodel 
                MealViewModel output = MappingLayer.MealToViewModel(meal);

                //return response 
                return new HttpResponseMessage()
                {
                    Content = new JsonContent(output)
                };
            });
        }

        // POST /users/x/meals/
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public async Task<HttpResponseMessage> Post(int parentId, HttpRequestMessage request)
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
                    MealViewModel viewModel = JsonUtil.JsonToObject<MealViewModel>(jsonString);

                    if (viewModel != null)
                    {
                        //create meal & map viewmodel 
                        Meal meal = MappingLayer.ViewModelToMeal(viewModel);
                        meal = MealService.CreateMeal(this.AuthToken, parentId, meal);

                        //return response 
                        return this.CreateResponseWithContent<MealViewModel>(HttpStatusCode.Created, MappingLayer.MealToViewModel(meal));
                    }
                    else
                        return new HttpResponseMessage(HttpStatusCode.BadRequest);
                }
                else
                    return new HttpResponseMessage(HttpStatusCode.BadRequest);
            });
        }

        // PUT /users/x/meals/y
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public async Task<HttpResponseMessage> Put(int parentId, int id, HttpRequestMessage request)
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
                    MealViewModel viewModel = JsonUtil.JsonToObject<MealViewModel>(jsonString);

                    if (viewModel != null)
                    {
                        //update meal
                        Meal meal = MappingLayer.ViewModelToMeal(viewModel);
                        meal = MealService.UpdateMeal(this.AuthToken, parentId, id, meal);

                        //return response 
                        return this.CreateResponseWithContent<MealViewModel>(HttpStatusCode.OK, MappingLayer.MealToViewModel(meal));
                    }
                    else
                        return new HttpResponseMessage(HttpStatusCode.BadRequest);
                }
                else
                    return new HttpResponseMessage(HttpStatusCode.BadRequest);
            });
        }

        // DELETE /users/x/meals/y
        [CalWatch.Api.Controllers.AtributeFilters.LoggingFilter]
        public HttpResponseMessage Delete(int parentId, int id)
        {
            return this.WrapInTryCatch(() =>
            {
                MealService.DeleteMeal(this.AuthToken, parentId, id);
                return new HttpResponseMessage(HttpStatusCode.NoContent);
            }); 
        }
    }
}
