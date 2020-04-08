package com.dimas.rumahmakan.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.cityModel.CityModel;

import java.util.ArrayList;


// ini adalah class adapter
// untuk menampilkan daftar data-data
// kota dalam bentuk list
// yg nantinya akan digunakan sebagai
// value adapater untuk recycleview
public class AdapterCityModel extends RecyclerView.Adapter<AdapterCityModel.Holder> {

    // deklarasi variabel konteks
    private Context context;

    // deklarasi data array kota
    private ArrayList<CityModel> list;

    // konstruktor dengan 2 parameter
    public AdapterCityModel(Context context, ArrayList<CityModel> list) {

        // inisialisasi konteks
        this.context = context;

        // inisialisasi aray data kota
        this.list = list;
    }



    // ini adalah fungsi yg akan dipanggil
    // untuk menetukan view apa yg akan digunakan
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate view dari resources
        return new Holder(((Activity)context).getLayoutInflater().inflate(R.layout.adapter_city_model,parent,false));

    }

    // ini adalah fungsi yg akan dipanggil
    // untuk menetukan value dari view yg telah diinflate
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        // mengambil data berdasarkan posisi
        CityModel item = list.get(position);

        // mengeset text nama dari
        // varibel nama dari item
        holder.name.setText(item.Name);

        // mengeset text total dari
        // varibel total dari item
        holder.total.setText(item.Total + " "+ context.getString(R.string.app_name));
    }


    // ini adalah fungsi yg akan dipanggil
    // untuk menetukan panjang list recycleview
    // yg akan ditampilkan
    @Override
    public int getItemCount() {
        return list.size();
    }


    // ini adalah static class
    // untuk menampung view view
    public static class Holder extends RecyclerView.ViewHolder{

        // deklarasi text nama
        public TextView name;

        // deklarasi text total
        public TextView total;

        // konstruktor dengan satu parameter
        public Holder(@NonNull View itemView) {
            super(itemView);

            // inisialisasi nama
            this.name = itemView.findViewById(R.id.city_name);

            // inisialisasi total
            this.total = itemView.findViewById(R.id.total);
        }
    }
}
