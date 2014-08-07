package com.example.android.sunshine.data.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.example.android.sunshine.data.WeatherContract.LocationEntry;

@Table(name = LocationEntry.TABLE_NAME, id = BaseColumns._ID)
public class Location extends Model {

    @Column(name = LocationEntry.COLUMN_LOCATION_SETTING,
        uniqueGroups = {"group1"}, onUniqueConflicts = Column.ConflictAction.IGNORE)
    public String locationSetting;

    @Column(name = LocationEntry.COLUMN_CITY_NAME)
    public String cityName;

    @Column(name = LocationEntry.COLUMN_COORD_LAT)
    public double latitude;

    @Column(name = LocationEntry.COLUMN_COORD_LONG)
    public double longitude;
}
