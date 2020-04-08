package com.dimas.rumahmakan.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.util.Unit;

// ini adalah class untuk menampilkan
// dialog tentang detail wisata kuliner
public class DialogDetailRestaurant {

    // deklarasi konteks
    private Context context;

    // deklarasi data wisata kuliner
    private RestaurantModel item;

    // deklarasi callback
    private Unit<Boolean> onOk;

    // konstruktor 3 parameter
    public DialogDetailRestaurant(Context context, RestaurantModel item, Unit<Boolean> onOk) {

        // inisialisai conteks
        this.context = context;

        // inisialisai data wisata kuliner
        this.item = item;

        // inisialisai callback
        this.onOk = onOk;
    }

    // fungsi untuk menampilkan dialog
    public void show(){

        // mengambil view dari inflater
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_detail_restaurant,null);

        // menyiapkan instance dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .create();

        // menyiapkan variabel text untuk nama
        TextView name = v.findViewById(R.id.restaurant_name_text);

        // tampilkan tulisan nama wisata kuliner pada teks
        name.setText(item.Name);

        // menyiapkan variabel text untuk alamat
        TextView address = v.findViewById(R.id.restaurant_address_text);

        // tampilkan tulisan alamat wisata kuliner pada teks
        // serta jarak dalam km dengan menggunakan string formating
        // untuk menyederhanakan angkanya
        address.setText(item.Address + " (" + String.format("%.1f", item.Distance) +" Km)");

        // menyiapkan tombol ok
        Button ok = v.findViewById(R.id.button_detail);

        // saat tombol ok di klik
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // maka akan menginvoke callback
                onOk.invoke(true);

                // dan akan menutup dialog
                dialog.dismiss();
            }
        });

        // set view dialog dengan
        // view yg telah diambil dari inflate tadi
        dialog.setView(v);

        // menghalangi dialog agar tidak tutup
        dialog.setCancelable(false);

        // buat dialog tanpa menampilkan judul
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // jika jendela dialog tidak null
        // maka set background dialog menjadi transparan
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // tampilkan dialog
        dialog.show();
    }
}
