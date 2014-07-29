/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.example.android.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.data.WeatherDbHelper;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // Test data we're going to insert into the DB to see if it works.
        String testLocationSetting = "99705";
        String testCityName = "North Pole";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_CITY_NAME, testCityName);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);
        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Specify which columns you want.
        String[] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_LOCATION_SETTING,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_COORD_LAT,
                LocationEntry.COLUMN_COORD_LONG
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex((LocationEntry.COLUMN_CITY_NAME));
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LAT));
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex((LocationEntry.COLUMN_COORD_LONG));
            double longitude = cursor.getDouble(longIndex);

            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            // Then take a break.  We both know that wasn't easy.
            assertEquals(testCityName, name);
            assertEquals(testLocationSetting, location);
            assertEquals(testLatitude, latitude);
            assertEquals(testLongitude, longitude);

            // Fantastic.  Now that we have a location, add some weather!
            String testDateText = "20141205";
            double testDegrees = 1.1;
            double testHumidity = 1.2;
            double testPressure = 1.3;
            double testMaxTemp = 75;
            double testMinTemp = 65;
            String testShortDescription = "Asteroids";
            double testWindSpeed = 5.5;
            int testWeatherId = 321;

            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherEntry.COLUMN_DATETEXT, testDateText);
            weatherValues.put(WeatherEntry.COLUMN_DEGREES, testDegrees);
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, testHumidity);
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, testPressure);
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, testMaxTemp);
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, testMinTemp);
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, testShortDescription);
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, testWindSpeed);
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, testWeatherId);

            String[] weatherColumns = {
                    WeatherEntry.COLUMN_LOC_KEY,
                    WeatherEntry.COLUMN_DATETEXT,
                    WeatherEntry.COLUMN_DEGREES,
                    WeatherEntry.COLUMN_HUMIDITY,
                    WeatherEntry.COLUMN_PRESSURE,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_WIND_SPEED,
                    WeatherEntry.COLUMN_WEATHER_ID
            };

            long weatherRowId;
            weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

            // Verify we got a row back.
            assertTrue(weatherRowId != -1);
            Log.d(LOG_TAG, "New row id: " + weatherRowId);

            Cursor weatherCursor = db.query(WeatherEntry.TABLE_NAME,
                    weatherColumns,
                    null, // Columns for the "where" clause
                    null, // Values for the "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null // sort order
            );

            if (weatherCursor.moveToFirst()) {
                int locationKeyIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_LOC_KEY);
                int locationKey = weatherCursor.getInt(locationKeyIndex);

                int dateTextIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DATETEXT);
                String dateText = weatherCursor.getString(dateTextIndex);

                int degreesIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES);
                double degrees = weatherCursor.getDouble(degreesIndex);

                int humidityIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
                double humidity = weatherCursor.getDouble(humidityIndex);

                int pressureIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
                double pressure = weatherCursor.getDouble(pressureIndex);

                int maxTempIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
                double maxTemp = weatherCursor.getDouble(maxTempIndex);

                int minTempIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
                double minTemp = weatherCursor.getDouble(minTempIndex);

                int shortDescriptionIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC);
                String shortDescription = weatherCursor.getString(shortDescriptionIndex);

                int windSpeedIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED);
                double windSpeed = weatherCursor.getDouble(windSpeedIndex);

                int weatherIdIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
                int weatherId = weatherCursor.getInt(weatherIdIndex);

                assertEquals(locationRowId, locationKey);
                assertEquals(testDateText, dateText);
                assertEquals(testDegrees, degrees);
                assertEquals(testHumidity, humidity);
                assertEquals(testPressure, pressure);
                assertEquals(testMaxTemp, maxTemp);
                assertEquals(testMinTemp, minTemp);
                assertEquals(testShortDescription, shortDescription);
                assertEquals(testWindSpeed, windSpeed);
                assertEquals(testWeatherId, weatherId);
            } else {
                fail("No values returned :(");
            }

            dbHelper.close();
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
    }
}
