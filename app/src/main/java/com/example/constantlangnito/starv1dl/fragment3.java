package com.example.constantlangnito.starv1dl;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.constantlangnito.starv1dl.Table.BusRoute;
import com.example.constantlangnito.starv1dl.Table.StopTime;

import java.util.ArrayList;

public class fragment3 extends AppCompatActivity {

    public ListView heurePassage;
    DatabaseManager db;
    private RecyclerView lv;
    RecyclerView.Adapter adapter ;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Bundle vals = getIntent().getExtras();
        String dateDepart = vals.getString("dateDepart");
        String heureDepart = vals.getString("heureDepart");
        int positionBusSelect = vals.getInt("positionBusSelect");
        int positionDirectionSelect = vals.getInt("positionDirectionSelect");
        int positionArretSelect = vals.getInt("positionArretSelect");
        String Route_id = vals.getString("Route_id");
        String ArretBus_ID = vals.getString("ArretBus_ID");

        setTitle("Arret Bus : "+ vals.getString("ArretBus"));
        //setContentView(R.layout.main);

        db = new DatabaseManager(this,"");

        //ArrayList<StopTime> listeheurePassageDataBase = db.getHeurePassageFromDatabase();

        //StopTime stopTime = listeheurePassageDataBase.get(positionBusSelect);

        //String idBus = stopTime.getRoute_id();

        ArrayList list = db.getHeurePassageFromDatabase(dateDepart,heureDepart,positionBusSelect,positionDirectionSelect,positionArretSelect,Route_id,ArretBus_ID);

        lv = findViewById(R.id.recycler_viewHeurePassage);
        lv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lv.setLayoutManager(layoutManager);
        adapter = new HeurePassageAdapter(list);
        lv.setAdapter(adapter);
    }


}
