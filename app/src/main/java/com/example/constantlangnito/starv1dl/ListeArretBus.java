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

import java.util.ArrayList;
import java.util.List;

public class ListeArretBus extends AppCompatActivity {

    public ListView arretBus;
    DatabaseManager db;
    private RecyclerView lv;
    RecyclerView.Adapter adapter ;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_arret_bus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle vals = getIntent().getExtras();
        String dateDepart = vals.getString("dateDepart");
        String heureDepart = vals.getString("heureDepart");
        int positionBusSelect = vals.getInt("positionBusSelect");
        int positionDirectionSelect = vals.getInt("positionDirectionSelect");

        db = new DatabaseManager(this,"");

        ArrayList<BusRoute> listeBusDataBase = db.getBusRoutesFromDatabase();

        BusRoute busRoute = listeBusDataBase.get(positionBusSelect);

        String idBus = busRoute.getRoute_id();

        ArrayList list = db.getArretBusForBusDatabase(idBus,Integer.toString(positionDirectionSelect) );

        lv = findViewById(R.id.recycler_viewListeArretBus);
        lv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lv.setLayoutManager(layoutManager);
        adapter = new ArretBusAdapter(list);
        lv.setAdapter(adapter);
    }



}
