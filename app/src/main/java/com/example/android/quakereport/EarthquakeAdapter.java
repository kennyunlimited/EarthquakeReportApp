package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by TAYE on 13/01/2018.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    private static final String LOCATION_SEPARATOR = "of";

    public EarthquakeAdapter(@NonNull Context context, @NonNull ArrayList<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Earthquake currentEarthquake = getItem(position);

        double magnitude = currentEarthquake.getmMagnitude();


        TextView magTextView = (TextView) listItemView.findViewById(R.id.magnitude);
        GradientDrawable magnitudeCircle = (GradientDrawable) magTextView.getBackground();
        int magnitudeColor = getMagnitudeColor(magnitude);
        int color = ContextCompat.getColor(getContext(), magnitudeColor);
        magnitudeCircle.setColor(color);


        magTextView.setText((magnitudeFormat(magnitude)));


        String originalLocation = currentEarthquake.getmLocation();

        String primaryLocation;
        String locationOffset;

        if (originalLocation.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalLocation.split("of");
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = originalLocation;
        }

        TextView proximityTextView = (TextView) listItemView.findViewById(R.id.proximity);
        proximityTextView.setText(locationOffset);

        TextView locationTextView = (TextView) listItemView.findViewById(R.id.location);
        locationTextView.setText(primaryLocation);


        long timeInMilliSeconds = currentEarthquake.getmTimeInMilliSeconds();

        Date dateObject = new Date(timeInMilliSeconds);


        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        String date = dateFormat(dateObject);
        dateTextView.setText(date);

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);
        String time = timeFormat(dateObject);
        timeTextView.setText(time);

        return listItemView;
    }


    private String dateFormat(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        String format = dateFormat.format(dateObject);
        return format;
    }

    private String timeFormat(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String format = timeFormat.format(dateObject);
        return format;
    }

    private String magnitudeFormat(double magnitude) {
        DecimalFormat magFormat = new DecimalFormat("0.0");
        String format = magFormat.format(magnitude);
        return format;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magFloor = (int) Math.floor(magnitude);
        switch (magFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        //return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
        return magnitudeColorResourceId;
        }
    }


