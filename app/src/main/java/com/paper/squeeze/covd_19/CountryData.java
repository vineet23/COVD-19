package com.paper.squeeze.covd_19;

public class CountryData {

    private String name;
    private int totalConfirmed;
    private int totalDeaths;
    private int totalRecovered;
    private double lat;
    private double lng;
    private String country;
    private boolean isSub;

    public CountryData(String name, int totalConfirmed, int totalDeaths, int totalRecovered, double lat, double lng, String country,boolean isSub) {
        this.name = name;
        this.totalConfirmed = totalConfirmed;
        this.totalDeaths = totalDeaths;
        this.totalRecovered = totalRecovered;
        this.lat = lat;
        this.lng = lng;
        this.country = country;
        this.isSub = isSub;
    }

    public boolean isSub(){
        return this.isSub;
    }

    public String getName() {
        return name;
    }

    public int getTotalConfirmed() {
        return totalConfirmed;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getTotalRecovered() {
        return totalRecovered;
    }

    public int getTotalActive(){
        return this.totalConfirmed-this.totalDeaths-this.totalRecovered;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getCountry() {
        return country;
    }

}
