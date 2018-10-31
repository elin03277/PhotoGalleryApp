package elin.photogalleryapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import elin.photogalleryapp.myDB.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    static final int CAMERA_REQUEST_CODE = 1;

    private String currentPhotoPath = null;
    private int currentPhotoIndex = 0;
    private ArrayList<String> photoGallery;
    private LocationManager locationManager;
    private double longitude;
    private double latitude;
    private ImageView iv;

    public Button btnLeft;
    public Button btnRight;
    public Button btnFilter;
    public Button btnCamera;
    public Button btnSaveCaption;
    public EditText captionText;
    public TextView dateText;
    public TextView latitudeText;
    public TextView longitudeText;

    public SQLiteDatabase db;
    public PhotoReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new PhotoReaderDbHelper(this);

        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();

        iv = (ImageView) findViewById(R.id.ivMain);

        btnLeft = (Button)findViewById(R.id.btnLeft);
        btnRight = (Button)findViewById(R.id.btnRight);
        btnFilter = (Button)findViewById(R.id.btnFilter);
        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnSaveCaption = (Button)findViewById(R.id.btnSaveCaption);
        captionText = (EditText)findViewById(R.id.captionText);
        dateText = (TextView)findViewById(R.id.dateText);
        latitudeText = (TextView)findViewById(R.id.latitudeText);
        longitudeText = (TextView)findViewById(R.id.longitudeText);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnFilter.setOnClickListener(filterListener);
        btnCamera.setOnClickListener(cameraListener);
        btnSaveCaption.setOnClickListener(saveListener);

        photoGallery = populateGallery(null, null, "", 49.24, -123);
        Log.d("onCreate, size", Integer.toString(photoGallery.size()));
        if (photoGallery.size() > 0)
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
        displayPhoto(currentPhotoPath);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            Log.d("Lat", String.valueOf(latitude));
            longitude = location.getLongitude();
            Log.d("Long", String.valueOf(longitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(i, SEARCH_ACTIVITY_REQUEST_CODE);
        }
    };

    private View.OnClickListener cameraListener = new View.OnClickListener() {
        public void onClick(View v) {
            takePicture(v);
        }
    };


    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View v) {
            saveCaption(v, captionText.getText().toString());
        }
    };

    private ArrayList<String> populateGallery(String minDate, String maxDate, String caption, double latitude, double longitude) {

        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/elin.photogalleryapp/files/Pictures");
        ArrayList<String> photoGallery = new ArrayList<String>();
        File[] fList = file.listFiles();

        if (fList != null) {
            if(minDate == null && maxDate == null) {

                String selection = "instr(" + PhotoReaderContract.FeedEntry.COLUMN_NAME_CAPTION + ", ?) > 0 AND "
                        + "? - " + PhotoReaderContract.FeedEntry.COLUMN_NAME_LAT + " < 0.01 AND "
                        + "? - " + PhotoReaderContract.FeedEntry.COLUMN_NAME_LONG + " < 0.01";

                String[] selectionArgs = {caption, latitude+"", longitude+""};

                Cursor cursor = db.query(
                    PhotoReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                    null,                              // The array of columns to return (pass null to get all)
                     selection,                                // The columns for the WHERE clause
                     selectionArgs,                            // The values for the WHERE clause
                    null,                              // don't group the rows
                    null,                               // don't filter by row groups
                    null                               // The sort order
                );

                while(cursor.moveToNext()) {
                    photoGallery.add(cursor.getString(cursor.getColumnIndex(PhotoReaderContract.FeedEntry.COLUMN_NAME_PHOTOPATH)));
                }

                cursor.close();
            } else {

                String selection = PhotoReaderContract.FeedEntry.COLUMN_NAME_DATE + " BETWEEN date(?) AND date(?) AND " + "instr(" + PhotoReaderContract.FeedEntry.COLUMN_NAME_CAPTION + ", ?) > 0 AND "
                        + "? - " + PhotoReaderContract.FeedEntry.COLUMN_NAME_LAT + " < 0.01 AND "
                        + "? - " + PhotoReaderContract.FeedEntry.COLUMN_NAME_LONG + " < 0.01";

                String[] selectionArgs = {minDate, maxDate, caption, latitude+"", longitude+""};

                Cursor cursor = db.query(
                        PhotoReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                        null,                               // The array of columns to return (pass null to get all)
                        selection,                                  // The columns for the WHERE clause
                        selectionArgs,                              // The values for the WHERE clause
                        null,                               // don't group the rows
                        null,                                // don't filter by row groups
                        null                                // The sort order
                );

                Log.d("Events", Integer.toString(cursor.getCount()));
                while (cursor.moveToNext()) {
                    photoGallery.add(cursor.getString(cursor.getColumnIndex(PhotoReaderContract.FeedEntry.COLUMN_NAME_PHOTOPATH)));
                }

                cursor.close();
            }
        }
        return photoGallery;
    }

    private void displayPhoto(String path) {

        iv.setImageBitmap(BitmapFactory.decodeFile(path));

        String caption = "";
        String date = "";
        String latitude = "";
        String longitude = "";
        String selection =  PhotoReaderContract.FeedEntry.COLUMN_NAME_PHOTOPATH + " = ?";
        String[] selectionArgs = {path};


        if (path != null) {

                Cursor cursor = db.query(
                        PhotoReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                        null,                              // The array of columns to return (pass null to get all)
                        selection,                                 // The columns for the WHERE clause
                        selectionArgs,                             // The values for the WHERE clause
                        null,                              // don't group the rows
                        null,                               // don't filter by row groups
                        null                               // The sort order
                );
                while (cursor.moveToNext()) {
                    caption = cursor.getString(cursor.getColumnIndex(PhotoReaderContract.FeedEntry.COLUMN_NAME_CAPTION));
                    date = cursor.getString(cursor.getColumnIndex(PhotoReaderContract.FeedEntry.COLUMN_NAME_DATE));
                    latitude = cursor.getString(cursor.getColumnIndex(PhotoReaderContract.FeedEntry.COLUMN_NAME_LAT));
                    longitude = cursor.getString(cursor.getColumnIndex(PhotoReaderContract.FeedEntry.COLUMN_NAME_LONG));
                    captionText.setText(caption);
                    dateText.setText(date);
                    latitudeText.setText("Lat:" + latitude);
                    longitudeText.setText("Long:" + longitude);
                }

                cursor.close();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClick( View v) {
        switch (v.getId()) {
            case R.id.btnLeft:
                --currentPhotoIndex;
                break;
            case R.id.btnRight:
                ++currentPhotoIndex;
                break;
            default:
                break;
        }

        if (photoGallery.size() > 0) {
            if (currentPhotoIndex < 0)
                currentPhotoIndex = 0;
            if (currentPhotoIndex >= photoGallery.size())
                currentPhotoIndex = photoGallery.size() - 1;

            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            Log.d("phpotoleft, size", Integer.toString(photoGallery.size()));
            Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
            displayPhoto(currentPhotoPath);
        }
    }

    public void goToSearch(View v) {
        startActivityForResult(new Intent(getApplicationContext(), SearchActivity.class), 27);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String minDateS   = data.getStringExtra("STARTDATE");
                String maxDateS   = data.getStringExtra("ENDDATE");
                String captionS   = data.getStringExtra("CAPTION");
                double  latitudeD  = data.getDoubleExtra("LATITUDE",49.24);
                double  longitudeD = data.getDoubleExtra("LONGITUDE",-123);

                if(minDateS.isEmpty() || maxDateS.isEmpty()) {
                    minDateS = null;
                    maxDateS = null;
                }

                if(Double.toString(latitudeD).isEmpty() || Double.toString(longitudeD).isEmpty()) {
                    latitudeD  = 49.24;
                    longitudeD = -123;
                }

               photoGallery = populateGallery(minDateS, maxDateS, captionS, latitudeD, longitudeD);

                Log.d("onCreate, size", Integer.toString(photoGallery.size()));
                currentPhotoIndex = 0;
                if(photoGallery.size() > 0) {
                    currentPhotoPath = photoGallery.get(currentPhotoIndex);
                    displayPhoto(currentPhotoPath);
                } else {
                    iv.setImageBitmap(null);
                    captionText.setText("");
                    dateText.setText("");
                    latitudeText.setText("");
                    longitudeText.setText("");
                }
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", "Picture Taken");
                photoGallery = populateGallery(null, null, "", 49.24, -123);
                currentPhotoIndex = 0;
                Log.d("", "currentPhotoIndex");
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                Log.d("", currentPhotoPath);
                displayPhoto(currentPhotoPath);
            }
        }
    }

    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("FileCreation", "Failed");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "elin.photogalleryapp.pictures.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public void saveCaption(View v, String caption){
        String[] selectionArgs = {currentPhotoPath};
        ContentValues values = new ContentValues();
        values.put( PhotoReaderContract.FeedEntry.COLUMN_NAME_CAPTION, caption);
        db.update( PhotoReaderContract.FeedEntry.TABLE_NAME,
                values,
                PhotoReaderContract.FeedEntry.COLUMN_NAME_PHOTOPATH + " = ?",
                selectionArgs);
        Log.d("Caption Saved", PhotoReaderContract.FeedEntry.COLUMN_NAME_CAPTION);
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String imageFileName = "JPEG_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", dir );
        currentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile", currentPhotoPath);


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PhotoReaderContract.FeedEntry.COLUMN_NAME_PHOTOPATH, currentPhotoPath);
        values.put(PhotoReaderContract.FeedEntry.COLUMN_NAME_DATE, timeStamp);
        values.put(PhotoReaderContract.FeedEntry.COLUMN_NAME_CAPTION, "Caption");
        values.put(PhotoReaderContract.FeedEntry.COLUMN_NAME_LAT, latitude);
        Log.d("Create Image Lat", String.valueOf(latitude));
        values.put(PhotoReaderContract.FeedEntry.COLUMN_NAME_LONG, longitude);
        Log.d("Create Image Long", String.valueOf(longitude));

        // Insert the new row, returning the primary key value of the new ro
        db.insert(PhotoReaderContract.FeedEntry.TABLE_NAME, null, values);

        return image;
    }
}
