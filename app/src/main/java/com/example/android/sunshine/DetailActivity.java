package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;

import java.util.Date;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
        private static final String TAG_NAME = DetailFragment.class.getSimpleName();
        private String mDateStr;

        private final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
        };


        public static final int COL_WEATHER_ID = 0;
        public static final int COL_WEATHER_DATE = 1;
        public static final int COL_WEATHER_DESC = 2;
        public static final int COL_WEATHER_MAX_TEMP = 3;
        public static final int COL_WEATHER_MIN_TEMP = 4;
        public static final int COL_LOCATION_SETTING = 5;

        private static final int FORECAST_LOADER = 1;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(FORECAST_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);

            ShareActionProvider shareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }

        }

        private TextView dateTextView;
        private TextView forecastTextView;
        private TextView highTextView;
        private TextView lowTextView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            Intent intent = getActivity().getIntent();
            mDateStr = intent.getStringExtra(Intent.EXTRA_TEXT);

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            dateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
            forecastTextView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
            highTextView = (TextView) rootView.findViewById(R.id.detail_high_textview);
            lowTextView = (TextView) rootView.findViewById(R.id.detail_low_textview);

            return rootView;
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            // returns us to our application instead of the sharing application
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mDateStr + FORECAST_SHARE_HASHTAG);

            return shareIntent;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Log.v(LOG_TAG, "onCreateLoader called");

            // This is called when a new Loader needs to be created.  This
            // fragment only uses one loader, so we don't care about checking the id.

            // To only show current and future dates, get the String representation for today,
            // and filter the query to return weather only for dates after or including today.
            // Only return data after today.
            String startDate;
            if (mDateStr == null || mDateStr.isEmpty()) {
                Log.v(TAG_NAME, "startDate is null, using new date");
                startDate = WeatherContract.getDbDateString(new Date());
            } else {
                startDate = mDateStr;
            }

            // Sort order:  Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

            String location = Utility.getPreferredLocation(getActivity());
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    location, startDate);

            Log.v(LOG_TAG, "uri = " + weatherForLocationUri);

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            Log.v(LOG_TAG, "onLoadFinished called");

            if (data.moveToFirst()) {

                boolean isMetric = Utility.isMetric(getActivity());
                String date = data.getString(COL_WEATHER_DATE);
                String forecast = data.getString(COL_WEATHER_DESC);
                String highTemp = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
                String lowTemp = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

                dateTextView.setText(Utility.formatDate(date));
                forecastTextView.setText(forecast);
                highTextView.setText(highTemp);
                lowTextView.setText(lowTemp);

            } else {
                Log.e(LOG_TAG, "No data found for cursor!");
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
