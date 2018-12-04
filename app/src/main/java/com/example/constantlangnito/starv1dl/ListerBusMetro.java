package com.example.constantlangnito.starv1dl;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class ListerBusMetro extends AppCompatActivity {

    //private static CustomAdapter adapter;

    protected void onCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lister_bus_metro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public ListView listViewBusMetro;
    public SimpleCursorAdapter simpleCursorAdapter;
    static final String[] FROM = {StarContract.BusRoutes.BusRouteColumns.SHORT_NAME, StarContract.BusRoutes.BusRouteColumns.LONG_NAME, StarContract.BusRoutes.BusRouteColumns.DESCRIPTION};
    static final int[] TO = {R.id.numLigne, R.id.direction1, R.id.direction2};
    DatabaseHelper db;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lister_bus_metro);


        /*listViewBusMetro = (ListView) findViewById(R.id.listViewBusMetro);



        ArrayAdapter arrayAdapter = new ArrayAdapter(this,)*/

        db = new DatabaseHelper(this);

        ArrayList list = db.getBusRoutesFromDatabase();

        lv = (ListView) findViewById(R.id.listViewBusMetro);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).
        ArrayList<String> your_array_list = new ArrayList<String>();
        your_array_list.add("foo");
        your_array_list.add("bar");

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                your_array_list );

        lv.setAdapter(arrayAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        /*Cursor c =  d.getBusRoutesFromDatabase();
        //db.getReadableDatabase().query(StarContract.BusRoutes.CONTENT_PATH,null,null,null,null,null,null);
        startManagingCursor(c);

        simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.line,c,FROM,TO);
        listViewBusMetro.setAdapter(simpleCursorAdapter);*/
    }

}
