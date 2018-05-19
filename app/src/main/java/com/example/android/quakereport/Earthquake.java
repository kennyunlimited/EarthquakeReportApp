package com.example.android.quakereport;

/**
 * Created by TAYE on 13/01/2018.
 */

public class Earthquake {

    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliSeconds;
    private String mUrl;

    public Earthquake(double mMagnitude, String mLocation, long mTimeInMilliSeconds, String mUrl) {
        this.mMagnitude = mMagnitude;
        this.mLocation = mLocation;
        this.mTimeInMilliSeconds = mTimeInMilliSeconds;
        this.mUrl = mUrl;
    }

    public double getmMagnitude() {
        return mMagnitude;
    }

    public String getmLocation() {
        return mLocation;
    }

    public long getmTimeInMilliSeconds() {
        return mTimeInMilliSeconds;
    }

    public String getmUrl() {
        return mUrl;
    }
}
