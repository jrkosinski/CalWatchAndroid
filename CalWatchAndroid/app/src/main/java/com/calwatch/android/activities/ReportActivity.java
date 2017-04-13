package com.calwatch.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.calwatch.android.R;
import com.calwatch.android.api.models.FilterParams;
import com.calwatch.android.api.models.Report;
import com.calwatch.android.api.models.ReportDay;
import com.calwatch.android.api.responses.ReportResponse;
import com.calwatch.android.storage.LocalStorage;
import com.calwatch.android.tasks.GetReportAsyncTask;
import com.calwatch.android.tasks.IApiResponseCallback;
import com.calwatch.android.util.AlertUtil;
import com.calwatch.android.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John R. Kosinski on 25/1/2559.
 * Allows user to view reports.
 */
public class ReportActivity extends ActivityBase {

    private static final String LogTag = "ReportActivity";

    private ListView reportListView;
    private Button filterButton;
    private boolean refreshList;
    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentResId(R.layout.activity_report);
        setAppBarTitle("Report");
        super.onCreate(savedInstanceState);

        reportListView = (ListView)findViewById(R.id.reportListView);
        filterButton = (Button)findViewById(R.id.filterButton);
        final Button doneButton = (Button)findViewById(R.id.doneButton);

        this.refreshListFromServer();

        //filter button action
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterView();
            }
        });

        //done button action
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMealsView();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case UserSettingsActivityId:
                if (resultCode == Activity.RESULT_OK) {
                    this.refreshList = true;
                }
                break;

            case FilterMealsActivityId:
                if (resultCode == Activity.RESULT_OK)  {
                    this.refreshList = true;
                }
                break;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(this.refreshList) {
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
        Intent intent = new Intent(ReportActivity.this, FilterActivity.class);
        FilterParams filterParams = LocalStorage.getFilterParams();

        if (filterParams == null)
            filterParams = new FilterParams();

        intent.putExtra("DateFrom", filterParams.getDateFrom());
        intent.putExtra("DateTo", filterParams.getDateTo());
        intent.putExtra("TimeFrom", filterParams.getTimeFrom());
        intent.putExtra("TimeTo", filterParams.getTimeTo());
        startActivityForResult(intent, FilterMealsActivityId);
        overridePendingTransitionVerticalEntrance();
    }

    private void refreshListFromServer()
    {
        this.displayUsername();
        this.displayFilterParams();
        final GetReportAsyncTask task = new GetReportAsyncTask(ReportActivity.this, LocalStorage.getFilterParams(), new GetReportCallback());
        task.execute(LocalStorage.getCurrentTargetUser().getId());
    }

    private void reloadListView()
    {
        ArrayList<ReportDay> reportDays = new ArrayList<>();
        if (report != null)
            reportDays = report.getDays();

        final ReportArrayAdapter listAdapter = new ReportArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                reportDays
        );

        reportListView.setAdapter(listAdapter);
    }

    private void displayFilterParams()
    {
        FilterParams filterParams = null;

        //attempt to get filter params from response; if not available, get from local storage
        if (report != null)
            filterParams = report.getFilterParams();
        if (filterParams == null)
            filterParams = LocalStorage.getFilterParams();

        if (filterParams != null && !filterParams.isEmpty())
        {
            StringBuilder filterText = new StringBuilder();
            boolean showingDate = false;
            if (filterParams.getDateFrom() != null || filterParams.getDateTo() != null) {
                filterText.append("from ");
                filterText.append((filterParams.getDateFrom() != null) ? filterParams.getDateFrom() : "?");
                filterText.append(" - ");
                filterText.append((filterParams.getDateTo() != null) ? filterParams.getDateTo() : "?");
                showingDate = true;
            }

            if (filterParams.getTimeFrom() != null || filterParams.getTimeTo() != null) {
                if (showingDate)
                    filterText.append("\n");
                filterText.append("between ");
                filterText.append((filterParams.getTimeFrom() != null) ? filterParams.getTimeFrom() : "00:00");
                filterText.append(" - ");
                filterText.append((filterParams.getTimeTo() != null) ? filterParams.getTimeTo() : "23:59");
            }

            ((TextView)findViewById(R.id.filterLabel)).setText(filterText.toString());
        }
    }

    private void displayReportDetails()
    {
        ((TextView)findViewById(R.id.totalCaloriesForPeriodLabel)).setText(Integer.toString(report.getTotalCalories()));
        ((TextView)findViewById(R.id.averageCaloriesPerDayLabel)).setText(Integer.toString(report.getAverageCaloriesPerDay()));
        ((TextView)findViewById(R.id.numberOfDaysOverTargetLabel)).setText(String.format("%d/%d", report.getNumberOfDaysOverTarget(), report.getDays().size()));
        ((TextView)findViewById(R.id.targetCaloriesPerDayLabel)).setText(Integer.toString(LocalStorage.getCurrentTargetUser().getTargetCaloriesPerDay()));
    }

    private void goToMealsView() {
        Intent intent = new Intent(ReportActivity.this, MealsViewActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(RESULT_OK);
        startActivity(intent);
        finish();
        overridePendingTransitionHorizontalExit();
    }



    private class ReportArrayAdapter extends ArrayAdapter<ReportDay>
    {
        private final Context context;
        private final List<ReportDay> reportDays;

        public ReportArrayAdapter(Context context, int textViewResourceId, List<ReportDay> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.reportDays = objects;
        }

        @Override
        public long getItemId(int position) {
            ReportDay item = getItem(position);
            return item.getDate().hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.report_row_layout, parent, false);
            TextView dateLabel = (TextView) rowView.findViewById(R.id.dateLabel);
            TextView caloriesLabel = (TextView) rowView.findViewById(R.id.caloriesLabel);

            ReportDay reportDay = reportDays.get(position);

            dateLabel.setText(DateTimeUtil.getDayOfWeek(DateTimeUtil.StringToDate(reportDay.getDate()), true) + " " + reportDay.getDate());
            caloriesLabel.setText(String.format("cal period/day: %s/%s",
                            Integer.toString(reportDay.getTotalCaloriesForPeriod()),
                            Integer.toString(reportDay.getTotalCaloriesForDay()))
            );

            //if over daily target, set it to red, otherwise green
            if (reportDay.getOverDailyTarget())
                rowView.setBackgroundColor(ContextCompat.getColor(ReportActivity.this, R.color.colorRedHighlight));
            else
                rowView.setBackgroundColor(ContextCompat.getColor(ReportActivity.this, R.color.colorGreenHighlight));

            return rowView;
        }
    }

    private class GetReportCallback implements IApiResponseCallback<ReportResponse>
    {
        @Override
        public void onFinished(ReportResponse response)
        {
            Log.i(LogTag, "Get report finished.");

            if (response != null && response.isSuccessful()) {
                report = (response.getReport());
                if (report == null)
                    report = new Report();

                displayFilterParams();
                displayReportDetails();
                reloadListView();
            }
            else
            {
                AlertUtil.showAlert(
                        ReportActivity.this,
                        "Get Report Error",
                        (response != null && response.hasError()) ? response.getErrorInfo().getMessage() : "Unable to get report."
                );
            }
        }
    }
}
