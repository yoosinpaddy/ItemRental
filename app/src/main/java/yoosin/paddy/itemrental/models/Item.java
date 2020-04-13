package yoosin.paddy.itemrental.models;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;

import yoosin.paddy.itemrental.sharedPreferenceManager.SharedPref;

public class Item implements Serializable {
    private static final String TAG = "Item";
    String id;
    String longitude="0.0";
    String latitude="0.0";
    String ownerId;
    String name;
    String description;
    String photoUrl;
    String price;
    String distance;
    int votes=0;

    public Item(String id, String longitude, String latitude, String ownerId, String name, String description, String photoUrl, String price) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.photoUrl = photoUrl;
        this.price = price;
    }
    public double getDistance(Context context) {
//        if (latitude.contentEquals(""))
        double lat1=Double.parseDouble(latitude.trim());
        double lon1 = Double.parseDouble(longitude.trim());
        double lat2 = SharedPref.getMyLocation(context)[0];
        double lon2 = SharedPref.getMyLocation(context)[1];
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        Log.e(TAG, "getDistance lat1: "+lat1 );
        Log.e(TAG, "getDistance long1: "+lon1 );
        Log.e(TAG, "getDistance mylat: "+lat2 );
        Log.e(TAG, "getDistance my long: "+lon2 );
        Log.e(TAG, "getDistance: "+dist );
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public Item() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
