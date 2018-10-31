package elin.photogalleryapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private EditText fromDate;
    private EditText toDate;
    private EditText caption;
    private EditText latitude;
    private EditText longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fromDate   = (EditText) findViewById(R.id.search_fromDate);
        toDate     = (EditText) findViewById(R.id.search_toDate);
        caption    = (EditText) findViewById(R.id.search_caption);
        latitude   = (EditText) findViewById(R.id.search_latitude);
        longitude  = (EditText) findViewById(R.id.search_longitude);
    }


    public void cancel(final View v) {
        finish();
    }

    public void search(final View v) {
        double latitudeD;
        double longitudeD;

        Intent i = new Intent();

        latitudeD = (latitude.getText().toString().equals("")) ? 49.24 : Double.parseDouble(latitude.getText().toString());
        longitudeD = (longitude.getText().toString().equals("")) ? -123 : Double.parseDouble(longitude.getText().toString());


        i.putExtra("STARTDATE", fromDate.getText().toString());
        i.putExtra("ENDDATE", toDate.getText().toString());
        i.putExtra("CAPTION", caption.getText().toString());
        i.putExtra("LATITUDE",latitudeD);
        i.putExtra("LONGITUDE", longitudeD);
        setResult(RESULT_OK, i);
        finish();
    }
}

