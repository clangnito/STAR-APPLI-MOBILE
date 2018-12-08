package com.example.constantlangnito.starv1dl;

/**
 * Created by langnito on 02/12/2018.
 */

public class VariablesStatic implements StarContract {

    public static String INIT_FOLDER_PATH = "star1DL/"; // From the root folder of the device
    public static String DOWNLOAD_PATH = "GTFS_2018.3.0.2_2018-11-26_2018-12-23";

    public static String CALENDAR_CSV_FILE = "calendar.txt";
    public static String BUS_ROUTES_CSV_FILE = "routes.txt";
    public static String STOPS_CSV_FILE = "stops.txt";
    public static String STOP_TIMES_CSV_FILE = "stop_times.txt";
    public static final String STOP_TIMES_SPLIT_CSV_FILE = "stop_times_";
    public static String TRIPS_CSV_FILE = "trips.txt";

    public static final String CREATE_T_BUS_ROUTE_TABLE = "CREATE TABLE IF NOT EXISTS " + BusRoutes.CONTENT_PATH +
            "(_id INTEGER NOT NULL PRIMARY KEY," +
            "route_id TEXT, " +
            BusRoutes.BusRouteColumns.SHORT_NAME + " TEXT, " +
            BusRoutes.BusRouteColumns.LONG_NAME + " TEXT, " +
            BusRoutes.BusRouteColumns.DESCRIPTION + " TEXT, " +
            BusRoutes.BusRouteColumns.TYPE + " TEXT," +
            BusRoutes.BusRouteColumns.COLOR + " TEXT, " +
            BusRoutes.BusRouteColumns.TEXT_COLOR + " TEXT );";


    public static final String CREATE_T_TRIPS_TABLE = "CREATE TABLE IF NOT EXISTS " + Trips.CONTENT_PATH +
            "(_id INTEGER NOT NULL PRIMARY KEY," +
            Trips.TripColumns.ROUTE_ID + " INTEGER," +
            Trips.TripColumns.SERVICE_ID + " INTEGER," +
            Trips.TripColumns.HEADSIGN + " TEXT," +
            Trips.TripColumns.DIRECTION_ID + " INTEGER," +
            Trips.TripColumns.BLOCK_ID + " TEXT," +
            Trips.TripColumns.WHEELCHAIR_ACCESSIBLE + " TEXT );";

    public static final String CREATE_T_STOPS_TABLE = "CREATE TABLE IF NOT EXISTS " + Stops.CONTENT_PATH +
            "(_id INTEGER NOT NULL PRIMARY KEY," +
            Stops.StopColumns.NAME + " TEXT," +
            Stops.StopColumns.DESCRIPTION + " TEXT," +
            Stops.StopColumns.LATITUDE + " INTEGER, " +
            Stops.StopColumns.LONGITUDE + " INTEGER," +
            Stops.StopColumns.WHEELCHAIR_BOARDING + " TEXT );";

    public static final String CREATE_STOP_TIMES_TABLE = "CREATE TABLE IF NOT EXISTS " + StarContract.StopTimes.CONTENT_PATH +
            "(" + StarContract.StopTimes.StopTimeColumns.TRIP_ID + " INTEGER," +
            StarContract.StopTimes.StopTimeColumns.ARRIVAL_TIME + " TEXT," +
            StarContract.StopTimes.StopTimeColumns.DEPARTURE_TIME + " TEXT," +
            StarContract.StopTimes.StopTimeColumns.STOP_ID + " INTEGER," +
            StarContract.StopTimes.StopTimeColumns.STOP_SEQUENCE + " TEXT );";

    public static final String CREATE_T_CALENDAR_TABLE = "CREATE TABLE IF NOT EXISTS " + Calendar.CONTENT_PATH +
            "(_id INTEGER NOT NULL PRIMARY KEY" +
            Calendar.CalendarColumns.MONDAY + " TEXT," +
            Calendar.CalendarColumns.TUESDAY + " TEXT," +
            Calendar.CalendarColumns.WEDNESDAY + " TEXT," +
            Calendar.CalendarColumns.THURSDAY + " TEXT," +
            Calendar.CalendarColumns.FRIDAY + " TEXT," +
            Calendar.CalendarColumns.SATURDAY + " TEXT," +
            Calendar.CalendarColumns.SUNDAY + " TEXT," +
            Calendar.CalendarColumns.START_DATE + " TEXT," +
            Calendar.CalendarColumns.END_DATE + " TEXT" +
            " );";

    /**
     * Gestion des versions
     */
    public static final String VERSIONS_TABLE = "versions";
    public static final String VERSIONS_FILE_NAME_COL = "file_name";
    public static final String VERSIONS_FILE_VERSION_COL = "file_version";
    public static final String CREATE_VERSIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + VERSIONS_TABLE +
            "( " + VERSIONS_FILE_NAME_COL + " TEXT , " +
            VERSIONS_FILE_VERSION_COL + " TEXT" +
            " );";

    /**
     */
    public static final String DEFAULT_FST_VERSION = "0001";

    public static String DATA_SOURCE_URL = "https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-busmetro-horaires-gtfs-versions-td&sort=-debutvalidite";
}
