package com.example.constantlangnito.starv1dl;

import android.content.Context;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.BusListViewHolder> {

    private List<BusRoute> routes;
    ArrayAdapter<String> busDirections;
    String splitor = "<>";
    Set<String> mId;
    Context mContext;
    private BusListItemClickListener busListItemClickListener;

    public BusListAdapter(@NonNull Context context,final List<BusRoute> routes) {
        this.routes = routes;
        this.mContext = context;
    }

    public void setOnClickListener(BusListItemClickListener busListItemClickListener) {

        this.busListItemClickListener = busListItemClickListener;
    }

    @NonNull
    @Override
    public BusListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.line, viewGroup, false);
        return new BusListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusListViewHolder busListViewHolder, int position) {
        final BusRoute busRoute = routes != null ? routes.get(busListViewHolder.getAdapterPosition()) : null;
        if (busRoute != null) {
            busListViewHolder.numLigneTV.setText(busRoute.getShortName());
            busListViewHolder.numLigneTV.setTextColor(Color.parseColor("#" + busRoute.getTextColor()));
            busListViewHolder.numLigneTV.setBackgroundColor(Color.parseColor("#" + busRoute.getColor()));
            busDirections.clear();
            busDirections.addAll(getDirections(busRoute.getLongName()));
            Log.e("direction",busRoute.getLongName());
            busDirections.notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        return routes != null ? routes.size() : 0;
    }

    public interface BusListItemClickListener {
        void onItemClick(int possition, View view);
    }


    public class BusListViewHolder extends RecyclerView.ViewHolder {
        protected TextView numLigneTV;
        protected Spinner directionsSP;


        public BusListViewHolder(@NonNull View itemView) {
            super(itemView);
            mId = new HashSet<>();
            numLigneTV = itemView.findViewById(R.id.numLigne);
            directionsSP = itemView.findViewById(R.id.directions);
            busDirections = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item);
            busDirections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            directionsSP.setAdapter(busDirections);
        }
    }

    public ArrayList<String> getDirections(@NonNull String allDirections) {
        String[] directions = allDirections.split(splitor);
        ArrayList<String> directionsList = new ArrayList<>();
        directionsList.addAll(Arrays.asList(directions));
        return directionsList;
    }
}

