package com.dimas.rumahmakan.model.restaurantModel;

import com.dimas.rumahmakan.model.BaseModel;
import com.google.gson.annotations.SerializedName;
import com.here.sdk.core.GeoCoordinates;


// ini adalah class untuk model
// rumah makan atau wisata kuliner
public class RestaurantModel extends BaseModel {

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("id")
    public int Id;

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("nama")
    public String Name;

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("url_menu")
    public String UrlMenu;

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("alamat")
    public String Address;

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("deskripsi")
    public String Description;

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("latitude")
    public double Latitude;

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("longitude")
    public double Longitude;

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("url_gambar")
    public String UrlImage;

    // variabel id
    // dengan nama untuk serialisasi
    public double Distance = 1.0;

    // konstruktor standar
    public RestaurantModel() {
        super();
    }

    // konstruktor dengan
    // 1 parameter
    public RestaurantModel(int id) {
        Id = id;
    }

    // konstruktor dengan
    // 3 parameter
    public RestaurantModel(int id, String name, String address, double distance) {
        Id = id;
        Name = name;
        Address = address;
        Distance = distance;
    }

    // konstruktor dengan
    // banyak parameter
    // sesuai dengan jumlah
    // variabel di class ini
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

    // ini adalah fungsi yg digunakan
    // untuk mengkalkulasi jarak
    // antar lokasi user dan data dalam instance
    public double calculateDistance(GeoCoordinates userLocation){

        // jika lokasi user null
        if (userLocation == null){

            // balikan nilai 1.0
            return 1.0;
        }

        // menentukan jarak dalam meter
        double dist = Math.sin(Math.toRadians(userLocation.latitude)) * Math.sin(Math.toRadians(this.Latitude)) + Math.cos(Math.toRadians(userLocation.latitude)) * Math.cos(Math.toRadians(this.Latitude)) * Math.cos(Math.toRadians(userLocation.longitude - this.Longitude));

        // dengan menggunakan rumus matimtika acos
        dist = Math.acos(dist);

        // lalu dicari derajatnya
        dist = Math.toDegrees(dist);

        // kalikan dengan 60 dan diameter bumi
        dist = dist * 60 * 1.1515;

        // lalu ubah ke km
        dist = dist * 1.609344; // for KM

        // balikan hasil sebagai nilai
        return dist;
    }
}
