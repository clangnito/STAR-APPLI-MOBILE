package com.example.constantlangnito.starv1dl;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.constantlangnito.starv1dl.Table.BusRoute;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

import static com.example.constantlangnito.starv1dl.VariablesStatic.zipFileUrl;


import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static Button buttonTelecharger;
    public static Button buttonLister;
    public static Button button_date;
    public static Button button_heure;
    public static Button buttonListerArretBus;

    public static EditText editText_Date;
    public static EditText editText_Heure;

    public static Spinner spinner_listeBus;
    public static Spinner spinner_listeDirection;

    public int positionBusSelect = 0;
    public int positionDirectionSelect = 0;


    java.util.Calendar c;
    DatePickerDialog dpd;

    private ProgressDialog mProgressDialog;

    DatabaseManager databaseHelper;

    private String exportPath = "";

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseManager(getApplicationContext(),exportPath);
        activerPermissions(this);
        final Service service = new Service();
        service.start();
        onNewIntent(getIntent());

        buttonTelecharger = (Button)findViewById(R.id.button_telechargement);
        buttonLister = (Button)findViewById(R.id.button_listerBus);
        buttonListerArretBus = (Button)findViewById(R.id.button_ListerArretBus);


        button_date = (Button)findViewById(R.id.button_date);
        button_heure = (Button)findViewById(R.id.button_heure);

        editText_Date = (EditText)findViewById(R.id.editText_date);
        editText_Heure = (EditText)findViewById(R.id.editText_heure);

        spinner_listeBus = (Spinner) findViewById(R.id.spiner_bus);
        spinner_listeBus.setOnItemSelectedListener(this);

        spinner_listeDirection = (Spinner) findViewById(R.id.spiner_direction);


        buttonTelecharger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.getVersionsInfos();
                downloadeZipFile(zipFileUrl);
            }
        });


        buttonLister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListerBusMetro.class));
            }
        });

        buttonListerArretBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cursor list = databaseHelper.getArretBusForBusDatabase();
                Intent intent = new Intent(MainActivity.this, ListeArretBus.class);
                Bundle vals = new Bundle();
                vals.putString("dateDepart","0004");
                vals.putString("heureDepart","1");
                vals.putInt("positionBusSelect",positionBusSelect);
                vals.putInt("positionDirectionSelect", positionDirectionSelect);
                intent.putExtras(vals);
                startActivity(intent);



            }
        });

        button_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = java.util.Calendar.getInstance();

                int day = c.get(java.util.Calendar.DAY_OF_MONTH);
                int month = c.get(java.util.Calendar.MONTH);
                int year = c.get(java.util.Calendar.YEAR);

                dpd = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int myear, int mmonth, int mdayOfMonth) {
                        editText_Date.setText(mdayOfMonth +"/" + mmonth+1 +"/" + myear );
                    }
                },day, month, year);
                dpd.show();
            }
        });


        button_heure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = java.util.Calendar.getInstance();

                final int heure = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int mhourOfDay, int mminute) {
                        editText_Heure.setText(mhourOfDay + ":" + mminute);
                    }
                },heure,minute,false);
                timePickerDialog.show();
            }
        });


        loadSpinnerData();


        spinner_listeDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                positionDirectionSelect = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });


    }




    private void loadSpinnerData() {
        // database handler
        DatabaseManager db = new DatabaseManager(getApplicationContext(),"");

        // Spinner Drop down elements
        List<BusRoute> listeBus = db.getBusRoutesFromDatabase();

        // Creating adapter for spinner
       /* ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, listeBus);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

        // attaching data adapter to spinner
        //spinner_listeBus.setAdapter(dataAdapter);
        spinner_listeBus.setAdapter(new LisBusAdapter(this, R.layout.bus_line, listeBus));
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // On selecting a spinner item


        DatabaseManager db = new DatabaseManager(getApplicationContext(),"");

        // Spinner Drop down elements
        List<String> listeDirection = db.getDirectionBus(position);

        positionBusSelect = position;

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, listeDirection);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_listeDirection.setAdapter(dataAdapter);

    }




    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void downloadeZipFile(String zipFileUrl) {
        final String sourceFilname = "" + zipFileUrl;
        AsyncHttpClient client = new AsyncHttpClient();
        String[] allowedType = {
                "application/zip","application/octect-stream"
        };
        client.get(sourceFilname, new BinaryHttpResponseHandler(allowedType) {

            @Override
            public void onStart() {
                super.onStart();
                showDialog(DIALOG_DOWNLOAD_PROGRESS);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {

                try {
                    //Splitting a File Name from SourceFileName
                    String DestinationName = sourceFilname.substring(sourceFilname.lastIndexOf('/') + 1, sourceFilname.length());
                    String INIT_FOLDER_PATH = "star1DL/";

                    File DEVICE_ROOT_FOLDER = getExternalStorageDirectory();
                    File file = new File((DEVICE_ROOT_FOLDER + "/" + INIT_FOLDER_PATH));

                    if (! file.exists()){
                        file.mkdir();
                    }



                    File _f = new File(file, DestinationName);

                    FileOutputStream output = new FileOutputStream(_f);

                    output.write(binaryData);
                    output.close();

                    // Debut du deziping
                    exportPath = _f.getAbsolutePath();
                    exportPath = exportPath.replace(".zip", "");

                    //DatabaseManager.DOWNLOAD_PATH = exportPath;
                    exportPath = exportPath + "/";

                    // decropress file in folder whith id name
                    DecompresZip df = new DecompresZip(_f.getAbsolutePath(), exportPath);
                    df.unzip();

                    /**
                     * Inserer les données télechargées
                     */
                    DatabaseManager databaseHelper = new DatabaseManager(getApplicationContext(),exportPath);
                    databaseHelper.insertAll();
                    dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
                    mProgressDialog.dismiss();
                    buttonTelecharger.setText("fin de telechargement");
                    new CountDownTimer(5000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            buttonTelecharger.setText("fin de telechargement");
                            Log.e("STAR-timer", "");

                        }

                        public void onFinish() {
                            onNewIntent(getIntent());
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int val = (int) ((bytesWritten * 100) / totalSize);
                mProgressDialog.setProgress(val);
                mProgressDialog.getCurrentFocus();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {


            }


        });
    }

    //


    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static void activerPermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Losrqu'on clic sur la notif de mise à jour
     *
     * @param intent
     */
    public void onNewIntent(Intent intent) {

        new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                int hours = (int) millisUntilFinished / (60 * 60 * 1000);
                int diff = (int) millisUntilFinished - hours * (60 * 60 * 1000);
                int min = (int) diff / (60 * 1000);
                int sec = (int) (diff - min * 60 * 1000) / 1000;
                buttonTelecharger.setText("verification nouvelle version : "+hours + ":" + min + ":" + sec);
            }

            public void onFinish() {
                buttonTelecharger.setText("Telecharger nouvelle version");
            }
        }.start();

    }

    /**
     * Progresse Bar
     *
     * @param id
     * @return
     */
    @Override
    protected ProgressDialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Telechargement …");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }
}
