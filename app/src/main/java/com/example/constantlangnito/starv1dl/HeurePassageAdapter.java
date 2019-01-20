package com.example.constantlangnito.starv1dl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.constantlangnito.starv1dl.Table.Stop;
import com.example.constantlangnito.starv1dl.Table.StopTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeurePassageAdapter extends RecyclerView.Adapter<HeurePassageAdapter.ViewHolder> {

    private List<StopTime> stopTime;
    Set<String> mId;

    private HeurePassageItemClickListener heurePassageItemClickListener;

    public HeurePassageAdapter(List<StopTime> stopTime) {
        this.stopTime = stopTime;
    }

    public void setOnClickListener(HeurePassageItemClickListener heurePassageItemClickListener) {

        this.heurePassageItemClickListener = heurePassageItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.heurepassagearret, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final StopTime stoptimeB = stopTime.get(position);
        viewHolder.heurePassage.setText(stoptimeB.getDeparture_time());
    }


    @Override
    public int getItemCount() {
        return stopTime.size();
    }

    public interface HeurePassageItemClickListener {
        void onItemClick(int possition, View view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView heurePassage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mId = new HashSet<String>();
            heurePassage = itemView.findViewById(R.id.heurepassage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            heurePassageItemClickListener.onItemClick(getLayoutPosition(), v);
        }
    }


}

