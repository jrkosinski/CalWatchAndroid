package com.calwatch.android.activities;

import android.app.Activity;
import com.calwatch.android.api.models.Meal;
import com.calwatch.android.api.models.MealFilterParams;
import com.calwatch.android.api.responses.ApiResponse;
import com.calwatch.android.api.responses.MealListResponse;
import com.calwatch.android.api.responses.MealResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.DeleteMealAsyncTask;
import com.calwatch.android.tasks.GetMealAsyncTask;
import com.calwatch.android.tasks.GetMealsAsyncTask;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.tasks.ICallback;
import com.calwatch.android.util.AlertUtil;
import com.calwatch.android.util.DateTimeUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.calwatch.android.R;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

//TODO: controls do not usually need to be class-level variables
//TODO: make the list not jump back to top when refreshing
public class MealsViewActivity extends ActivityBase
{
    private static final String LogTag = "MealsViewActivity";

    private ListView mealsListView;
    private List<Meal> mealsList;
    private boolean refreshList;
    private Button filterButton;
    private View filterViewContainer;
    private TextView filterLabel;

    public List<Meal> getMealsList() {
        return this.mealsList;
    }
    public void setMealsList(List<Meal> value) {
        this.mealsList = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Meals");

        initializeState();

        mealsListView = (ListView)findViewById(R.id.mealsListView);
        filterButton = (Button)findViewById(R.id.filterButton);
        filterLabel = (TextView)findViewById(R.id.filterText);
        filterViewContainer = findViewById(R.id.filterViewContainer);
        Button addButton = (Button)findViewById(R.id.addButton);
        Button editFilterButton = (Button)findViewById(R.id.editFilterButton);
        Button clearFilterButton = (Button)findViewById(R.id.clearFilterButton);

        this.refreshListFromServer();

        //add button action
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateUpdateScreen(0);
            }
        });

        //filter button action
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterView();
            }
        });

        editFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterView();
            }
        });

        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalStorage.setMealFilterParams(null);
                refreshListFromServer();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case UpdateMealActivityId:
                if (resultCode == Activity.RESULT_OK) {
                    this.refreshList = true;
                }
                break;

            case UserSettingsActivityId:
                if (resultCode == Activity.RESULT_OK) {
                    this.refreshList = true;
                }
                break;

            case FilterMealsActivityId:
                if (resultCode == Activity.RESULT_OK)
                {
                    this.refreshList = true;
                }
                break;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(this.refreshList)
        {
            this.refreshListFromServer();
            refreshList=false;
        }
    }

    @Override
    protected  void onUserChanged()
    {
        refreshListFromServer();
    }

    private void showFilterView()
    {
        Intent intent = new Intent(MealsViewActivity.this, MealsFilterActivity.class);
        MealFilterParams filterParams = LocalStorage.getMealFilterParams();

        if (filterParams == null)
            filterParams = new MealFilterParams();

        intent.putExtra("DateFrom", filterParams.getFromDate());
        intent.putExtra("DateTo", filterParams.getToDate());
        intent.putExtra("TimeFrom", filterParams.getFromTime());
        intent.putExtra("TimeTo", filterParams.getToTime());
        startActivityForResult(intent, FilterMealsActivityId);
        overridePendingTransitionVerticalEntrance();
    }

    private void refreshListFromServer()
    {
        super.onUserChanged();
        this.displayFilterParams();
        final GetMealsAsyncTask getMealsAsyncTask = new GetMealsAsyncTask(MealsViewActivity.this, LocalStorage.getMealFilterParams(), new GetMealsCallback());
        getMealsAsyncTask.execute(LocalStorage.getCurrentTargetUserId());
    }

    private void reloadListView()
    {
        if (this.mealsList == null)
            this.mealsList = new ArrayList<Meal>();

        final MealArrayAdapter listAdapter = new MealArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                this.mealsList
        );

        mealsListView.setAdapter(listAdapter);
    }

    private void showCreateUpdateScreen(int mealId)
    {
        Intent intent = new Intent(MealsViewActivity.this, CreateUpdateMealActivity.class);
        intent.putExtra("MealId", mealId);
        startActivityForResult(intent, UpdateMealActivityId);
        overridePendingTransitionVerticalEntrance();
    }

    private void displayFilterParams()
    {
        MealFilterParams filterParams = LocalStorage.getMealFilterParams();

        if (filterParams != null && !filterParams.isEmpty())
        {
            filterButton.setVisibility(View.GONE);
            filterViewContainer.setVisibility(View.VISIBLE);

            StringBuilder filterText = new StringBuilder();
            boolean showingDate = false;
            if (filterParams.getFromDate() != null || filterParams.getToDate() != null) {
                filterText.append((filterParams.getFromDate() != null) ? filterParams.getFromDate() : "?");
                filterText.append(" - ");
                filterText.append((filterParams.getToDate() != null) ? filterParams.getToDate() : "?");
                showingDate = true;
            }

            if (filterParams.getFromTime() != null || filterParams.getToTime() != null) {
                if (showingDate)
                    filterText.append("\n");
                filterText.append((filterParams.getFromTime() != null) ? filterParams.getFromTime() : "?");
                filterText.append(" - ");
                filterText.append((filterParams.getToTime() != null) ? filterParams.getToTime() : "?");
            }

            filterLabel.setText(filterText.toString());
        }
        else
        {
            filterButton.setVisibility(View.VISIBLE);
            filterViewContainer.setVisibility(View.GONE);
        }
    }


    private class MealArrayAdapter extends ArrayAdapter<Meal>
    {
        private final Context context;
        private final List<Meal> meals;

        public MealArrayAdapter(Context context, int textViewResourceId, List<Meal> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.meals = objects;
        }

        @Override
        public long getItemId(int position) {
            Meal item = getItem(position);
            return item.getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.meal_row_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.mealDescText);
            Button updateButton = (Button)rowView.findViewById(R.id.editButton);
            Button deleteButton = (Button)rowView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    int mealId = meals.get(position).getId();

                    //make user confirm before deleting
                    AlertUtil.showDialogTwoOptions(
                            MealsViewActivity.this,
                            "Confirm Delete",
                            "Are you sure you want to delete this meal?",
                            "Delete",
                            "Cancel",
                            new ConfirmDeleteCallback(mealId),
                            null
                    );
                }
            });

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Meal meal = meals.get(position);
                    showCreateUpdateScreen(meal.getId());
                }
            });

            textView.setText(meals.get(position).getDescription());

            return rowView;
        }
    }

    private class ConfirmDeleteCallback implements ICallback
    {
        private  int mealId;

        public ConfirmDeleteCallback(int mealId) {
            this.mealId = mealId;
        }

        @Override
        public void execute()
        {
            final DeleteMealAsyncTask deleteAsyncTask = new DeleteMealAsyncTask(
                    MealsViewActivity.this,
                    LocalStorage.getCurrentTargetUserId(),
                    new DeleteMealCallback()
            );

            deleteAsyncTask.execute(mealId);
        }
    }

    private class DeleteMealCallback implements IApiResponseCallback<ApiResponse>
    {
        @Override
        public void onFinished(ApiResponse response)
        {
            Log.i(LogTag, "Delete meal finished.");

            if (response != null && response.isSuccessful()) {
                refreshListFromServer();
            }
            else
            {
                AlertUtil.showAlert(
                        MealsViewActivity.this,
                        "Delete Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Delete Meal unsuccessful."
                );
            }
        }
    }

    private class GetMealsCallback implements IApiResponseCallback<MealListResponse>
    {
        @Override
        public void onFinished(MealListResponse response)
        {
            Log.i(LogTag, "Get meals finished.");

            if (response != null && response.isSuccessful()) {
                setMealsList(response.getMeals());
                reloadListView();
            }
            else
            {
                AlertUtil.showAlert(
                        MealsViewActivity.this,
                        "Get Meals Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Unable to retrieve meals."
                );
            }
        }
    }
}