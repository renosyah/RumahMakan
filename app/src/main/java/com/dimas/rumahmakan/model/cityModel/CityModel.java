package com.dimas.rumahmakan.model.cityModel;

import com.dimas.rumahmakan.model.BaseModel;
import com.google.gson.annotations.SerializedName;

// ini adalah class untuk model kota
// yg akan digunakan untuk mengelompokan
// jumlah wisata kuliner dari masing-masing
// kota atau wilayah
public class CityModel extends BaseModel {

    // variabel id
    // dengan nama untuk serialisasi
    @SerializedName("id")
    public int Id;

    // variabel nama
    // dengan nama untuk serialisasi
    @SerializedName("nama")
    public String Name;

    // variabel total
    // dengan nama untuk serialisasi
    @SerializedName("total")
    public int Total;

    // konstruktor dengan 3 parameter
    public CityModel(int id, String name, int total) {
        Id = id;
        Name = name;
        Total = total;
    }
}
