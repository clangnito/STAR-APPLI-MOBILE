package com.example.constantlangnito.starv1dl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.constantlangnito.starv1dl.Table.BusRoute;
import com.example.constantlangnito.starv1dl.Table.Stop;
import com.example.constantlangnito.starv1dl.Table.StopTime;
import com.example.constantlangnito.starv1dl.Table.Trip;
import com.example.constantlangnito.starv1dl.Table.Calendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;
import static com.example.constantlangnito.starv1dl.VariablesStatic.CALENDAR_CSV_FILE;
import static com.example.constantlangnito.starv1dl.VariablesStatic.DOWNLOAD_PATH;
import static com.example.constantlangnito.starv1dl.VariablesStatic.INIT_FOLDER_PATH;
import static com.example.constantlangnito.starv1dl.VariablesStatic.STOPS_CSV_FILE;
import static com.example.constantlangnito.starv1dl.VariablesStatic.STOP_TIMES_CSV_FILE;
import static com.example.constantlangnito.starv1dl.VariablesStatic.TRIPS_CSV_FILE;


/**
 * Created by langnito on 02/12/2018.
 */

public class DatabaseManager extends SQLiteOpenHelper implements StarContract {

    private static File DEVICE_ROOT_FOLDER = getExternalStorageDirectory();
    public SQLiteDatabase database;
    private static String DB_NAME = "starBusDb";
    private static final int DATA_BASE_VERSION = 1;
    /**
     */


    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DATA_BASE_VERSION);
        Log.d("DB CREATE", "Creation de la base de donnees");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VariablesStatic.CREATE_T_BUS_ROUTE_TABLE);
        db.execSQL(VariablesStatic.CREATE_T_CALENDAR_TABLE);
        db.execSQL(VariablesStatic.CREATE_STOP_TIMES_TABLE);
        db.execSQL(VariablesStatic.CREATE_T_STOPS_TABLE);
        db.execSQL(VariablesStatic.CREATE_T_TRIPS_TABLE);
        db.execSQL(VariablesStatic.CREATE_VERSIONS_TABLE);

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
     * Insert des données dans les tables
     */
    public void insertAll() {
        //insertStopTimes();
        //insertCalendars();
        insertBusRoutes();
        insertStops();
        insertTrips();


    }


    public void insertStopTimes() {
        database = this.getWritableDatabase();
        ArrayList<StopTime> stopTimes = new ArrayList<StopTime>();
        try {
            stopTimes = loadStopTimesData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            database.beginTransaction();
            String sql = "INSERT INTO " + StopTimes.CONTENT_PATH + " (" +
                    StopTimes.StopTimeColumns.TRIP_ID + "," +
                    StopTimes.StopTimeColumns.ARRIVAL_TIME + "," +
                    StopTimes.StopTimeColumns.DEPARTURE_TIME + "," +
                    StopTimes.StopTimeColumns.STOP_ID + "," +
                    StopTimes.StopTimeColumns.STOP_SEQUENCE +
                    ") VALUES (?,?,?,?,?)";
            SQLiteStatement insert = database.compileStatement(sql);
            for (int i = 0; i < stopTimes.size(); i++) {
                StopTime item = stopTimes.get(i);
                insert.bindLong(1, item.getTripId());
                insert.bindString(2, item.getArrivalTime());
                insert.bindString(3, item.getDepartureTme());
                insert.bindLong(4, item.getStopId());
                insert.bindString(5, item.getStopSequence());
                Log.d("STARX", i + " -- stoptime added");
                insert.execute();
            }
            database.setTransactionSuccessful();
            Log.d("STARX", stopTimes.size() + " stoptimes added ----------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }


    public void insertTrips() {
        ArrayList<Trip> items = new ArrayList<Trip>();
        try {
            items = loadTripsData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            database = this.getWritableDatabase();
            database.beginTransaction();
            String sql = "INSERT INTO " + StarContract.Trips.CONTENT_PATH + " (" +
                    Trips.TripColumns.ROUTE_ID+ "," +
                    Trips.TripColumns.SERVICE_ID+ "," +
                    Trips.TripColumns.HEADSIGN+ "," +
                    Trips.TripColumns.DIRECTION_ID+ "," +
                    Trips.TripColumns.BLOCK_ID+ "," +
                    Trips.TripColumns.WHEELCHAIR_ACCESSIBLE+
                    ") VALUES (?,?,?,?,?,?,?)";
            SQLiteStatement insert = database.compileStatement(sql);
            for (int i = 0; i < items.size(); i++) {
                Trip item = items.get(i);
                insert.bindLong(1, item.getTripId());
                insert.bindLong(2, item.getRouteId());
                insert.bindLong(3, item.getServiceId());
                insert.bindString(4, item.getHeadSign());
                insert.bindLong(5, item.getDirectionId());
                insert.bindString(6, item.getBlockId());
                insert.bindString(7, item.getWheelchairAccessible());
                Log.d("STARX", i + " -- Trips added");
                insert.execute();
            }
            database.setTransactionSuccessful();
            Log.d("STARX", items.size() + " Calendar added ----------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * Loads Trip from the csv file
     *
     * @return
     * @throws IOException
     */
    public static ArrayList<Trip> loadTripsData() throws IOException {
        Log.d("STARXC", "start loading... " + DEVICE_ROOT_FOLDER + "/" + INIT_FOLDER_PATH + DOWNLOAD_PATH + "/" + TRIPS_CSV_FILE);
        ArrayList<Trip> trips = new ArrayList<>();
        FileReader file = new FileReader(new File(DEVICE_ROOT_FOLDER, INIT_FOLDER_PATH + TRIPS_CSV_FILE));
        BufferedReader buffer = new BufferedReader(file);
        String line = "";
        int i = 0;
        while ((line = buffer.readLine()) != null) {
            if (i != 0) {
                String[] str = line.split(",");
                int routeId = Integer.valueOf(str[0].replaceAll("\"", ""));
                int tripId = Integer.valueOf(str[2].replaceAll("\"", ""));
                int serviceId = Integer.valueOf(str[1].replaceAll("\"", ""));
                int direction = Integer.valueOf(str[5].replaceAll("\"", ""));
                String block = str[6].replaceAll("\"", "");
                Trip trip = new Trip(tripId,
                        routeId,
                        serviceId,
                        str[3].replaceAll("\"", ""),
                        direction,
                        block,
                        str[8].replaceAll("\"", ""));
                trips.add(trip);
                Log.d("STARXT", i + "-loaded... " + trip.toString());
            }
            i++;
        }
        buffer.close();
        file.close();
        Log.d("STARXT", i + " trips loaded");
        return trips;
    }


    /**
     * Loads StopTimes from the csv file
     *
     * @return
     * @throws IOException
     */
    public ArrayList<StopTime> loadStopTimesData() throws IOException {
        Log.d("STARXC", "start loading... " + DEVICE_ROOT_FOLDER + "/" + INIT_FOLDER_PATH + STOP_TIMES_CSV_FILE);
        ArrayList<StopTime> fileStopTimes = new ArrayList<>();
        long items = 0;
        long allStoptimes = 0;
        String currfile = INIT_FOLDER_PATH + STOP_TIMES_CSV_FILE;
        Log.d("STARX", "current file ..: " + currfile);
        FileReader file = new FileReader(new File(DEVICE_ROOT_FOLDER, currfile));
        BufferedReader buffer = new BufferedReader(file);
        String line = "";
        long nb = 0;
        while ((line = buffer.readLine()) != null) {
            if (nb != 0) {
                String[] str = line.split(",");
                int tripId = Integer.valueOf(str[0].replaceAll("\"", ""));
                int stopId = Integer.valueOf(str[3].replaceAll("\"", ""));
                StopTime stopTime = new StopTime(
                        tripId, str[1].replaceAll("\"", ""),
                        str[2].replaceAll("\"", ""),
                        stopId,
                        str[4].replaceAll("\"", ""));
                fileStopTimes.add(stopTime);
                Log.d("STARXST", items + "-loaded... " + stopTime.toString());
            }
            items++;
            nb++;
        }
        buffer.close();
        file.close();
        allStoptimes += items;
        Log.d("STARXST", items + " stopTimes loaded");
        return fileStopTimes;
    }







    public void insertBusRoutes() {
        ArrayList<BusRoute> items = new ArrayList<BusRoute>();
        database = this.getWritableDatabase();
        try {
            items = loadBusRoutesData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            String sqlDelete = "DELETE FROM " + StarContract.BusRoutes.CONTENT_PATH;
            SQLiteStatement delete = database.compileStatement(sqlDelete);
            delete.execute();
            Log.d("STAR DELETE FROM", " Bus Route ");

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
                insert.bindString(2, item.getshortName());
                insert.bindString(3, item.getLongName());
                insert.bindString(4, item.getDescription());
                insert.bindString(5, item.getType());
                insert.bindString(6, item.getColor());
                insert.bindString(7, item.getTextColor());
                insert.execute();
            }
            database.setTransactionSuccessful();
            Log.d("STAR", " Bus Route add ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }


    public void insertStops() {
        ArrayList<Stop> items = new ArrayList<Stop>();
        try {
            items = loadStopsData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            database = this.getWritableDatabase();
            database.beginTransaction();
            String sql = "INSERT INTO " + StarContract.Stops.CONTENT_PATH + " (" +
                    Stops.StopColumns.NAME + "," +
                    Stops.StopColumns.DESCRIPTION + "," +
                    Stops.StopColumns.LATITUDE + "," +
                    Stops.StopColumns.LONGITUDE + "," +
                    Stops.StopColumns.WHEELCHAIR_BOARDING +
                    ") VALUES (?,?,?,?,?,?)";
            SQLiteStatement insert = database.compileStatement(sql);
            for (int i = 0; i < items.size(); i++) {
                Stop item = items.get(i);
                insert.bindString(1, item.getId());
                insert.bindString(2, item.getName());
                insert.bindString(3, item.getDescription());
                insert.bindLong(4, (long) item.getLatitude());
                insert.bindLong(5, (long) item.getLongitude());
                insert.bindString(6, item.getWheelChairBoalding());
                Log.d("STARX", i + " -- Stops added");
                insert.execute();
            }
            database.setTransactionSuccessful();
            Log.d("STARX", items.size() + " Stops added ----------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * Loads Stops from the csv file
     *
     * @return
     * @throws IOException
     */
    public static ArrayList<Stop> loadStopsData() throws IOException {
        Log.d("STARXC", "start loading... " + DEVICE_ROOT_FOLDER + "/" + INIT_FOLDER_PATH + DOWNLOAD_PATH + "/" + STOPS_CSV_FILE);
        ArrayList<Stop> stops = new ArrayList<>();
        FileReader file = new FileReader(new File(DEVICE_ROOT_FOLDER, INIT_FOLDER_PATH+ STOPS_CSV_FILE));
        Log.e("STARXS", " Absolut Path for file" + file.toString());
        BufferedReader buffer = new BufferedReader(file);
        String line = "";
        int i = 0;
        while ((line = buffer.readLine()) != null) {
            if (i != 0) {
                String[] str = line.split(",");
                String id = str[0].replaceAll("\"", "");
                String name = str[2].replaceAll("\"", "");
                String description = str[3].replaceAll("\"", "");
                float latitude = Float.valueOf(str[4].replace('"', ' '));
                float longitude = Float.valueOf(str[5].replace('"', ' '));
                String wheelChairBoalding = str[11].replaceAll("\"", "");
                Stop stop = new Stop(id, name, description, latitude, longitude, wheelChairBoalding);
                stops.add(stop);
                Log.d("STARXS", i + "-loaded... " + stop.toString());
            }
            i++;
        }
        buffer.close();
        file.close();
        Log.d("STARXS", i + " stops loaded");
        return stops;
    }


    /**
     *
     * @return
     * @throws IOException
     */
    public static ArrayList<BusRoute> loadBusRoutesData() throws IOException {
        Log.d("STARXC", "start loading... " + DEVICE_ROOT_FOLDER + "/" + INIT_FOLDER_PATH  + DOWNLOAD_PATH + "/" + VariablesStatic.BUS_ROUTES_CSV_FILE);
        ArrayList<BusRoute> busRoutes = new ArrayList<>();
        FileReader file = new FileReader(new File(DEVICE_ROOT_FOLDER, INIT_FOLDER_PATH + DOWNLOAD_PATH + "/" + VariablesStatic.BUS_ROUTES_CSV_FILE));
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
                //Log.d("STARX", "load from db..." + item);
            } while (cursor.moveToNext());
            // Log.d("STARX", "-----   " + data.size() + " BusRoutes loaded form database ");
        }
        cursor.close();
        return data;
    }


    /**
     *
     * @param file
     * @throws IOException
     */
    private static void deleteDossier(File file) throws IOException {

        for (File childFile : file.listFiles()) {

            if (childFile.isDirectory()) {
                deleteDossier(childFile);
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