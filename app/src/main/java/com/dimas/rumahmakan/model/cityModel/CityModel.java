package com.dimas.rumahmakan.model.cityModel;

import com.dimas.rumahmakan.model.BaseModel;
import com.google.gson.annotations.SerializedName;

public class CityModel extends BaseModel {

    @SerializedName("id")
    public int Id;

    @SerializedName("nama")
    public String Name;

    @SerializedName("total")
    public int Total;

    public CityModel(int id, String name, int total) {
        Id = id;
        Name = name;
        Total = total;
    }

}
