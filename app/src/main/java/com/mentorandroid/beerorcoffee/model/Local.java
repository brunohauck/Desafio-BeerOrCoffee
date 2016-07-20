package com.mentorandroid.beerorcoffee.model;

/**
 * Created by brunodelhferreira on 18/07/16.
 */
public class Local {

    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public Integer beverage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getBeverage() {
        return beverage;
    }

    public void setBeverage(Integer beverage) {
        this.beverage = beverage;
    }
}
