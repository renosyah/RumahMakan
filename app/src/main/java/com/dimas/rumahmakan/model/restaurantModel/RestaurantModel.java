package com.dimas.rumahmakan.model.restaurantModel;

import com.dimas.rumahmakan.model.BaseModel;
import com.google.gson.annotations.SerializedName;
import com.here.sdk.core.GeoCoordinates;

public class RestaurantModel extends BaseModel {

    @SerializedName("id")
    public int Id;

    @SerializedName("nama")
    public String Name;

    @SerializedName("url_menu")
    public String UrlMenu;

    @SerializedName("alamat")
    public String Address;

    @SerializedName("deskripsi")
    public String Description;

    @SerializedName("latitude")
    public double Latitude;

    @SerializedName("longitude")
    public double Longitude;

    @SerializedName("url_gambar")
    public String UrlImage;

    public double Distance = 1.0;

    public RestaurantModel() {
        super();
    }

    public RestaurantModel(int id) {
        Id = id;
    }

    public RestaurantModel(int id, String name, String address, double distance) {
        Id = id;
        Name = name;
        Address = address;
        Distance = distance;
    }

    public RestaurantModel(int id, String name, String urlMenu, String address, String description, double latitude, double longitude, String urlGambar) {
        Id = id;
        Name = name;
        UrlMenu = urlMenu;
        Address = address;
        Description = description;
        Latitude = latitude;
        Longitude = longitude;
        UrlImage = urlGambar;
    }

    public double calculateDistance(GeoCoordinates userLocation){
        if (userLocation == null){
            return 1.0;
        }
        double dist = Math.sin(Math.toRadians(userLocation.latitude)) * Math.sin(Math.toRadians(this.Latitude)) + Math.cos(Math.toRadians(userLocation.latitude)) * Math.cos(Math.toRadians(this.Latitude)) * Math.cos(Math.toRadians(userLocation.longitude - this.Longitude));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344; // for KM
        return dist;
    }
}
