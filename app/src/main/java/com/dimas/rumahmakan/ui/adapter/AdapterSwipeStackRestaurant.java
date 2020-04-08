package com.dimas.rumahmakan.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.util.Unit;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// ini adalah class adapter
// untuk menampilkan daftar data-data
// wisata kuliner dalam bentuk list
// yg nantinya akan digunakan sebagai
// value adapater untuk Swipe Stack
public class AdapterSwipeStackRestaurant extends BaseAdapter {

    // deklarasi variabel konteks
    private Context context;

    // deklarasi data array wisata kuliner
    private ArrayList<RestaurantModel> list;

    // deklarasi callback
    private Unit<RestaurantModel> onClick;

    // konstruktor dengan 3 parameter
    public AdapterSwipeStackRestaurant(Context context, ArrayList<RestaurantModel> list, Unit<RestaurantModel> onClick) {

        // inisialisasi konteks
        this.context = context;

        // inisialisasi data array wisata kuliner
        this.list = list;

        // inisialisasi callback
        this.onClick = onClick;
    }

    // ini adalah fungsi yg akan dipanggil
    // untuk menetukan panjang list recycleview
    // yg akan ditampilkan
    @Override
    public int getCount() {
        return list.size();
    }

    // ini adalah fungsi yg akan dipanggil
    // untuk mendapatkan item berdasarkan posisi
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    // ini adalah fungsi yg akan dipanggil
    // untuk mendapatkan item id berdasarkan posisi
    // untuk saat ini kegunaanya masih belum diketahui
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ini adalah fungsi yg akan dipanggil
    // untuk menetukan view apa yg akan digunakan
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // inflate view dari resources
        convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.adapter_swipe_stack_restaurant,parent,false);

        // mengambil data berdasarkan posisi
        RestaurantModel item = list.get(position);

        // deklarasi dan inisialisasi image
        // yg diambil dari convert view
        ImageView image = convertView.findViewById(R.id.restaurant_image);

        // inisialisasi instance picasso
        // untuk mengambil gambar dari
        // url dan meletakkannya kedalam
        // view image
        Picasso.get()
                .load(item.UrlImage)
                .into(image);

        // deklarasi dan inisialisasi name
        // yg diambil dari convert view
        TextView name = convertView.findViewById(R.id.restaurant_name);

        // lalu mengeset value untuk textnya yg diambil dari
        // item yg memiliki variabel name
        name.setText(item.Name);

        // deklarasi dan inisialisasi address
        // yg diambil dari convert view
        TextView address = convertView.findViewById(R.id.restaurant_address);

        // lalu mengeset value untuk textnya yg diambil dari
        // item yg memiliki variabel address
        address.setText(item.Address + " (" + String.format("%.1f", item.Distance) +" Km)");

        // deklarasi dan inisialisasi detail
        // yg diambil dari convert view
        Button detail = convertView.findViewById(R.id.button_detail);

        // saat tombol detail ditekan
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // invoke callback
                onClick.invoke(item);
            }
        });

        // kembalikan convertview
        // sebagai hasil
        return convertView;
    }
}
