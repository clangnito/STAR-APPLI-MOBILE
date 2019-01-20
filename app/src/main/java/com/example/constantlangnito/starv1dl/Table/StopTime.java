package com.example.constantlangnito.starv1dl.Table;

/**
 * Created by langnito on 02/12/2018.
 */

public class StopTime {

    //trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled
    private int trip_id;
    private String arrival_time;
    private String departure_time;
    private int stop_id;
    private String stop_sequence;

    public StopTime(int trip_id, String arrival_time, String departure_time, int stop_id, String stopsequence) {
        this.trip_id = trip_id;
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.stop_id = stop_id;
        stop_sequence = stopsequence;
    }

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public int getStop_id() {
        return stop_id;
    }

    public void setStop_id(int stop_id) {
        this.stop_id = stop_id;
    }

    public String getStop_sequence() {
        return stop_sequence;
    }

    public void setStop_sequence(String stop_sequence) {
        this.stop_sequence = stop_sequence;
    }

    @Override
    public String toString() {
        return "StopTime{" +
                "trip_id:" + trip_id +
                ", arrival_time:'" + arrival_time + '\'' +
                ", departure_time:'" + departure_time + '\'' +
                ", stop_id:" + stop_id +
                ", stop_sequence:'" + stop_sequence + '\'' +
                '}';
    }

    public String sql() {
        return "("+ trip_id +",'"+ arrival_time +"','"+ departure_time +"',"+ stop_id +",'"+ stop_sequence +"')";
    }
}
