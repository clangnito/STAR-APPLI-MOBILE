package com.example.constantlangnito.starv1dl;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.constantlangnito.starv1dl.Table.Stop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArretBusAdapter extends RecyclerView.Adapter<ArretBusAdapter.ArretBusViewHolder> {

    private List<Stop> stop;
    Set<String> mId;
    private ArretBusItemClickListener arretBusItemClickListener;

    public ArretBusAdapter(List<Stop> stop) {
        this.stop = stop;
    }

    public void setOnClickListener(ArretBusItemClickListener arretBusItemClickListener) {

        this.arretBusItemClickListener = arretBusItemClickListener;
    }

    @NonNull
    @Override
    public ArretBusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ligne_arret_bus, viewGroup, false);
        return new ArretBusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArretBusViewHolder arretBusViewHolder, int position) {
        final Stop stopB = stop.get(position);
        arretBusViewHolder.arretBus.setText(stopB.getName());
    }


    @Override
    public int getItemCount() {
        return stop.size();
    }

    public interface ArretBusItemClickListener {
        void onItemClick(int possition, View view);
    }


    public class ArretBusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView arretBus;


        public ArretBusViewHolder(@NonNull View itemView) {
            super(itemView);
            mId = new HashSet<String>();
            arretBus = itemView.findViewById(R.id.arretBus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            arretBusItemClickListener.onItemClick(getLayoutPosition(), v);
        }
    }


}

