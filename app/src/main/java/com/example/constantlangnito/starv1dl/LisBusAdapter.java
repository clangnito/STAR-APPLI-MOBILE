package com.example.constantlangnito.starv1dl;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.constantlangnito.starv1dl.Table.BusRoute;

import java.util.List;

/**
 * Created by akoumare on 20/01/2019.
 */

class LisBusAdapter extends ArrayAdapter<BusRoute> {
    List<BusRoute> mRoutes;
    Context mContext;

    public LisBusAdapter(Context context, int textViewResourceId, List<BusRoute> route){
        super(context, textViewResourceId, route);
        this.mRoutes = route;
        this.mContext = context;
    }


    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_line, parent, false);
        final TextView label = row.findViewById(R.id.label);
        label.setText(mRoutes.get(position).getshortName());
        label.setTextColor(Color.parseColor("#" + mRoutes.get(position).getTextColor()));
       label.setBackgroundColor(Color.parseColor("#" + mRoutes.get(position).getColor()));
        final TextView line = row.findViewById(R.id.line);
        line.setText(mRoutes.get(position).getRoute_long_name());
        return row;
    }
}
