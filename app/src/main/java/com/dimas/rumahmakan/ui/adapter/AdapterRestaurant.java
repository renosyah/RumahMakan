package com.dimas.rumahmakan.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.util.Unit;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


// ini adalah class adapter
// untuk menampilkan daftar data-data
// wisata kuliner dalam bentuk list
// yg nantinya akan digunakan sebagai
// value adapater untuk recycleview
public class AdapterRestaurant extends RecyclerView.Adapter<AdapterRestaurant.Holder> {

    // deklarasi variabel konteks
    private Context context;

    // deklarasi data array wisata kuliner
    private ArrayList<RestaurantModel> list;

    // deklarasi callback
    private Unit<RestaurantModel> onClick;

    // konstruktor dengan 3 parameter
    public AdapterRestaurant(Context context, ArrayList<RestaurantModel> list, Unit<RestaurantModel> onClick) {

        // inisialisasi konteks
        this.context = context;

        // inisialisasi data array wisata kuliner
        this.list = list;

        // inisialisasi callback
        this.onClick = onClick;
    }


    // ini adalah fungsi yg akan dipanggil
    // untuk menetukan view apa yg akan digunakan
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate view dari resources
        return new Holder(((Activity)context).getLayoutInflater().inflate(R.layout.adapter_restaurant,parent,false));
    }


    // ini adalah fungsi yg akan dipanggil
    // untuk menetukan value dari view yg telah diinflate
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        // mengambil data berdasarkan posisi
        RestaurantModel item = list.get(position);

        // inisialisasi instance picasso
        // untuk mengambil gambar dari
        // url dan meletakkannya kedalam
        // view image
        Picasso.get()
                .load(item.UrlImage)
                .into(holder.image);

        // mengeset text nama dari
        // varibel nama dari item
        holder.name.setText(item.Name);

        // mengeset text distance dari
        // varibel distance dari item
        holder.distance.setText(String.format("%.1f", item.Distance) +" Km");

        // pada saat gambar diclik
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // invoke callback
                onClick.invoke(item);
            }
        });

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
    static class Holder extends RecyclerView.ViewHolder {

        // deklarasi text image
        public ImageView image;

        // deklarasi text nama
        public TextView name;

        // deklarasi text distance
        public TextView distance;

        // konstruktor dengan satu parameter
        public Holder(@NonNull View itemView) {
            super(itemView);

            // inisialisasi image
            image = itemView.findViewById(R.id.restaurant_image);

            // inisialisasi nama
            name = itemView.findViewById(R.id.restaurant_name_text);

            // inisialisasi distance
            distance= itemView.findViewById(R.id.restaurant_distance_text);
        }
    }
}
