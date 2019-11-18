package com.example.constantlangnito.starv1dl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.constantlangnito.starv1dl.Table.BusRoute;
import com.example.constantlangnito.starv1dl.Table.Stop;

import java.util.ArrayList;
import java.util.List;

public class ListeArretBus extends AppCompatActivity {

    public ListView arretBus;
    DatabaseManager db;
    private RecyclerView lv;
    RecyclerView.Adapter adapter ;
    RecyclerView.LayoutManager layoutManager;

    String dateDepart ;
    String heureDepart ;
    int positionBusSelect ;
    int positionDirectionSelect ;
    String Route_id;
    ArrayList<BusRoute> listeBusDataBase;
    ArrayList listArretBus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_arret_bus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle vals = getIntent().getExtras();
        dateDepart = vals.getString("dateDepart");
        heureDepart = vals.getString("heureDepart");
        positionBusSelect = vals.getInt("positionBusSelect");
        positionDirectionSelect = vals.getInt("positionDirectionSelect");

        db = new DatabaseManager(this,"");

        listeBusDataBase = db.getBusRoutesFromDatabase();

        BusRoute busRoute = listeBusDataBase.get(positionBusSelect);

        String idBus = busRoute.getRoute_id();
        String shortName = busRoute.getshortName();
        String Route_desc = busRoute.getRoute_long_name();

        String[] splits;
        splits = Route_desc.split("<>");

        setTitle("Bus : "+ shortName + " -> " + splits[positionDirectionSelect]);
        Route_id = idBus;
        listArretBus = db.getArretBusForBusDatabase(idBus,Integer.toString(positionDirectionSelect) );

        lv = findViewById(R.id.recycler_viewListeArretBus);
        lv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lv.setLayoutManager(layoutManager);
        adapter = new ArretBusAdapter(listArretBus);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((ArretBusAdapter) adapter).setOnClickListener(new ArretBusAdapter.ArretBusItemClickListener() {
            @Override
            public void onItemClick(int possition, View view) {
                Toast.makeText(ListeArretBus.this, "Selectionnez une ligne "+possition, Toast.LENGTH_SHORT).show();

                String ArretBus_ID = ((Stop) listArretBus.get(possition)).getId();
                String ArretBus = ((Stop) listArretBus.get(possition)).getName();


                Intent intent = new Intent(ListeArretBus.this, fragment3.class);
                Bundle vals = new Bundle();
                vals.putString("dateDepart",dateDepart);
                vals.putString("heureDepart",heureDepart);
                vals.putInt("positionBusSelect",positionBusSelect);
                vals.putInt("positionDirectionSelect", positionDirectionSelect);
                vals.putInt("positionArretSelect", possition);
                vals.putString("ArretBus_ID", ArretBus_ID );
                vals.putString("ArretBus", ArretBus );
                vals.putString("Route_id", Route_id);
                intent.putExtras(vals);
                startActivity(intent);
            }
        });
    }


}
