package com.dimas.rumahmakan.model.responseModel;

import com.google.gson.annotations.SerializedName;

public class ResponseModel<T> {
    @SerializedName("data")
    public T Data;

    @SerializedName("error")
    public String Error;
}
