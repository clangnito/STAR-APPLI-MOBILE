package com.example.constantlangnito.starv1dl.Table;

/**
 * Created by langnito on 02/12/2018.
 */

public class BusRoute {


//route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color,route_sort_order
    private String route_id;
    private String route_short_name;
    private String route_long_name;
    private String route_desc;
    private String type;
    private String color;
    private String textColor;

    public BusRoute(String route_id, String route_short_name, String route_long_name, String route_desc, String type, String color, String textColor) {
        this.route_id = route_id;
        this.route_short_name = route_short_name;
        this.route_long_name = route_long_name;
        this.route_desc = route_desc;
        this.type = type;
        this.color = color;
        this.textColor = textColor;
    }


    public String getshortName() {
        return route_short_name;
    }

    public void setshortName(String shortName) {
        this.route_short_name = shortName;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getRoute_long_name() {
        return route_long_name;
    }

    public void setRoute_long_name(String route_long_name) {
        this.route_long_name = route_long_name;
    }

    public String getRoute_desc() {
        return route_desc;
    }

    public void setRoute_desc(String route_desc) {
        this.route_desc = route_desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    @Override
    public String toString() {
        return "BusRoute{" +
                "route_id:'" + route_id + '\'' +
                ", route_short_name:'" + route_short_name + '\'' +
                ", route_long_name:'" + route_long_name + '\'' +
                ", route_desc:'" + route_desc + '\'' +
                ", type:'" + type + '\'' +
                ", color:'" + color + '\'' +
                ", textColor:'" + textColor + '\'' +
                '}';
    }

}
