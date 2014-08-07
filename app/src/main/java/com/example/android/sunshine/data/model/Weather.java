package com.example.android.sunshine.data.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;

@Table(name = WeatherEntry.TABLE_NAME, id = BaseColumns._ID)
public class Weather extends Model {

    @Column(name = WeatherEntry.COLUMN_LOC_KEY,
            uniqueGroups = {"group1"}, onUniqueConflicts = {Column.ConflictAction.REPLACE})
    public Location location;

    @Column(name = WeatherEntry.COLUMN_DATETEXT,
            uniqueGroups = {"group1"}, onUniqueConflicts = {Column.ConflictAction.REPLACE})
    public String dateText;

    @Column(name = WeatherEntry.COLUMN_SHORT_DESC)
    public String shortDescription;

    @Column(name = WeatherEntry.COLUMN_WEATHER_ID)
    public int weatherId;

    @Column(name = WeatherEntry.COLUMN_MAX_TEMP)
    public double maxTemperature;

    @Column(name = WeatherEntry.COLUMN_MIN_TEMP)
    public double minTemperature;

    @Column(name = WeatherEntry.COLUMN_HUMIDITY)
    public double humidity;

    @Column(name = WeatherEntry.COLUMN_PRESSURE)
    public double pressure;

    @Column(name = WeatherEntry.COLUMN_WIND_SPEED)
    public double windSpeed;

    @Column(name = WeatherEntry.COLUMN_DEGREES)
    public double windDirection;


}
