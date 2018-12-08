package com.example.constantlangnito.starv1dl;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ProgressDialog;

import cz.msebera.android.httpclient.Header;


import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {

    public static Button buttonTelecharger;
    public static Button buttonLister;
    private ProgressDialog mProgressDialog;

    DatabaseManager databaseHelper;

    private String exportPath = "";

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseManager(getApplicationContext());
        activerPermissions(this);

        onNewIntent(getIntent());

        buttonTelecharger = (Button)findViewById(R.id.button_telechargement);
        buttonLister = (Button)findViewById(R.id.button_listerBus);
        buttonTelecharger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadeZipFile("http://ftp.keolis-rennes.com/opendata/tco-busmetro-horaires-gtfs-versions-td/attachments/GTFS_2018.3.0.2_2018-11-26_2018-12-23.zip");
            }
        });


        buttonLister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListerBusMetro.class));
            }
        });
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
                Log.e("STARX", "success start");

                try {
                    //Splitting a File Name from SourceFileName
                    String DestinationName = sourceFilname.substring(sourceFilname.lastIndexOf('/') + 1, sourceFilname.length());
                    String INIT_FOLDER_PATH = "star1DL/";

                    File DEVICE_ROOT_FOLDER = getExternalStorageDirectory();
                    File file = new File((DEVICE_ROOT_FOLDER + "/" + INIT_FOLDER_PATH));

                    if (! file.exists()){
                        file.mkdir();
                    }

                   // DatabaseManager.INIT_FOLDER_PATH = INIT_FOLDER_PATH + DestinationName.substring(0, DestinationName.lastIndexOf(".")) + "/";
                    //Saving a File into Download Folder


                    File _f = new File(file, DestinationName);

                    FileOutputStream output = new FileOutputStream(_f);

                    Log.e("STARX", "success try");
                    output.write(binaryData);
                    output.close();
                    Log.e("STARX", "" + _f);

                    // Debut du deziping
                    exportPath = _f.getAbsolutePath();
                    exportPath = exportPath.replace(".zip", "");
                    Log.e("STARX", "==> " + exportPath);

                    //DatabaseManager.DOWNLOAD_PATH = exportPath;
                    exportPath = exportPath + "/";

                    // decropress file in folder whith id name
                    DecompresZip df = new DecompresZip(_f.getAbsolutePath(), exportPath);
                    df.unzip();

                    /**
                     * Inserer les données télechargées
                     */
                    DatabaseManager databaseHelper = new DatabaseManager(getApplicationContext());
                    databaseHelper.insertAll();
                    dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
                    mProgressDialog.dismiss();
                    buttonTelecharger.setText("fin de telechargement");
                } catch (IOException e) {
                    Log.e("STARX", "success catch");
                    e.printStackTrace();
                }
            }


            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int val = (int) ((bytesWritten * 100) / totalSize);
                Log.d("STARX", "downloading ..... " + val);
                mProgressDialog.setProgress(val);
                mProgressDialog.getCurrentFocus();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

                Log.e("STARX", "==> " + error);

            }


        });
    }

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
                mProgressDialog.setMessage("Downloading file…");
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
