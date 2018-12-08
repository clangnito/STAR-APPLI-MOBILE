package com.example.constantlangnito.starv1dl;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
    }

    public ListView listViewBusMetro;
    DatabaseHelper db;
    private RecyclerView lv;
    RecyclerView.Adapter adapter ;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lister_bus_metro);

        db = new DatabaseHelper(this);

        ArrayList list = db.getBusRoutesFromDatabase();

        lv = findViewById(R.id.recycler_viewBusMetro);
        lv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lv.setLayoutManager(layoutManager);
        adapter = new BusListAdapter(list);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BusListAdapter) adapter).setOnClickListener(new BusListAdapter.BusListItemClickListener() {
            @Override
            public void onItemClick(int possition, View view) {
                Log.d("itemBus", "");
            }
        });
    }

}
