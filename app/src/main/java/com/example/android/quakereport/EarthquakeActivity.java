/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Earthquake>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    EarthquakeAdapter earthquakeAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView emptyTextView;

    private ProgressBar progressBar;

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST: Earthquake Activity onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

//        // Create a fake list of earthquake locations.
//        ArrayList<Earthquake> earthquakes = new ArrayList<>();
//        earthquakes.add(new Earthquake("7.2", "San Francisco", "Feb 3, 2016"));
//        earthquakes.add(new Earthquake("6.1", "London", "July 20, 2015"));
//        earthquakes.add(new Earthquake("3.9", "Tokyo", "Nov 10, 2014"));
//        earthquakes.add(new Earthquake("4.1", "Mexico City", "May 3, 2014"));
//        earthquakes.add(new Earthquake("2.9", "Moscow", "Jan 31, 2013"));
//        earthquakes.add(new Earthquake("4.9", "Rio de Janero", "Aug 19, 2012"));
//        earthquakes.add(new Earthquake("1.6", "Paris", "Oct 30, 2011"));

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        emptyTextView = (TextView) findViewById(R.id.empty_view);

        earthquakeListView.setEmptyView(emptyTextView);

        // Create a new {@link ArrayAdapter} of earthquakes
        earthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(earthquakeAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);


        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currentEarthquake = earthquakeAdapter.getItem(position);

                Uri uri = Uri.parse(currentEarthquake.getmUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(websiteIntent);
            }
        });


        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            progressBar.setVisibility(View.VISIBLE);

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.i(LOG_TAG, "TEST: calling initLoader()");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            emptyTextView.setText(R.string.no_internet);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_min_magnitude_key)) ||
                key.equals(getString(R.string.settings_order_by_key))) {
            // Clear the ListView as a new query will be kicked off
            earthquakeAdapter.clear();

            // Hide the empty state text view as the loading indicator will be displayed
            emptyTextView.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched
            progressBar.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
    }


    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the given URL
        Log.i(LOG_TAG, "TEST: Earthquake Activity onCreateLoader() called ....");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {


        progressBar.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        emptyTextView.setText(R.string.no_earthquake);

        Log.i(LOG_TAG, "TEST: Earthquake Activity onLoadFinished() called ....");
        //clear the adapter of previous earthquake data
        earthquakeAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
          earthquakeAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "TEST: Earthquake Activity onLoaderReset() called ....");
       if (earthquakeAdapter != null) {
           earthquakeAdapter.clear();
       }
    }

}
