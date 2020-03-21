package com.paper.squeeze.covd_19;

public class LocationData {

    private String usertype;
    private String condition;
    private double lat;
    private double lng;

    public LocationData(String usertype, String condition, double lat, double lng) {
        this.usertype = usertype;
        this.condition = condition;
        this.lat = lat;
        this.lng = lng;
    }

    public String getUsertype() {
        return usertype;
    }

    public String getCondition() {
        return condition;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}

