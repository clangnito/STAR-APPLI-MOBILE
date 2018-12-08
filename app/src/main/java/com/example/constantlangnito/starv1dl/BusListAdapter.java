package com.example.constantlangnito.starv1dl;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.constantlangnito.starv1dl.Table.BusRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.BusListViewHolder> {

    private List<BusRoute> routes;
    ArrayAdapter<String> busDirections;
    String splitor = "<>";
    private BusListItemClickListener busListItemClickListener;

    public BusListAdapter(List<BusRoute> routes) {
        this.routes = routes;
    }

    public void setOnClickListener(BusListItemClickListener busListItemClickListener) {

        this.busListItemClickListener = busListItemClickListener;
    }

    @NonNull
    @Override
    public BusListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.line, viewGroup, false);
        return new BusListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusListViewHolder busListViewHolder, int position) {
        BusRoute busRoute = routes.get(position);
        busListViewHolder.numLigne.setText(busRoute.getShortName());
        busListViewHolder.numLigne.setTextColor(Color.parseColor("#" + busRoute.getTextColor()));
        busListViewHolder.numLigne.setBackgroundColor(Color.parseColor("#" + busRoute.getColor()));
        busDirections.addAll(getDirections(busRoute.getLongName()));
        busDirections.notifyDataSetChanged();
        //busListViewHolder.direction2.setText((routes.get(i).getLongName().split("<>").length>1)?"<<"+routes.get(i).getLongName().split("<>")[1] : "");
    }


    @Override
    public int getItemCount() {
        return routes.size();
    }

    public interface BusListItemClickListener {
        void onItemClick(int possition, View view);
    }


    public class BusListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView numLigne;
        protected Spinner directionsSP;


        public BusListViewHolder(@NonNull View itemView) {
            super(itemView);
            numLigne = itemView.findViewById(R.id.numLigne);
            directionsSP = itemView.findViewById(R.id.directions);
            busDirections = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item);
            busDirections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            directionsSP.setAdapter(busDirections);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            busListItemClickListener.onItemClick(getLayoutPosition(), v);
        }
    }

    public ArrayList<String> getDirections(@NonNull String allDirections) {
        String[] directions = allDirections.split(splitor);
        ArrayList<String> directionsList = new ArrayList<>();
        directionsList.addAll(Arrays.asList(directions));
        return directionsList;
    }
}

