package com.example.constantlangnito.starv1dl;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.constantlangnito.starv1dl.Table.BusRoute;

import java.util.List;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.BusListViewHolder> {

    private List<BusRoute> routes;
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
    public void onBindViewHolder(@NonNull BusListViewHolder busListViewHolder, int i) {
        busListViewHolder.numLigne.setText(routes.get(i).getshortName());
        busListViewHolder.numLigne.setTextColor(Color.parseColor("#"+routes.get(i).getTextColor()));
        busListViewHolder.numLigne.setBackgroundColor(Color.parseColor("#"+routes.get(i).getColor()));
        busListViewHolder.direction1.setText(routes.get(i).getLongName());
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
        TextView numLigne, direction1, direction2;

        public BusListViewHolder(@NonNull View itemView) {
            super(itemView);
            numLigne = itemView.findViewById(R.id.numLigne);
            direction1 = itemView.findViewById(R.id.direction1);
            ///direction2 = itemView.findViewById(R.id.direction2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            busListItemClickListener.onItemClick(getLayoutPosition(), v);
        }
    }

}

