package com.example.constantlangnito.starv1dl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.constantlangnito.starv1dl.Table.BusRoute;
import com.example.constantlangnito.starv1dl.Table.Stop;
import com.example.constantlangnito.starv1dl.Table.StopTime;
import com.example.constantlangnito.starv1dl.Table.Trip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;
//import static com.example.constantlangnito.starv1dl.VariablesStatic.DOWNLOAD_PATH;
import static com.example.constantlangnito.starv1dl.VariablesStatic.INIT_FOLDER_PATH;
import static com.example.constantlangnito.starv1dl.VariablesStatic.STOPS_CSV_FILE;
import static com.example.constantlangnito.starv1dl.VariablesStatic.TRIPS_CSV_FILE;


/**
 * Created by langnito on 02/12/2018.
 */

public class DatabaseManager extends SQLiteOpenHelper implements StarContract {

    private static File DEVICE_ROOT_FOLDER = getExternalStorageDirectory();
    public SQLiteDatabase database;
    private static String DB_NAME = "starBusDb";
    private static final int DATA_BASE_VERSION = 1;
    private static String DOWNLOAD_PATH = "";
    String splitor = "<>";

    /**
     */


    public DatabaseManager(Context context, String sDOWNLOAD_PATH) {
        super(context, DB_NAME, null, DATA_BASE_VERSION);
        DOWNLOAD_PATH = sDOWNLOAD_PATH;
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


    public List<String> getAllNameListeBus(){
        List<String> listeBus = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + BusRoutes.CONTENT_PATH;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                listeBus.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return listeBus;
    }

    public List<String> getDirectionBus(int position){


        ArrayList<BusRoute> listeBusDataBase = getBusRoutesFromDatabase();

        BusRoute busRoute = listeBusDataBase.get(position);

        List<String> listeDirection = new ArrayList<String>();

        listeDirection = getDirections(busRoute.getRoute_long_name());

       return  listeDirection;
    }

    public ArrayList<String> getDirections(@NonNull String allDirections) {
        String[] directions = allDirections.split(splitor);
        ArrayList<String> directionsList = new ArrayList<>();
        directionsList.addAll(Arrays.asList(directions));
        return directionsList;
    }

    /**
     * Insert des données dans les tables
     */
    public void insertAll() {
        insertStopTimes();
        insertCalendars();
        insertBusRoutes();
        insertStops();
        insertTrips();


    }


    public void insertStopTimes() {

        Log.d("insertStopTimes", "debut ");
        database = this.getWritableDatabase();

        try {

            String sqlDelete = "DELETE FROM " + StarContract.StopTimes.CONTENT_PATH;
            SQLiteStatement delete = database.compileStatement(sqlDelete);
            delete.execute();

            BufferedReader stopTimesBuffered = loadStopTimesData();
            stopTimesBuffered.readLine();
            int i = 0;
            StringBuilder sql = new StringBuilder();
            String line;
            String[] splits;
            database.beginTransaction();
            try {
                while ((line = stopTimesBuffered.readLine()) != null) {
                    if (i == 20000) {
                        database.execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES %s",
                                StopTimes.CONTENT_PATH, StopTimes.StopTimeColumns.TRIP_ID,
                                StopTimes.StopTimeColumns.ARRIVAL_TIME, StopTimes.StopTimeColumns.DEPARTURE_TIME,
                                StopTimes.StopTimeColumns.STOP_ID, StopTimes.StopTimeColumns.STOP_SEQUENCE,
                                sql.toString())
                        );
                        Log.e("ATG", "il y a eu une insertion");
                        i = 0;
                        sql = new StringBuilder();
                    }

                    if (i != 0) sql.append(", ");

                    splits = line.split(",");
                    sql.append(String.format("(%s, %s, %s, %s, %s)",
                            splits[0], splits[1], splits[2], splits[3], splits[4]));
                    ++i;
                }

                if (i > 0) {
                    database.execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES %s",
                            StopTimes.CONTENT_PATH, StopTimes.StopTimeColumns.TRIP_ID,
                            StopTimes.StopTimeColumns.ARRIVAL_TIME, StopTimes.StopTimeColumns.DEPARTURE_TIME,
                            StopTimes.StopTimeColumns.STOP_ID, StopTimes.StopTimeColumns.STOP_SEQUENCE,
                            sql.toString()));
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        } catch (IOException ignored) {
        }
    }


    public void insertCalendars() {

        Log.d("insertCalendars", "debut ");
        database = this.getWritableDatabase();

        try {

            String sqlDelete = "DELETE FROM " + StarContract.Calendar.CONTENT_PATH;
            SQLiteStatement delete = database.compileStatement(sqlDelete);
            delete.execute();

            BufferedReader calendarBuffered = loadCalendarData();
            calendarBuffered.readLine();
            int i = 0;
            StringBuilder sql = new StringBuilder();
            String line;
            String[] splits;
            database.beginTransaction();
            try {
                while ((line = calendarBuffered.readLine()) != null) {
                    if (i == 50) {
                        database.execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES %s",
                                Calendar.CONTENT_PATH, Calendar.CalendarColumns.SERVICE_ID, Calendar.CalendarColumns.MONDAY, Calendar.CalendarColumns.TUESDAY,
                                Calendar.CalendarColumns.WEDNESDAY, Calendar.CalendarColumns.THURSDAY,Calendar.CalendarColumns.FRIDAY,
                                Calendar.CalendarColumns.SATURDAY, Calendar.CalendarColumns.SUNDAY,Calendar.CalendarColumns.START_DATE,
                                Calendar.CalendarColumns.END_DATE, sql.toString()));

                        Log.e("ATG", "il y a eu une insertion Calendars");
                        i = 0;
                        sql = new StringBuilder();
                    }

                    if (i != 0) sql.append(", ");

                    splits = line.split(",");
                    sql.append(String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                            splits[0], splits[1], splits[2], splits[3], splits[4], splits[5], splits[6], splits[7], splits[8], splits[9]));
                    ++i;


                }

                if (i > 0) {
                    database.execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES %s",
                            Calendar.CONTENT_PATH, Calendar.CalendarColumns.SERVICE_ID, Calendar.CalendarColumns.MONDAY, Calendar.CalendarColumns.TUESDAY,
                            Calendar.CalendarColumns.WEDNESDAY, Calendar.CalendarColumns.THURSDAY,Calendar.CalendarColumns.FRIDAY,
                            Calendar.CalendarColumns.SATURDAY, Calendar.CalendarColumns.SUNDAY,Calendar.CalendarColumns.START_DATE,
                            Calendar.CalendarColumns.END_DATE, sql.toString()));
                    Log.e("ATG", "fin insertion Calendars");
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        } catch (IOException ignored) {
        }
    }



    /**
     * Loads Stops from the csv file
     *
     * @return
     * @throws IOException
     */
    public static BufferedReader loadTripsData() throws IOException {
        FileReader file = new FileReader(new File( DOWNLOAD_PATH + "/" + VariablesStatic.TRIPS_CSV_FILE));
        return new BufferedReader(file);
    }



    /**
     * Loads Stops from the csv file
     *
     * @return
     * @throws IOException
     */
    public static BufferedReader loadStopTimesData() throws IOException {
        FileReader file = new FileReader(new File( DOWNLOAD_PATH + "/" + VariablesStatic.STOP_TIMES_CSV_FILE));
        return new BufferedReader(file);
    }

    /**
     * Loads Stops from the csv file
     *
     * @return
     * @throws IOException
     */
    public static BufferedReader loadCalendarData() throws IOException {
        FileReader file = new FileReader(new File( DOWNLOAD_PATH + "/" + VariablesStatic.CALENDAR_CSV_FILE));
        return new BufferedReader(file);
    }

    /**
     * Loads Stops from the csv file
     *
     * @return
     * @throws IOException
     */
    public static BufferedReader loadBusRoutesData() throws IOException {
        FileReader file = new FileReader(new File(  DOWNLOAD_PATH + "/" + VariablesStatic.BUS_ROUTES_CSV_FILE));
        return new BufferedReader(file);
    }


    /**
     * Loads Stops from the csv file
     *
     * @return
     * @throws IOException
     */
    public static BufferedReader loadStopsData() throws IOException {
        FileReader file = new FileReader(new File( DOWNLOAD_PATH + "/" + VariablesStatic.STOPS_CSV_FILE));
        return new BufferedReader(file);
    }


    public void insertBusRoutes() {

        Log.d("insertBusRoutes", "debut ");
        database = this.getWritableDatabase();

        try {

            String sqlDelete = "DELETE FROM " + StarContract.BusRoutes.CONTENT_PATH;
            SQLiteStatement delete = database.compileStatement(sqlDelete);
            delete.execute();

            BufferedReader BusRoutesBuffered = loadBusRoutesData();
            BusRoutesBuffered.readLine();
            int i = 0;
            StringBuilder sql = new StringBuilder();
            String line;
            String[] splits;
            database.beginTransaction();
            try {
                while ((line = BusRoutesBuffered.readLine()) != null) {
                    if (i == 50) {
                        database.execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES %s",
                                BusRoutes.CONTENT_PATH, "route_id", BusRoutes.BusRouteColumns.SHORT_NAME
                                , BusRoutes.BusRouteColumns.LONG_NAME, BusRoutes.BusRouteColumns.DESCRIPTION
                                , BusRoutes.BusRouteColumns.TYPE, BusRoutes.BusRouteColumns.COLOR,
                                BusRoutes.BusRouteColumns.TEXT_COLOR, sql.toString()));

                        Log.e("ATG", "il y a eu une insertion BusRoutes");
                        i = 0;
                        sql = new StringBuilder();
                    }

                    if (i != 0) sql.append(", ");

                    splits = line.split(",");
                    sql.append(String.format("(%s, %s, %s, %s, %s, %s, %s)",
                            splits[0], splits[2], splits[3], splits[4], splits[5], splits[7], splits[8]));
                    ++i;

                }

                if (i > 0) {
                    database.execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES %s",
                            BusRoutes.CONTENT_PATH, "route_id", BusRoutes.BusRouteColumns.SHORT_NAME
                            , BusRoutes.BusRouteColumns.LONG_NAME, BusRoutes.BusRouteColumns.DESCRIPTION
                            , BusRoutes.BusRouteColumns.TYPE, BusRoutes.BusRouteColumns.COLOR,
                            BusRoutes.BusRouteColumns.TEXT_COLOR, sql.toString()));
                    Log.e("ATG", "fin insertion Stop");
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        } catch (IOException ignored) {
        }
    }




    public void insertStops() {

        Log.d("insertStops", "debut ");
        database = this.getWritableDatabase();

        try {

            String sqlDelete = "DELETE FROM " + StarContract.Stops.CONTENT_PATH;
            SQLiteStatement delete = database.compileStatement(sqlDelete);
            delete.execute();

            BufferedReader stopsBuffered = loadStopsData();
            stopsBuffered.readLine();
            int i = 0;
            StringBuilder sql = new StringBuilder();
            String line;
            String[] splits;
            database.beginTransaction();
            try {
                while ((line = stopsBuffered.readLine()) != null) {
                    if (i == 500) {
                        database.execSQL(String.format("INSERT INTO %s (%s,%s, %s, %s, %s, %s) VALUES %s",
                                Stops.CONTENT_PATH, Stops.StopColumns.STOP_ID, Stops.StopColumns.NAME, Stops.StopColumns.DESCRIPTION,
                                Stops.StopColumns.LATITUDE, Stops.StopColumns.LONGITUDE,
                                Stops.StopColumns.WHEELCHAIR_BOARDING, sql.toString()));

                        Log.e("ATG", "il y a eu une insertion Stop");
                        i = 0;
                        sql = new StringBuilder();
                    }

                    if (i != 0) sql.append(", ");

                    splits = line.split(",");
                    sql.append(String.format("(%s, %s, %s, %s, %s, %s)",
                            splits[0],splits[2], splits[3], splits[4], splits[5], splits[11]));
                    ++i;

                }

                if (i > 0) {
                    database.execSQL(String.format("INSERT INTO %s (%s,%s, %s, %s, %s, %s) VALUES %s",
                            Stops.CONTENT_PATH, Stops.StopColumns.STOP_ID, Stops.StopColumns.NAME, Stops.StopColumns.DESCRIPTION,
                            Stops.StopColumns.LATITUDE, Stops.StopColumns.LONGITUDE,
                            Stops.StopColumns.WHEELCHAIR_BOARDING, sql.toString()));
                    Log.e("ATG", "fin insertion Stop");
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        } catch (IOException ignored) {
        }
    }



    public void insertTrips() {

        Log.d("insertTrips", "debut ");
        database = this.getWritableDatabase();

        try {

            String sqlDelete = "DELETE FROM " + StarContract.Trips.CONTENT_PATH;
            SQLiteStatement delete = database.compileStatement(sqlDelete);
            delete.execute();

            BufferedReader TripsBuffered = loadTripsData();
            TripsBuffered.readLine();
            int i = 0;
            StringBuilder sql = new StringBuilder();
            String line;
            String[] splits;
            database.beginTransaction();
            try {
                while ((line = TripsBuffered.readLine()) != null) {
                    if (i == 10000) {
                        database.execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES %s",
                                Trips.CONTENT_PATH, Trips.TripColumns.TRIP_ID, Trips.TripColumns.ROUTE_ID, Trips.TripColumns.SERVICE_ID,
                                Trips.TripColumns.HEADSIGN, Trips.TripColumns.DIRECTION_ID,Trips.TripColumns.BLOCK_ID,
                                Trips.TripColumns.WHEELCHAIR_ACCESSIBLE, sql.toString()));

                        Log.e("ATG", "il y a eu une insertion insertTrips");
                        i = 0;
                        sql = new StringBuilder();
                    }

                    if (i != 0) sql.append(", ");

                    splits = line.split(",");
                    sql.append(String.format("(%s, %s, %s, %s, %s, %s, %s)",
                            splits[2],splits[0], splits[1], splits[3], splits[5], splits[6], splits[8]));
                    ++i;


                }

                if (i > 0) {
                    database.execSQL(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES %s",
                            Trips.CONTENT_PATH, Trips.TripColumns.TRIP_ID, Trips.TripColumns.ROUTE_ID, Trips.TripColumns.SERVICE_ID,
                            Trips.TripColumns.HEADSIGN, Trips.TripColumns.DIRECTION_ID,Trips.TripColumns.BLOCK_ID,
                            Trips.TripColumns.WHEELCHAIR_ACCESSIBLE, sql.toString()));
                    Log.e("ATG", "fin insertion Stop");
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        } catch (IOException ignored) {
        }

        String countStopTimes = "SELECT  count(*) FROM " + StopTimes.CONTENT_PATH;
        String countBusRoutes = "SELECT  count(*) FROM " + BusRoutes.CONTENT_PATH;
        String countCalendar = "SELECT  count(*) FROM " + Calendar.CONTENT_PATH;
        String countStops = "SELECT  count(*) FROM " + Stops.CONTENT_PATH;
        String countTrips = "SELECT  count(*) FROM " + Trips.CONTENT_PATH;

        Cursor mCount= database.rawQuery(countStopTimes, null);
        mCount.moveToFirst();
        int countStopTimesInt= mCount.getInt(0);
        mCount.close();

        mCount= database.rawQuery(countBusRoutes, null);
        mCount.moveToFirst();
        int countBusRoutesInt= mCount.getInt(0);
        mCount.close();

        mCount= database.rawQuery(countCalendar, null);
        mCount.moveToFirst();
        int countCalendarInt= mCount.getInt(0);
        mCount.close();

        mCount= database.rawQuery(countStops, null);
        mCount.moveToFirst();
        int countStopsInt= mCount.getInt(0);
        mCount.close();

        mCount= database.rawQuery(countTrips, null);
        mCount.moveToFirst();
        int countTripsInt= mCount.getInt(0);
        mCount.close();

        Log.d("insertStopTimes", " countStopTimesInt = " + countStopTimesInt
                + "countBusRoutesInt = " + countBusRoutesInt + "countCalendarInt = " + countCalendarInt
                + "countStopsInt = " + countStopsInt + "countTripsInt = " + countTripsInt);
    }




    /**
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
     * @return
     */
    public ArrayList<Stop> getArretBusForBusDatabase(String idBus, String direction) {
        String selectQuery = "SELECT DISTINCT " + Stops.CONTENT_PATH +
                             ".* FROM " + Trips.CONTENT_PATH + "," + StopTimes.CONTENT_PATH + "," + Stops.CONTENT_PATH +
                             " WHERE " + Trips.CONTENT_PATH + "." + Trips.TripColumns.TRIP_ID + "=" + StopTimes.CONTENT_PATH + "." + StopTimes.StopTimeColumns.TRIP_ID +
                             " AND " + Stops.CONTENT_PATH + "." + Stops.StopColumns.STOP_ID + "=" + StopTimes.CONTENT_PATH + "." + StopTimes.StopTimeColumns.STOP_ID +
                             " AND " + Trips.CONTENT_PATH + "." + Trips.TripColumns.ROUTE_ID + " = '"+idBus+"' " +
                             " AND " + Trips.CONTENT_PATH + "." + Trips.TripColumns.DIRECTION_ID + " = '"+direction+"' " +
                            "ORDER BY " + StopTimes.CONTENT_PATH + "." + StopTimes.StopTimeColumns.STOP_SEQUENCE +" DESC";
        ArrayList<Stop> data = new ArrayList<>();

        try(Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null)){
            while (cursor.moveToNext()){
                Stop item = new Stop(
                        cursor.getString(cursor.getColumnIndex(Stops.StopColumns.STOP_ID)),
                        cursor.getString(cursor.getColumnIndex(Stops.StopColumns.NAME)),
                        cursor.getString(cursor.getColumnIndex(Stops.StopColumns.DESCRIPTION)),
                        cursor.getFloat(cursor.getColumnIndex(Stops.StopColumns.LATITUDE)),
                        cursor.getFloat(cursor.getColumnIndex(Stops.StopColumns.LONGITUDE)),
                        cursor.getString(cursor.getColumnIndex(Stops.StopColumns.WHEELCHAIR_BOARDING)),
                        cursor.getString(cursor.getColumnIndex(Stops.StopColumns.STOP_ID)));
                data.add(item);
            }
        }

        return data;
    }



    public ArrayList<StopTime> getHeurePassageFromDatabase(String idBus, String direction) {
        String selectQuery = "SELECT " + StopTimes.CONTENT_PATH +
                ".*, calendar.start_date, calendar.end_date  FROM " + StopTimes.CONTENT_PATH + "," + Trips.CONTENT_PATH + "," + Calendar.CONTENT_PATH +
                " WHERE " + StopTimes.CONTENT_PATH + "." + StopTimes.StopTimeColumns.TRIP_ID + "=" + Trips.CONTENT_PATH + "." + Trips.TripColumns.TRIP_ID +
                " AND " + Trips.CONTENT_PATH + "." + Trips.TripColumns.SERVICE_ID + "=" + Calendar.CONTENT_PATH + "." + Calendar.CalendarColumns.SERVICE_ID +
                " AND " + StopTimes.CONTENT_PATH + "." + StopTimes.StopTimeColumns.STOP_ID + " = '1188' " +
                " AND " + Trips.CONTENT_PATH + "." + Trips.TripColumns.ROUTE_ID + " = '0004' " +
                " AND time("+ StopTimes.CONTENT_PATH +"."+ StopTimes.StopTimeColumns.ARRIVAL_TIME +") >= time('10:40')" +
                " AND " + Calendar.CONTENT_PATH + "." + Calendar.CalendarColumns.START_DATE + " <= current_date " +
                " AND current_date <= " + Calendar.CONTENT_PATH + "." + Calendar.CalendarColumns.END_DATE +
                " AND " + Calendar.CONTENT_PATH + "." + Calendar.CalendarColumns.SATURDAY + " = 1 " +
                "ORDER BY " + StopTimes.CONTENT_PATH + "." + StopTimes.StopTimeColumns.ARRIVAL_TIME +" asc";
        ArrayList<StopTime> data = new ArrayList<>();

        try(Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null)){
            while (cursor.moveToNext()){
                StopTime item = new StopTime(
                        cursor.getInt(cursor.getColumnIndex(StopTimes.StopTimeColumns.TRIP_ID)),
                        cursor.getString(cursor.getColumnIndex(StopTimes.StopTimeColumns.ARRIVAL_TIME)),
                        cursor.getString(cursor.getColumnIndex(StopTimes.StopTimeColumns.DEPARTURE_TIME)),
                        cursor.getInt(cursor.getColumnIndex(StopTimes.StopTimeColumns.STOP_ID)),
                        cursor.getString(cursor.getColumnIndex(StopTimes.StopTimeColumns.STOP_SEQUENCE))
                        ) {
                };
                data.add(item);
            }
        }

        return data;
    }


    /**
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