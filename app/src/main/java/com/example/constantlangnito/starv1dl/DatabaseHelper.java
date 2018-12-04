package com.example.constantlangnito.starv1dl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;




import static android.os.Environment.getExternalStorageDirectory;


/**
 * Created by langnito on 20/11/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper implements StarContract {

    private static File DEVICE_ROOT_FOLDER = getExternalStorageDirectory();
    public SQLiteDatabase database;
    private static String DATA_NAME = "starBusDb";
    private static final int DATA_BASE_VERSION = 1;
    /**
     * Paths used to load csv files
     */
    public static String INIT_FOLDER_PATH = "star1DL/"; // From the root folder of the device
    public static String DOWNLOAD_PATH = "GTFS_2018.3.0.2_2018-11-26_2018-12-23";

    private static String CALENDAR_CSV_FILE = "calendar.txt";
    private static String BUS_ROUTES_CSV_FILE = "routes.txt";
    private static String STOPS_CSV_FILE = "stops.txt";
    private static String STOP_TIMES_CSV_FILE = "stop_times.txt";
    private static final String STOP_TIMES_SPLIT_CSV_FILE = "stop_times_";
    private static String TRIPS_CSV_FILE = "trips.txt";

    public DatabaseHelper(Context context) {
        super(context, DATA_NAME, null, DATA_BASE_VERSION);
        Log.d("DB_CREATE", "db cretaed");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB_T_ROUTE_TABLE", "db cretaed");
        db.execSQL(Constants.CREATE_BUS_ROUTE_TABLE);
        Log.d("CREATE_BUS_ROUTE_TABLE", "db cretaed");
        /*db.execSQL(Constants.CREATE_CALENDAR_TABLE);
        db.execSQL(Constants.CREATE_STOP_TIMES_TABLE);
        db.execSQL(Constants.CREATE_STOPS_TABLE);
        db.execSQL(Constants.CREATE_TRIPS_TABLE);
        db.execSQL(Constants.CREATE_VERSIONS_TABLE);*/
        /*
          Initialisation des versions

        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();
        values.put(Constants.VERSIONS_FILE_NAME_COL, "file1");
        values2.put(Constants.VERSIONS_FILE_NAME_COL, "file2");
        values.put(Constants.VERSIONS_FILE_VERSION_COL, Constants.DEFAULT_FIRST_VERSION);
        values2.put(Constants.VERSIONS_FILE_VERSION_COL, Constants.DEFAULT_FIRST_VERSION);
        db.insert(Constants.VERSIONS_TABLE, null, values);
        db.insert(Constants.VERSIONS_TABLE, null, values2);
        Log.d("STARX", "db cretaed");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public List<String> allTableNames() {
        List<String> result = new ArrayList<>();
        String selectQuery = "select name from sqlite_master where type = 'table'";
        Cursor cursor = this.getReadableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                result.add(name);
            } while (cursor.moveToNext());
        }
        return result;
    }

    /**
     * Insert toutes les données disponibles dans le dossier
     */
    public void insertAll() {
       // insertStopTimes();
       // insertCalendars();
        insertBusRoutes();
        //insertStops();
        //insertTrips();

        /**
         * Supprime les données après leurs insertion

        try {
            deleteDirectory(new File(DEVICE_ROOT_FOLDER + "/" + INIT_FOLDER_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Insert a BusRoute in the db
     *
     * @param route
     */
    public void insertRoute(BusRoute route) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("route_id", route.getRoute_id());
        values.put(BusRoutes.BusRouteColumns.SHORT_NAME, route.getShortName());
        values.put(BusRoutes.BusRouteColumns.LONG_NAME, route.getLongName());
        values.put(BusRoutes.BusRouteColumns.DESCRIPTION, route.getDescription());
        values.put(BusRoutes.BusRouteColumns.TYPE, route.getType());
        values.put(BusRoutes.BusRouteColumns.COLOR, route.getColor());
        values.put(BusRoutes.BusRouteColumns.TEXT_COLOR, route.getTextColor());
        database.insert(BusRoutes.CONTENT_PATH, null, values);
    }

    /**
     * Insert all BusRoute from the csv file to the db
     */
    public void insertBusRoutes() {
        ArrayList<BusRoute> items = new ArrayList<BusRoute>();
        database = this.getWritableDatabase();
        try {
            items = loadBusRoutesData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            database.beginTransaction();
            String sql = "INSERT INTO " + StarContract.BusRoutes.CONTENT_PATH + " (route_id," +
                    BusRoutes.BusRouteColumns.SHORT_NAME + "," +
                    BusRoutes.BusRouteColumns.LONG_NAME + "," +
                    BusRoutes.BusRouteColumns.DESCRIPTION + "," +
                    BusRoutes.BusRouteColumns.TYPE + "," +
                    BusRoutes.BusRouteColumns.COLOR + "," +
                    BusRoutes.BusRouteColumns.TEXT_COLOR +
                    ") VALUES (?,?,?,?,?,?,?)";
            SQLiteStatement insert = database.compileStatement(sql);
            for (int i = 0; i < items.size(); i++) {
                BusRoute item = items.get(i);
                insert.bindString(1, item.getRoute_id());
                insert.bindString(2, item.getShortName());
                insert.bindString(3, item.getLongName());
                insert.bindString(4, item.getDescription());
                insert.bindString(5, item.getType());
                insert.bindString(6, item.getColor());
                insert.bindString(7, item.getTextColor());
                Log.d("STARX", i + " -- BusRoute added");
                insert.execute();
            }
            database.setTransactionSuccessful();
            Log.d("STARX", items.size() + " BusRoute added ----------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }


    /**
     * Loads BusRoutes from the csv file
     *
     * @return
     * @throws IOException
     */
    public static ArrayList<BusRoute> loadBusRoutesData() throws IOException {
        Log.d("STARXC", "start loading... " + DEVICE_ROOT_FOLDER + "/" + INIT_FOLDER_PATH + DOWNLOAD_PATH + "/" + BUS_ROUTES_CSV_FILE);
        ArrayList<BusRoute> busRoutes = new ArrayList<>();
        FileReader file = new FileReader(new File(DEVICE_ROOT_FOLDER, INIT_FOLDER_PATH + DOWNLOAD_PATH  + "/" + BUS_ROUTES_CSV_FILE));
        BufferedReader buffer = new BufferedReader(file);
        String line = "";
        int i = 0;
        while ((line = buffer.readLine()) != null) {
            if (i != 0) {
                String[] str = line.split(",");
                BusRoute br = new BusRoute(
                        str[0].replaceAll("\"", ""),
                        str[2].replaceAll("\"", ""),
                        str[3].replaceAll("\"", ""),
                        str[4].replaceAll("\"", ""),
                        str[5].replaceAll("\"", ""),
                        str[7].replaceAll("\"", ""),
                        str[8].replaceAll("\"", ""));
                busRoutes.add(br);
                Log.d("STARXBR", i + "-loaded... " + br.toString());
            }
            i++;
        }
        buffer.close();
        file.close();
        Log.d("STARXBR", i + " busRoutes loaded");
        return busRoutes;
    }


    /**
     * Load BusRoutes from database
     *
     * @return
     */
    public ArrayList<BusRoute> getBusRoutesFromDatabase() {
        String selectQuery = "SELECT  * FROM " + StarContract.BusRoutes.CONTENT_PATH;
        ArrayList<BusRoute> data = new ArrayList<>();
        Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                BusRoute item = new BusRoute(
                        cursor.getString(cursor.getColumnIndex("route_id")),
                        cursor.getString(cursor.getColumnIndex(StarContract.BusRoutes.BusRouteColumns.SHORT_NAME)),
                        cursor.getString(cursor.getColumnIndex(StarContract.BusRoutes.BusRouteColumns.LONG_NAME)),
                        cursor.getString(cursor.getColumnIndex(StarContract.BusRoutes.BusRouteColumns.DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(StarContract.BusRoutes.BusRouteColumns.TYPE)),
                        cursor.getString(cursor.getColumnIndex(StarContract.BusRoutes.BusRouteColumns.COLOR)),
                        cursor.getString(cursor.getColumnIndex(StarContract.BusRoutes.BusRouteColumns.TEXT_COLOR))
                );
                data.add(item);
                Log.d("STARX", "load from db..." + item);
            } while (cursor.moveToNext());
            Log.d("STARX", "-----   " + data.size() + " BusRoutes loaded form database ");
        }
        cursor.close();
        return data;
    }



    /**
     * Supprimer un dossier
     * @param file
     * @throws IOException
     */
    private static void deleteDirectory(File file) throws IOException {

        for (File childFile : file.listFiles()) {

            if (childFile.isDirectory()) {
                deleteDirectory(childFile);
            } else {
                if (!childFile.delete()) {
                    throw new IOException();
                }
            }
        }

        if (!file.delete()) {
            throw new IOException();
        }
    }
}