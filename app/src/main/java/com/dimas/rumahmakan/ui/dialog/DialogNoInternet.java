package com.dimas.rumahmakan.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.util.Unit;

// ini adalah class untuk menampilkan
// dialog tentang status koneksi internet
// preangkat user
public class DialogNoInternet {

    // deklarasi konteks
    private Context context;

    // deklarasi callback
    private Unit<Boolean> onOk;

    // konstruktor 2 parameter
    public DialogNoInternet(Context context, Unit<Boolean> onOk) {

        // inisialisai conteks
        this.context = context;

        // inisialisai callback
        this.onOk = onOk;
    }

    // fungsi untuk menampilkan dialog
    public void show(){

        // mengambil view dari inflater
        View v = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_no_internet,null);

        // menyiapkan instance dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .create();

        // menyiapkan tombol ok
        Button ok = v.findViewById(R.id.button_ok);

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
